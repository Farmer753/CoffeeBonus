package ru.ll.coffeebonus.ui.profile

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.ll.coffeebonus.databinding.ItemCoffeeShopMoreThanTenBinding
import ru.ll.coffeebonus.ui.adapter.AdapterItem

fun coffeeShopMoreThanTenAdapterDelegate(clickListener: () -> Unit) =
    adapterDelegateViewBinding<CoffeeShopMoreThanTenUiItem, AdapterItem, ItemCoffeeShopMoreThanTenBinding>(
        { layoutInflater, parent ->
            ItemCoffeeShopMoreThanTenBinding.inflate(layoutInflater, parent, false)
        })
    {
        bind {
            with(binding) {
//                coffeeShopMoreThanTenTextView.setOnClickListener { clickListener() }
            }
        }
    }
