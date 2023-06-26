package ru.ll.coffeebonus.ui.profile

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.ll.coffeebonus.databinding.ItemCoffeeShopAllBinding
import ru.ll.coffeebonus.databinding.ItemCoffeeShopBinding
import ru.ll.coffeebonus.ui.adapter.AdapterItem
import ru.ll.coffeebonus.ui.util.showMarker

fun coffeeShopAdapterDelegate(clickListener: (CoffeeShopUiItem) -> Unit) =
    adapterDelegateViewBinding<CoffeeShopUiItem, AdapterItem, ItemCoffeeShopAllBinding>(
        { layoutInflater, parent ->
            ItemCoffeeShopAllBinding.inflate(layoutInflater, parent, false)
        }) {
        bind {
            with(binding) {
                root.setOnClickListener { clickListener(item) }
                nameTextView.text = item.name
                addressTextView.text = item.address
                mapView.showMarker(item.latitude, item.longitude)
            }
        }
    }
