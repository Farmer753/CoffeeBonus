package ru.ll.coffeebonus.ui.bonus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.ll.coffeebonus.databinding.FragmentBonusBinding
import ru.ll.coffeebonus.ui.BaseFragment
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BonusFragment : BaseFragment<FragmentBonusBinding, BonusViewModel>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBonusBinding =
        FragmentBonusBinding::inflate

    @Inject
    lateinit var viewModelAssistedFactory: BonusViewModel.Factory

    override val viewModel: BonusViewModel by viewModels {
        BonusViewModel.provideFactory(
            viewModelAssistedFactory
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("FragmentBonusBinding")
    }
}
