package ru.ll.coffeebonus.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import ru.ll.coffeebonus.domain.coffeeshop.FirestoreCoffeeShop
import ru.ll.coffeebonus.domain.user.FirestoreUser
import ru.ll.coffeebonus.domain.user.UserRepository
import ru.ll.coffeebonus.ui.util.snapshotFlow
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
            favoriteCoffeeShop = listOf(),
            bonusPrograms = listOf()
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

    override fun getFirestoreUserFlow(): Flow<FirestoreUser> {
        return bd.collection(COLLECTION_USERS).document(getAuthorizedUser().id)
            .snapshotFlow()
            .map {
                it.toObject(FirestoreUser::class.java)!!
            }
    }
}