package ru.ll.coffeebonus.ui.coffee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentCoffeeBinding
import ru.ll.coffeebonus.domain.CoffeeShop
import ru.ll.coffeebonus.util.DrawableImageProvider
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CoffeeFragment : BottomSheetDialogFragment() {

    companion object {
        const val ARG_COFFEESHOP = "ARG_COFFEESHOP"
    }

    private var _binding: FragmentCoffeeBinding? = null
    protected val binding get() = _binding!!

    val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCoffeeBinding =
        FragmentCoffeeBinding::inflate

    @Inject
    lateinit var viewModelAssistedFactory: CoffeeViewModel.Factory

    private val viewModel: CoffeeViewModel by viewModels {
        CoffeeViewModel.provideFactory(
            viewModelAssistedFactory,
            requireArguments().getSerializable(ARG_COFFEESHOP) as CoffeeShop
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nameTextView.text = "Название ${viewModel.coffeeShop.name}"
        binding.addressTextView.text = "Адрес ${viewModel.coffeeShop.address}"
        binding.mapView.setNoninteractive(true)
        binding.favoriteImageView.setOnClickListener { viewModel.toggleFavorite() }
        val imageProvider = DrawableImageProvider(
            requireContext(),
            R.drawable.ic_action_name
        )
        binding.mapView.map.mapObjects.addPlacemark(
            Point(
                viewModel.coffeeShop.latitude.toDouble(),
                viewModel.coffeeShop.longitude.toDouble()
            ),
            imageProvider
        )
        binding.mapView.map.move(
            CameraPosition(
                Point(
                    viewModel.coffeeShop.latitude.toDouble(),
                    viewModel.coffeeShop.longitude.toDouble()
                ),
                15.0f, 0.0f, 0.0f
            )
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventsFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    Timber.d("$event event")
                    when (event) {
                        is CoffeeViewModel.Event.ShowNeedAuthorisationMessage -> {
                            val snackbar = Snackbar.make(
//                                dialog?.window?.decorView?:view,
//                                parentFragment?.view ?: view,
//                                binding.anchorView,
                                view,
                                R.string.need_authorisation,
                                Snackbar.LENGTH_LONG
                            )
                            snackbar.anchorView = binding.anchorView
                            snackbar.show()
                        }
                    }
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }
}