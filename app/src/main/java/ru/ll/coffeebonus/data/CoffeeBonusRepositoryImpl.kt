package ru.ll.coffeebonus.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ru.ll.coffeebonus.domain.bonus.CoffeeBonusRepository
import ru.ll.coffeebonus.domain.bonus.FirestoreBonus

class CoffeeBonusRepositoryImpl(
    private val bd: FirebaseFirestore
) : CoffeeBonusRepository {

    companion object {
        const val COLLECTION_COFFEE_SHOPS = "coffeeshops"
        const val FIELD_COFFEE_BONUS = "coffeeBonus"
        const val FIELD_BONUS_QUANTITY = "bonusQuantity"
        const val FIELD_FIRESTORE_ID = "firestoreId"
    }

    override suspend fun save(firestoreId: String, firestoreBonus: FirestoreBonus) {
        val documentReference = bd.collection(COLLECTION_COFFEE_SHOPS).document(firestoreId)
        documentReference.update(FIELD_COFFEE_BONUS, firestoreBonus).await()
    }

    override suspend fun edit(firestoreId: String, newFreeCoffee: FirestoreBonus) {
        val documentReference = bd.collection(COLLECTION_COFFEE_SHOPS).document(firestoreId)
        documentReference.update(FIELD_COFFEE_BONUS, newFreeCoffee).await()
    }

    override suspend fun delete(firestoreId: String) {
        val documentReference = bd.collection(COLLECTION_COFFEE_SHOPS).document(firestoreId)
        documentReference.update(FIELD_COFFEE_BONUS, null).await()
    }
}