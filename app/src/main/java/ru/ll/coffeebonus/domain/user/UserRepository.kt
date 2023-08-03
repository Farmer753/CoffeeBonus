package ru.ll.coffeebonus.domain.user

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun saveUser(firestoreUser: FirestoreUser)
    fun getAuthorizedUser(): FirestoreUser
    suspend fun userExists(userId: String): Boolean
    suspend fun getFirestoreUser(): FirestoreUser
    suspend fun coffeeShopFavoriteExists(firestoreId: String): Boolean
    suspend fun addCoffeeFavorite(firestoreId: String)
    suspend fun removeCoffeeFavorite(firestoreId: String)
    suspend fun upsertUserBonusProgram(firestoreId: String, newCount: Int)
    fun getFirestoreUserFlow(): Flow<FirestoreUser?>
}