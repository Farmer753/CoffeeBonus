package ru.ll.coffeebonus.domain.user

import kotlinx.coroutines.flow.Flow
import ru.ll.coffeebonus.domain.coffeeshop.FirestoreCoffeeShop

interface UserRepository {
    suspend fun saveUser(firestoreUser: FirestoreUser)
    fun getAuthorizedUser(): FirestoreUser
    suspend fun userExists(userId: String):Boolean
    suspend fun getFirestoreUser(): FirestoreUser
    suspend fun coffeeShopFavoriteExists(firestoreId: String): Boolean
    suspend fun addCoffeeFavorite(firestoreId: String)
    suspend fun removeCoffeeFavorite(firestoreId: String)
    fun getFirestoreUserFlow(): Flow<FirestoreUser>
}