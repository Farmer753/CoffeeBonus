package ru.ll.coffeebonus.domain

import java.io.Serializable

data class CoffeeShop(
    val firestoreId: String? = null,
    val id: String,
    val name: String,
    val address: String,
    val longitude: Float,
    val latitude: Float
) : Serializable
