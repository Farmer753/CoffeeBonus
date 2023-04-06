package ru.ll.coffeebonus.domain.user

interface UserRepository {
    suspend fun saveUser(firestoreUser: FirestoreUser)
    fun getAuthorizedUser(): FirestoreUser
    suspend fun userExists(userId: String):Boolean
    suspend fun getFirestoreUser(): FirestoreUser
    suspend fun coffeeShopFavoriteExists(firestoreId: String): Boolean
    suspend fun addCoffeeFavorite(firestoreId: String)
    suspend fun removeCoffeeFavorite(firestoreId: String)
    suspend fun getFavoriteCoffeeShops(count: Short): List<String>
}