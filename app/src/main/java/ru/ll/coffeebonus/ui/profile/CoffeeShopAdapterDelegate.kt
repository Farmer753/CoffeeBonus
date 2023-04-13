package ru.ll.coffeebonus.ui.profile

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.ll.coffeebonus.databinding.ItemCoffeeShopBinding
import ru.ll.coffeebonus.ui.adapter.AdapterItem
import ru.ll.coffeebonus.ui.util.showMarker

fun coffeeShopAdapterDelegate(clickListener: (CoffeeShopUiItem) -> Unit) =
    adapterDelegateViewBinding<CoffeeShopUiItem, AdapterItem, ItemCoffeeShopBinding>(
        { layoutInflater, parent ->
            ItemCoffeeShopBinding.inflate(layoutInflater, parent, false)
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
