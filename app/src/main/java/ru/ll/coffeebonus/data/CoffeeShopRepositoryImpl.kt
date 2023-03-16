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
    }

    override suspend fun save(firestoreCoffeeShop: FirestoreCoffeeShop) {
        bd.collection(COLLECTION_COFFEE_SHOPS).document()
            .set(firestoreCoffeeShop)
            .await()
    }

}