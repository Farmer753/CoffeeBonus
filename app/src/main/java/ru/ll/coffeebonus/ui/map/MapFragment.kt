package ru.ll.coffeebonus.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
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
import com.yandex.mapkit.location.*
import com.yandex.mapkit.map.*
import com.yandex.mapkit.search.*
import com.yandex.mapkit.uri.UriObjectMetadata
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentMapBinding
import ru.ll.coffeebonus.domain.CoffeeShop
import ru.ll.coffeebonus.ui.BaseFragment
import ru.ll.coffeebonus.ui.coffee.CoffeeFragment.Companion.ARG_COFFEESHOP
import ru.ll.coffeebonus.ui.login.LoginFragment
import ru.ll.coffeebonus.util.DrawableImageProvider
import ru.ll.coffeebonus.util.ProgramaticalDrawableImageProvider
import timber.log.Timber
import java.lang.Float.max


@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding, MapViewModel>() {

    companion object {
        private const val DESIRED_ACCURACY = 0.0
        private const val MINIMAL_TIME: Long = 0
        private const val MINIMAL_DISTANCE = 50.0
        private const val COFFEE_SHOP_SEARCH_ZOOM_DEFAULT = 13f
        private const val COFFEE_SHOP_ZOOM_DEFAULT = 14f
        private const val USER_ZOOM_DEFAULT = 15f
        private const val USE_IN_BACKGROUND = false
        private const val ANIMATION_DURATION = .5f
    }

    private val clusterTapListener: ClusterTapListener = ClusterTapListener {
        Timber.d("Размер placemarks ${it.placemarks.size}")
        binding.mapview.map.move(
            CameraPosition(
                it.placemarks[0].geometry,
                binding.mapview.map.cameraPosition.zoom + 1, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, ANIMATION_DURATION),
            null
        )
        true
    }

    private val clusterListener: ClusterListener =
        ClusterListener {
            val coffeeShopsOnMapFromCluster =
                it.placemarks.map { placemark -> (placemark.userData as CoffeeShopOnMap) }
            val imageProvider =
                ProgramaticalDrawableImageProvider(generateClusterIcon(coffeeShopsOnMapFromCluster))
            it.addClusterTapListener(clusterTapListener)
            it.appearance.setIcon(imageProvider)
        }
    private val shownPlacemarks = mutableSetOf<PlacemarkMapObject>()
    private lateinit var mapObjects: ClusterizedPlacemarkCollection
    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null
    private val searchListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            if (searchSession!!.hasNextPage()) {
                searchSession!!.fetchNextPage(this)
            }
            viewModel.onSearchResult(
                response.collection.children.map {
                    CoffeeShop(
                        id = it.obj?.metadataContainer?.getItem(UriObjectMetadata::class.java)
                            ?.uris
                            ?.firstOrNull()
                            ?.value
                            ?.toUri()?.getQueryParameter("oid")!!,
                        name = it.obj?.name!!,
                        address = it.obj?.metadataContainer?.getItem(BusinessObjectMetadata::class.java)
                            ?.address
                            ?.formattedAddress ?: "",
                        longitude = it.obj?.geometry?.first()?.point!!.longitude.toFloat(),
                        latitude = it.obj?.geometry?.first()?.point!!.latitude.toFloat()
                    )
                }
            )
        }

        override fun onSearchError(p0: com.yandex.runtime.Error) {
            showMessage("Error $p0")
            Timber.e("Error $p0")
        }
    }

    private var mapMoved = false
    private var markerMy: PlacemarkMapObject? = null
    private var locationManager: LocationManager? = null
    private val myLocationListener = object : LocationListener {
        override fun onLocationUpdated(p0: Location) {
            val imageProvider = DrawableImageProvider(
                requireContext(),
                R.drawable.ic_action_my
            )
            markerMy?.let {
                binding.mapview.map.mapObjects.remove(it)
            }
            markerMy = binding.mapview.map.mapObjects.addPlacemark(
                Point(p0.position.latitude, p0.position.longitude),
                imageProvider
            )
            if (!mapMoved) {
                binding.mapview.map.move(
                    CameraPosition(
                        p0.position,
                        USER_ZOOM_DEFAULT, 0.0f, 0.0f
                    ),
                    Animation(Animation.Type.SMOOTH, ANIMATION_DURATION),
                    null
                )
                mapMoved = true
            }
        }

        override fun onLocationStatusUpdated(p0: LocationStatus) {
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
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

    private val tapListener: GeoObjectTapListener = GeoObjectTapListener {
        true
    }

    private val cameraListener = CameraListener { _, p1, _, _ ->
        if (p1.zoom > COFFEE_SHOP_SEARCH_ZOOM_DEFAULT) {
            searchCoffee()
        }
    }

    override val viewModel: MapViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMapBinding =
        FragmentMapBinding::inflate

    private val placeMarkTapListener: MapObjectTapListener =
        MapObjectTapListener { placeMark, point ->
            showMessage("Нажата")
            val zoom = max(COFFEE_SHOP_ZOOM_DEFAULT, binding.mapview.map.cameraPosition.zoom)
            binding.mapview.map.move(
                CameraPosition(
                    point, zoom, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, ANIMATION_DURATION)
            ) {
                viewModel.mapClick(
                    placeMark.userData as CoffeeShopOnMap
                )
            }
            true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
        SearchFactory.initialize(requireContext())
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("переменная из MapFragment ${viewModel.test} ")
        if (permission()) {
            locationManager = MapKitFactory.getInstance().createLocationManager()
            locationManager?.subscribeForLocationUpdates(
                DESIRED_ACCURACY,
                MINIMAL_TIME,
                MINIMAL_DISTANCE,
                USE_IN_BACKGROUND,
                FilteringMode.OFF,
                myLocationListener
            )
        } else {
            binding.mapview.map.move(
                CameraPosition(
                    Point(59.938879, 30.315212), 15.0f, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, ANIMATION_DURATION),
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
        mapObjects =
            binding.mapview.map.mapObjects.addClusterizedPlacemarkCollection(clusterListener)

        binding.toolbar.inflateMenu(R.menu.menu_profile)
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.profile) {
                Timber.d("Профиль нажат")
                viewModel.profileClick()
            }
            false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventsFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is MapViewModel.Event.NavigateToCoffee -> {
                            Timber.d("event ${event.coffeeShop}")
                            findNavController().navigate(
                                R.id.action_map_to_coffee,
                                bundleOf(
                                    ARG_COFFEESHOP to event.coffeeShop
                                )
                            )
                        }
                        MapViewModel.Event.NavigateToProfile -> findNavController().navigate(
                            R.id.action_map_to_profile
                        )
                        MapViewModel.Event.NavigateToLogin -> findNavController().navigate(
                            R.id.action_map_to_login,
                            bundleOf(LoginFragment.ARG_OPEN_PROFILE to true)
                        )
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResult
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { coffeeShops: List<CoffeeShopOnMap> ->
                    val imageProviderUnRegistered = DrawableImageProvider(
                        requireContext(),
                        R.drawable.ic_action_name
                    )
                    val imageProviderFavorite = DrawableImageProvider(
                        requireContext(),
                        R.drawable.ic_baseline_favorite_24
                    )
                    val imageProviderRegistered = DrawableImageProvider(
                        requireContext(),
                        R.drawable.ic_baseline_blender_24
                    )
                    val oldShownPlacemarks = shownPlacemarks.size
                    val shownCoffeeShopsIds =
                        shownPlacemarks.map { (it.userData as CoffeeShopOnMap).id }
                    shownPlacemarks += coffeeShops.filter {
                        !shownCoffeeShopsIds.contains(it.id)
                    }.map {
                        val placeMark: PlacemarkMapObject = mapObjects.addPlacemark(
                            Point(it.latitude.toDouble(), it.longitude.toDouble()),
                            if (it.favorite) {
                                imageProviderFavorite
                            } else {
                                if (it.firestoreId == null) {
                                    imageProviderUnRegistered
                                } else {
                                    imageProviderRegistered
                                }
                            }
                        )
                        placeMark.addTapListener(placeMarkTapListener)
                        placeMark.userData = it
                        return@map placeMark
                    }
                    if (oldShownPlacemarks != shownPlacemarks.size) {
                        mapObjects.clusterPlacemarks(200.0, 15)
                    }
                }
        }
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        Timber.d("onStop")
        shownPlacemarks.clear()
        mapObjects.clear()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("onDestroyView")
        locationManager?.unsubscribe(myLocationListener)
        markerMy = null
        mapMoved = false
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    private fun searchCoffee() {
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

    private fun permission(): Boolean {
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

    private fun generateClusterIcon(coffeeShopsOnMap: List<CoffeeShopOnMap>): Drawable {
        val hasFavorite = coffeeShopsOnMap.firstOrNull { it.favorite } != null
        val hasRegistered = coffeeShopsOnMap.firstOrNull { it.firestoreId != null } != null

        val border = GradientDrawable()
        border.shape = GradientDrawable.OVAL
        border.setColor(Color.WHITE)

        val background = GradientDrawable()
        background.shape = GradientDrawable.OVAL
        background.setColor(Color.BLACK)

        val clip = GradientDrawable()
        clip.shape = GradientDrawable.OVAL
        clip.setColor(Color.RED)

        val layers = arrayOf<Drawable>(background, border, clip)
        val layerDrawable = LayerDrawable(layers)

        layerDrawable.setLayerSize(0, 100, 100)
        layerDrawable.setLayerSize(1, 50, 50)
        layerDrawable.setLayerSize(2, 20, 20)

        layerDrawable.setLayerInset(0, 0, 0, 0, 0)
        layerDrawable.setLayerInset(1, 25, 25, 0, 0)
        layerDrawable.setLayerInset(2, 40, 40, 0, 0)

        if (!hasFavorite) {
            layerDrawable.setLayerSize(2, 0, 0)
            if (!hasRegistered) {
                layerDrawable.setLayerSize(1, 0, 0)
            }
        }

        return layerDrawable
    }
}