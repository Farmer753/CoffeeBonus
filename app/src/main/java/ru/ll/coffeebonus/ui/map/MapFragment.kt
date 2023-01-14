package ru.ll.coffeebonus.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.yandex.mapkit.map.*
import com.yandex.mapkit.search.*
import com.yandex.mapkit.uri.UriObjectMetadata
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentMapBinding
import ru.ll.coffeebonus.domain.CoffeeShop
import ru.ll.coffeebonus.ui.BaseFragment
import ru.ll.coffeebonus.ui.coffee.CoffeeFragment.Companion.ARG_LAT
import ru.ll.coffeebonus.ui.coffee.CoffeeFragment.Companion.ARG_LON
import ru.ll.coffeebonus.ui.coffee.CoffeeFragment.Companion.ARG_NAME
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
//            response.collection.children.forEach {
//                val uri = it.obj?.metadataContainer?.getItem(UriObjectMetadata::class.java)
//                    ?.uris
//                    ?.firstOrNull()
//                    ?.value
//                    ?.toUri()
//                    ?.getQueryParameter("oid")
//
//                Timber.d("Успешный вывод ${it.obj?.name}, $uri")
//
////                val imageProvider = DrawableImageProvider(
////                    requireContext(),
////                    R.drawable.ic_action_name
////                )
////                val placeMark = mapObjects.addPlacemark(
////                    it.obj!!.geometry.first().point!!,
////                    imageProvider
////                )
////                placeMark.addTapListener(placeMarkTapListener)
////                placeMark.userData = it.obj?.name
//            }
        }

        override fun onSearchError(p0: com.yandex.runtime.Error) {
            showMessage("Error $p0")
            Timber.e("Error $p0")
        }
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

    val placeMarkTapListener: MapObjectTapListener = MapObjectTapListener { a, point ->
        showMessage("Нажата")
        binding.mapview.map.move(
            CameraPosition(
                point, 14.0f, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0.5f)
        ) {
            viewModel.mapClick(
                point.longitude.toFloat(),
                point.latitude.toFloat(),
                a.userData.toString()
            )
        }
        true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("переменная из MapFragment ${viewModel.test} ")
        binding.mapview.map.move(
            CameraPosition(
                Point(59.938879, 30.315212), 15.0f, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0f),
            null
        )
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
                                    ARG_LON to event.longitude,
                                    ARG_LAT to event.latitude,
                                    ARG_NAME to event.nameCoffee
                                )
                            )
                        }
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResult
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { result ->
                    Timber.d("Список $result")
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
//        Timber.d("выводим из searchCoffee ${point.boundingBox!!.northEast.latitude} ")
        searchSession = searchManager!!.submit(
            "кофейня",
            point,
            SearchOptions(),
            searchListener
        )
    }
}