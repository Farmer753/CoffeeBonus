package ru.ll.coffeebonus.ui.coffee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentCoffeeBinding
import ru.ll.coffeebonus.databinding.FragmentProfileBinding
import ru.ll.coffeebonus.ui.BaseFragment
import timber.log.Timber

@AndroidEntryPoint
class CoffeeFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCoffeeBinding? = null
    protected val binding get() = _binding!!
   val viewModel: CoffeeViewModel by viewModels()

    val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCoffeeBinding =
        FragmentCoffeeBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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