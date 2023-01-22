package ru.ll.coffeebonus.ui.coffee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import dagger.hilt.android.AndroidEntryPoint
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentCoffeeBinding
import ru.ll.coffeebonus.di.util.DrawableImageProvider
import ru.ll.coffeebonus.domain.CoffeeShop
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
        binding.name.text = "Название ${viewModel.coffeeShop.name}"
        binding.map.setNoninteractive(true)
        val imageProvider = DrawableImageProvider(
            requireContext(),
            R.drawable.ic_action_name
        )
        binding.map.map.mapObjects.addPlacemark(
            Point(
                viewModel.coffeeShop.latitude.toDouble(),
                viewModel.coffeeShop.longitude.toDouble()
            ),
            imageProvider
        )
        binding.map.map.move(
            CameraPosition(
                Point(
                    viewModel.coffeeShop.latitude.toDouble(),
                    viewModel.coffeeShop.longitude.toDouble()
                ),
                15.0f, 0.0f, 0.0f
            )
        )
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