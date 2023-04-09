package ru.ll.coffeebonus.ui.profile

import ru.ll.coffeebonus.ui.adapter.AdapterItem

data class CoffeeShopUiItem(
    val firestoreId: String?,
    val id: String,
    val name: String,
    val address: String,
    val longitude: Float,
    val latitude: Float
) : AdapterItem
