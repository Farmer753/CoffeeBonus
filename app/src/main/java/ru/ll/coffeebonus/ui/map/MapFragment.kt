package ru.ll.coffeebonus.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.search.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentMapBinding
import ru.ll.coffeebonus.ui.BaseFragment
import ru.ll.coffeebonus.ui.coffee.CoffeeFragment.Companion.ARG_LAT
import ru.ll.coffeebonus.ui.coffee.CoffeeFragment.Companion.ARG_LON
import timber.log.Timber

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding, MapViewModel>() {

    var searchManager: SearchManager? = null
    var searchSession: Session? = null

    override val viewModel: MapViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMapBinding =
        FragmentMapBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    val tapListener: GeoObjectTapListener = GeoObjectTapListener {
        val longitude = it.geoObject.geometry.first().point!!.longitude.toFloat()
        val latitude = it.geoObject.geometry.first().point!!.latitude.toFloat()
        Timber.d("координаты $longitude")
        Timber.d("координаты $latitude")
        viewModel.mapClick(longitude, latitude)
        false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("переменная из MapFragment ${viewModel.test} ")
        binding.mapview.map.move(
            CameraPosition(
                Point(59.938879, 30.315212), 11.0f, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0f),
            null
        )
        binding.mapview.map.addTapListener(tapListener)
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
                                    ARG_LAT to event.latitude
                                )
                            )
                        }
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
        val point = Geometry.fromPoint(Point(59.95, 30.32))
        searchSession = searchManager!!.submit("кафе", point, SearchOptions(),
            object : Session.SearchListener {
                override fun onSearchResponse(p0: Response) {
                    showMessage("Success")
                    p0.collection.children.forEach {
                        Timber.d("Успешный вывод ${it.obj?.name}")
                    }
                }

                override fun onSearchError(p0: com.yandex.runtime.Error) {
                    showMessage("Error")
                }
            }
        )
    }

}