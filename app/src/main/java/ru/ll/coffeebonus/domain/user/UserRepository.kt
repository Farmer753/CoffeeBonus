package ru.ll.coffeebonus.domain.user

interface UserRepository {
    suspend fun saveUserCoroutines(firestoreUser: FirestoreUser)
    fun getAuthorizedUser(): FirestoreUser
    suspend fun userExist(userId: String):Boolean
    suspend fun getFirestoreUser(): FirestoreUser
}