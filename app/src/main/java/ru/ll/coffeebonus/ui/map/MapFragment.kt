package ru.ll.coffeebonus.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.*
import com.yandex.mapkit.search.*
import com.yandex.mapkit.uri.UriObjectMetadata
import com.yandex.mapkit.user_location.UserLocationLayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentMapBinding
import ru.ll.coffeebonus.di.util.DrawableImageProvider
import ru.ll.coffeebonus.domain.CoffeeShop
import ru.ll.coffeebonus.ui.BaseFragment
import ru.ll.coffeebonus.ui.coffee.CoffeeFragment.Companion.ARG_COFFEESHOP
import timber.log.Timber

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding, MapViewModel>() {

    lateinit var mapObjects: MapObjectCollection
    var searchManager: SearchManager? = null
    var searchSession: Session? = null
    val searchListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            Timber.d("Ещё результаты ${searchSession!!.hasNextPage()}")
            if (searchSession!!.hasNextPage()) {
                searchSession!!.fetchNextPage(this)
            }
            viewModel.onSearchResult(
                response.collection.children.map {
                    CoffeeShop(
                        it.obj?.metadataContainer?.getItem(UriObjectMetadata::class.java)
                            ?.uris
                            ?.firstOrNull()
                            ?.value
                            ?.toUri()?.getQueryParameter("oid")!!,
                        it.obj?.name!!,
                        it.obj?.geometry?.first()?.point!!.longitude.toFloat(),
                        it.obj?.geometry?.first()?.point!!.latitude.toFloat()
                    )
                }
            )
        }

        override fun onSearchError(p0: com.yandex.runtime.Error) {
            showMessage("Error $p0")
            Timber.e("Error $p0")
        }
    }

    var userLocationLayer: UserLocationLayer? = null

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                Timber.d("Precise location access granted")
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                Timber.d("Only approximate location access granted")
            }
            else -> {
                // No location access granted.
                Timber.d("No location access granted")
            }
        }
    }

    val tapListener: GeoObjectTapListener = GeoObjectTapListener {
        true
    }

    val cameraListener = CameraListener { p0, p1, p2, p3 -> searchCoffee() }

    override val viewModel: MapViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMapBinding =
        FragmentMapBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
        SearchFactory.initialize(requireContext())
    }

    val placeMarkTapListener: MapObjectTapListener = MapObjectTapListener { placeMark, point ->
        showMessage("Нажата")
        binding.mapview.map.move(
            CameraPosition(
                point, 14.0f, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0.5f)
        ) {
            viewModel.mapClick(
                placeMark.userData as CoffeeShop
            )
        }
        true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("переменная из MapFragment ${viewModel.test} ")
        userLocationLayer = MapKitFactory.getInstance()
            .createUserLocationLayer(binding.mapview.mapWindow)
        userLocationLayer?.isVisible = true
        if (permission()) {
//            TODO: Подвинуть карту к местоположению юзера
            Timber.d("Двигаем карту ${userLocationLayer}")
            Timber.d("Двигаем карту ${userLocationLayer?.cameraPosition()}")
            Timber.d("Двигаем карту ${userLocationLayer?.cameraPosition()?.target}")
//            TODO: вытащить позицию пользователя
//            userLocationLayer?.setObjectListener()
            binding.mapview.map.move(
                CameraPosition(
                    userLocationLayer?.cameraPosition()?.target
                        ?: Point(59.938879, 30.315212),
                    15.0f, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0f),
                null
            )
        } else {
            binding.mapview.map.move(
                CameraPosition(
                    Point(59.938879, 30.315212), 15.0f, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0f),
                null
            )
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        binding.mapview.map.addTapListener(tapListener)
        binding.mapview.map.addCameraListener(cameraListener)
        mapObjects = binding.mapview.map.mapObjects.addCollection()
        searchCoffee()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventsFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is MapViewModel.Event.NavigateToCoffee -> {
                            findNavController().navigate(
                                R.id.action_map_to_coffee, bundleOf(
                                    ARG_COFFEESHOP to event.coffeeShop
                                )
                            )
                        }
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResult
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { coffeeShops: List<CoffeeShop> ->
                    Timber.d("Список $coffeeShops")
                    val imageProvider = DrawableImageProvider(
                        requireContext(),
                        R.drawable.ic_action_name
                    )
                    mapObjects.clear()
                    coffeeShops.forEach {
                        val placeMark = mapObjects.addPlacemark(
                            Point(it.latitude.toDouble(), it.longitude.toDouble()),
                            imageProvider
                        )
                        placeMark.addTapListener(placeMarkTapListener)
                        placeMark.userData = it
                    }
                }
        }
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    fun searchCoffee() {
        searchManager = SearchFactory.getInstance().createSearchManager(
            SearchManagerType.ONLINE
        )
        val point = VisibleRegionUtils.toPolygon(binding.mapview.map.visibleRegion)
        searchSession = searchManager!!.submit(
            "кофейня",
            point,
            SearchOptions(),
            searchListener
        )
    }

//    fun showUserLocation() {
////binding.mapview.map.sublayerManager.
//        UserLocationLayer
//    }

    fun permission(): Boolean {
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return coarseLocationGranted || fineLocationGranted
    }
}