package ru.ll.coffeebonus.domain.coffeeshop

import kotlinx.coroutines.flow.Flow

interface CoffeeShopRepository {
    suspend fun createCoffeeShop(firestoreCoffeeShop: FirestoreCoffeeShop)
    suspend fun exists(id: String): Boolean
    suspend fun getByFirestoreId(firestoreId: String): FirestoreCoffeeShop
    suspend fun getByYandexId(yandexId: String): FirestoreCoffeeShop?
    suspend fun getByYandexIdFromServer(yandexId: String): FirestoreCoffeeShop?
    suspend fun getByYandexId(yandexIds: List<String>): List<FirestoreCoffeeShop>
    suspend fun getCoffeeShopsByIds(listId: List<String>): List<FirestoreCoffeeShop>
    fun getByYandexIdFlow(yandexId: String): Flow<FirestoreCoffeeShop?>
}