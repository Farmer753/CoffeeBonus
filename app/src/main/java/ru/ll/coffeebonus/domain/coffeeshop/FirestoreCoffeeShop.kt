package ru.ll.coffeebonus.domain.coffeeshop

data class FirestoreCoffeeShop @JvmOverloads constructor(
    var firestoreId: String = "",
    /**
     * id from Yandex
     */
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val longitude: Float = 0f,
    val latitude: Float = 0f
)