package ru.ll.coffeebonus.ui.profile

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.ItemCoffeeShopBinding
import ru.ll.coffeebonus.ui.adapter.AdapterItem
import ru.ll.coffeebonus.util.DrawableImageProvider

fun coffeeShopAdapterDelegate(clickListener: (CoffeeShopUiItem) -> Unit) =
    adapterDelegateViewBinding<CoffeeShopUiItem, AdapterItem, ItemCoffeeShopBinding>(
        { layoutInflater, parent ->
            ItemCoffeeShopBinding.inflate(layoutInflater, parent, false)
        }) {
        bind {
            with(binding) {
                root.setOnClickListener{clickListener(item)}
                nameTextView.text = item.name
                addressTextView.text = item.address

                binding.mapView.setNoninteractive(true)
                val imageProvider = DrawableImageProvider(
                    context,
                    R.drawable.ic_action_name
                )
                mapView.map.mapObjects.clear()
                mapView.map.mapObjects.addPlacemark(
                    Point(
                        item.latitude.toDouble(),
                        item.longitude.toDouble()
                    ),
                    imageProvider
                )
                mapView.map.move(
                    CameraPosition(
                        Point(
                            item.latitude.toDouble(),
                            item.longitude.toDouble()
                        ),
//                        TODO добавить константу
                        15.0f, 0.0f, 0.0f
                    )
                )
            }

        }
    }
