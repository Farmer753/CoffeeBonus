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

    override suspend fun getByYandexId(yandexIds: List<String>): List<FirestoreCoffeeShop> {
        if (yandexIds.size > 10) {
            throw IllegalArgumentException("Список id кофеен не может быть больше 10")
        }
        return bd.collection(COLLECTION_COFFEE_SHOPS).whereIn(FIELD_YANDEX_ID, yandexIds)
            .get().await().toObjects(FirestoreCoffeeShop::class.java)
    }

    override suspend fun getCoffeeShopsByIds(listId: List<String>): List<FirestoreCoffeeShop> {
        if (listId.size > 10) {
            throw IllegalArgumentException("Список id кофеен не может быть больше 10")
        }
        return bd.collection(COLLECTION_COFFEE_SHOPS).whereIn(FIELD_FIRESTORE_ID, listId)
            .get().await().toObjects(FirestoreCoffeeShop::class.java)
    }

}