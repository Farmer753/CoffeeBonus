package ru.ll.coffeebonus.domain.coffeeshop

interface CoffeeShopRepository {
    suspend fun save(firestoreCoffeeShop: FirestoreCoffeeShop)
    suspend fun exists(id: String): Boolean
}