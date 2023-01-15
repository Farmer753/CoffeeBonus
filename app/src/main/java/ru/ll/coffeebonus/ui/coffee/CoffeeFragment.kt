package ru.ll.coffeebonus.ui.coffee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.ll.coffeebonus.databinding.FragmentCoffeeBinding
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
        binding.latitude.text = "Широта ${viewModel.coffeeShop.latitude}"
        binding.longitude.text = "Долгота ${viewModel.coffeeShop.longitude}"
        binding.coffeeName.text = "Название ${viewModel.coffeeShop.name}"
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