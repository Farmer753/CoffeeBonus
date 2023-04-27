package ru.ll.coffeebonus.domain.bonus

interface CoffeeBonusRepository {
    suspend fun save(firestoreId: String, firestoreBonus: FirestoreBonus)
    suspend fun edit(firestoreBonus: FirestoreBonus)
    suspend fun delete(firestoreBonus: FirestoreBonus)
}