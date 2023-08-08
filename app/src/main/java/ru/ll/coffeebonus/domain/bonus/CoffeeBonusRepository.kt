package ru.ll.coffeebonus.domain.bonus

interface CoffeeBonusRepository {
    suspend fun saveBonusProgram(firestoreId: String, firestoreBonus: FirestoreBonus)
    suspend fun edit(firestoreId: String, newFreeCoffee: FirestoreBonus)
    suspend fun delete(firestoreId: String)
}