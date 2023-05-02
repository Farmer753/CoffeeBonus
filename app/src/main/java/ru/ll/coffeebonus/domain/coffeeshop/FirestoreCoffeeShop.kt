package ru.ll.coffeebonus.domain.coffeeshop

import ru.ll.coffeebonus.domain.bonus.FirestoreBonus

data class FirestoreCoffeeShop @JvmOverloads constructor(
    var firestoreId: String = "",
    /**
     * id from Yandex
     */
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val longitude: Float = 0f,
    val latitude: Float = 0f,
    val coffeeBonus: FirestoreBonus? = null
)