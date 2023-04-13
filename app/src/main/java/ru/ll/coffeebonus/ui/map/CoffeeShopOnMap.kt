package ru.ll.coffeebonus.ui.map

import java.io.Serializable

data class CoffeeShopOnMap(
    val firestoreId: String?,
    val id: String,
    val name: String,
    val address: String,
    val longitude: Float,
    val latitude: Float,
    val favorite: Boolean
) : Serializable
