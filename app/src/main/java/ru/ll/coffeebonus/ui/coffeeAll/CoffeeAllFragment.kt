package ru.ll.coffeebonus.ui.coffeeAll

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint
import ru.ll.coffeebonus.databinding.FragmentCoffeeAllBinding
import ru.ll.coffeebonus.ui.BaseFragment
import ru.ll.coffeebonus.ui.adapter.AdapterItem

@AndroidEntryPoint
class CoffeeAllFragment : BaseFragment<FragmentCoffeeAllBinding, CoffeeAllViewModel>() {
    override val viewModel: CoffeeAllViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCoffeeAllBinding =
        FragmentCoffeeAllBinding::inflate

    private lateinit var adapter: ListDelegationAdapter<List<AdapterItem>>
}