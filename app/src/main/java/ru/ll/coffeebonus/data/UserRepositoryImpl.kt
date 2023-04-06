package ru.ll.coffeebonus.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ru.ll.coffeebonus.domain.user.FirestoreUser
import ru.ll.coffeebonus.domain.user.UserRepository
import timber.log.Timber

class UserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val bd: FirebaseFirestore
) : UserRepository {

    companion object {
        const val COLLECTION_USERS = "users"
        const val FIELD_ID = "id"
        const val FIELD_FAVORITE_COFFEE_SHOP = "favoriteCoffeeShop"
    }

    override suspend fun saveUser(firestoreUser: FirestoreUser) {
        bd.collection(COLLECTION_USERS).document(firestoreUser.id)
            .set(firestoreUser)
            .await()
    }

    override fun getAuthorizedUser(): FirestoreUser {
        return FirestoreUser(
            id = firebaseAuth.currentUser!!.uid,
            name = firebaseAuth.currentUser!!.displayName!!,
            avatarUrl = firebaseAuth.currentUser!!.photoUrl!!.toString(),
            email = firebaseAuth.currentUser!!.email!!,
            favoriteCoffeeShop = listOf()
        )
    }

    override suspend fun userExists(userId: String): Boolean {
        return bd.collection(COLLECTION_USERS).document(userId).get().await().exists()
    }

    override suspend fun getFirestoreUser(): FirestoreUser {
        return bd.collection(COLLECTION_USERS).document(getAuthorizedUser().id).get()
            .await()
            .toObject(FirestoreUser::class.java)!!
    }

    override suspend fun coffeeShopFavoriteExists(firestoreId: String): Boolean {
        return bd.collection(COLLECTION_USERS).whereEqualTo(FIELD_ID, getAuthorizedUser().id)
            .whereArrayContains(FIELD_FAVORITE_COFFEE_SHOP, firestoreId)
            .get().await().isEmpty.not()
    }

    override suspend fun addCoffeeFavorite(coffeeShopFirestoreId: String) {
        Timber.d("coffeeShopFirestoreId $coffeeShopFirestoreId")
        bd.collection(COLLECTION_USERS).document(getAuthorizedUser().id)
            .update(FIELD_FAVORITE_COFFEE_SHOP, FieldValue.arrayUnion(coffeeShopFirestoreId))
            .await()
    }

    override suspend fun removeCoffeeFavorite(coffeeShopFirestoreId: String) {
        bd.collection(COLLECTION_USERS).document(getAuthorizedUser().id)
            .update(FIELD_FAVORITE_COFFEE_SHOP, FieldValue.arrayRemove(coffeeShopFirestoreId))
            .await()
    }

    override suspend fun getFavoriteCoffeeShops(count: Short): List<String> {
        if (count > 10) {
            throw IllegalArgumentException("count не может быть больше 10")
        }
        return bd.collection(COLLECTION_USERS)
            .document(getAuthorizedUser().id)
            .collection(FIELD_FAVORITE_COFFEE_SHOP)
//                TODO не работает!
//            .limit(count.toLong())
            .get()
            .await()
            .toObjects(String::class.java)
    }
}