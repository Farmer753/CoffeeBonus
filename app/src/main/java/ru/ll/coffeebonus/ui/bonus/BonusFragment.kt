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
import dagger.hilt.android.AndroidEntryPoint
import ru.ll.coffeebonus.R
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
        binding.addBonusButton.setOnClickListener {
            binding.addBonusButton.visibility = GONE
            binding.bonusInputLayout.visibility = VISIBLE
            binding.sendMaterialButton.visibility = VISIBLE
        }
        (0..14).forEach {
            val imageView = ImageView(requireContext())
            imageView.setImageResource(R.drawable.ic_coffee)
            binding.flexBox.addView(imageView)
            imageView.updateLayoutParams<MarginLayoutParams> {
                bottomMargin =
                    resources.getDimensionPixelSize(R.dimen.coffee_in_bonus_bottom_margin)
            }
        }
        binding.addCoffeeButton.setOnClickListener {
            (binding.flexBox[0] as ImageView).setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.purple_700)
            )
        }
    }
}
