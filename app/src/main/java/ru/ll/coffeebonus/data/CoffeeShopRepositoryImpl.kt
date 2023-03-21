package ru.ll.coffeebonus.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ru.ll.coffeebonus.domain.coffeeshop.CoffeeShopRepository
import ru.ll.coffeebonus.domain.coffeeshop.FirestoreCoffeeShop

class CoffeeShopRepositoryImpl(
    private val bd: FirebaseFirestore
) : CoffeeShopRepository {

    companion object {
        const val COLLECTION_COFFEE_SHOPS = "coffeeshops"
        const val FIELD_YANDEX_ID = "id"
        const val FIELD_FIRESTORE_ID = "firestoreId"
    }

    override suspend fun save(firestoreCoffeeShop: FirestoreCoffeeShop) {
        val documentReference = bd.collection(COLLECTION_COFFEE_SHOPS).document()
        documentReference.set(firestoreCoffeeShop.apply { firestoreId = documentReference.id })
            .await()
    }

    override suspend fun exists(id: String): Boolean {
        return bd.collection(COLLECTION_COFFEE_SHOPS).whereEqualTo(FIELD_YANDEX_ID, id).get()
            .await().isEmpty.not()
    }

    override suspend fun getByFirestoreId(firestoreId: String): FirestoreCoffeeShop {
        return bd.collection(COLLECTION_COFFEE_SHOPS).document(firestoreId)
            .get().await().toObject(FirestoreCoffeeShop::class.java)!!
    }

    override suspend fun getByYandexId(yandexId: String): FirestoreCoffeeShop? {
        return bd.collection(COLLECTION_COFFEE_SHOPS).whereEqualTo(FIELD_YANDEX_ID, yandexId)
            .get().await().firstOrNull()?.toObject(FirestoreCoffeeShop::class.java)
    }

}