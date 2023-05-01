package ru.ll.coffeebonus.ui.profile

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.ll.coffeebonus.databinding.ItemCoffeeShopAllBinding
import ru.ll.coffeebonus.databinding.ItemCoffeeShopBinding
import ru.ll.coffeebonus.databinding.ItemEmptyBinding
import ru.ll.coffeebonus.ui.adapter.AdapterItem

fun coffeeShopAllAdapterDelegate(clickListener: (CoffeeShopAllUiItem) -> Unit) =
    adapterDelegateViewBinding<CoffeeShopAllUiItem, AdapterItem, ItemCoffeeShopAllBinding>(
        { layoutInflater, parent ->
            ItemCoffeeShopAllBinding.inflate(layoutInflater, parent, false)
        }) {
        binding.root.setOnClickListener { clickListener(item) }
    }
