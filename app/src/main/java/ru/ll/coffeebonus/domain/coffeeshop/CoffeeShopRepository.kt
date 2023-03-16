package ru.ll.coffeebonus.domain.coffeeshop

interface CoffeeShopRepository {
    suspend fun save(firestoreCoffeeShop: FirestoreCoffeeShop)
}