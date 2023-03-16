package ru.ll.coffeebonus.domain.coffeeshop

data class FirestoreCoffeeShop @JvmOverloads constructor(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val longitude: Float = 0f,
    val latitude: Float = 0f
)