package ru.ll.coffeebonus.domain.coffeeshop

interface CoffeeShopRepository {
    suspend fun save(firestoreCoffeeShop: FirestoreCoffeeShop)
    suspend fun exists(id: String): Boolean
    suspend fun getByFirestoreId(firestoreId: String): FirestoreCoffeeShop
    suspend fun getByYandexId(yandexId: String): FirestoreCoffeeShop?
    suspend fun getByYandexId(yandexIds: List<String>): List<FirestoreCoffeeShop>
    suspend fun getCoffeeShopsByIds(listId: List<String>): List<FirestoreCoffeeShop>
}