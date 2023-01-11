package ru.ll.coffeebonus.ui.coffee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.ll.coffeebonus.databinding.FragmentCoffeeBinding
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CoffeeFragment : BottomSheetDialogFragment() {

    companion object {
        const val ARG_LON = "ARG_LON"
        const val ARG_LAT = "ARG_LAT"
        const val ARG_NAME = "ARG_NAME"
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
            requireArguments().getFloat(ARG_LAT),
            requireArguments().getFloat(ARG_LON),
            requireArguments().getString(ARG_NAME)!!
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Широта ${viewModel.latitude}")
        Timber.d("Долгота ${viewModel.longitude}")
        binding.latitude.text = "Широта ${viewModel.latitude}"
        binding.longitude.text = "Долгота ${viewModel.longitude}"
        binding.coffeeName.text = "Название ${viewModel.nameCoffee}"
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