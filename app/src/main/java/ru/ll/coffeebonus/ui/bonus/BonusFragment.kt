package ru.ll.coffeebonus.ui.bonus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentBonusBinding
import ru.ll.coffeebonus.domain.CoffeeShop
import ru.ll.coffeebonus.ui.BaseFragment
import ru.ll.coffeebonus.ui.coffee.CoffeeFragment.Companion.ARG_COFFEESHOP
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
            viewModelAssistedFactory,
            requireArguments().getSerializable(ARG_COFFEESHOP) as CoffeeShop
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonRetry.setOnClickListener { viewModel.loadInitialData() }

        initNotBonusLayout()
        initBonusLayout()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadingStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it) {
                        binding.progressView.visibility = VISIBLE
                    } else {
                        binding.progressView.visibility = GONE
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    Timber.d("ошибка $it")
                    binding.errorView.visibility = if (it != null) {
                        VISIBLE
                    } else {
                        GONE
                    }
                    binding.errorTextView.text = it
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.coffeeShopStateFlow
                .map { it?.coffeeBonus }
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it == null) {
                        binding.notBonusConstraintLayout.visibility = VISIBLE
                        binding.bonusConstraintLayout.visibility = GONE
                    } else {
                        binding.notBonusConstraintLayout.visibility = GONE
                        binding.bonusConstraintLayout.visibility = VISIBLE
                        binding.flexBox.removeAllViews()
                        (0 until it.bonusQuantity).forEach {
                            val imageView = ImageView(requireContext())
                            imageView.setImageResource(R.drawable.ic_coffee)
                            binding.flexBox.addView(imageView)
                            imageView.updateLayoutParams<MarginLayoutParams> {
                                bottomMargin =
                                    resources.getDimensionPixelSize(R.dimen.coffee_in_bonus_bottom_margin)
                            }
                        }
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.countCoffeeStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { count ->
                    (0 until count).forEach {
                        (binding.flexBox[it] as ImageView).setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.ic_launcher_background)
                        )
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadingButtonStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it) {
                        binding.buttonsProgressView.visibility = VISIBLE
                    } else {
                        binding.buttonsProgressView.visibility = GONE
                    }
                }
        }
    }

    private fun initBonusLayout() {
        binding.addCoffeeButton.setOnClickListener {
            viewModel.addCoffeeButtonClick()
        }
        binding.clearCoffeeButton.setOnClickListener { viewModel.clearCoffeeBonusButtonClick() }
        binding.editCoffeeBonusButton.setOnClickListener { viewModel.editCoffeeBonusButtonClick() }
        binding.deleteCoffeeBonusButton.setOnClickListener { viewModel.deleteBonusButtonClick() }
    }

    private fun initNotBonusLayout() {
        binding.addBonusButton.setOnClickListener {
            binding.addBonusButton.visibility = GONE
            binding.bonusInputLayout.visibility = VISIBLE
            binding.sendMaterialButton.visibility = VISIBLE
        }
    }
}
