package ru.ll.coffeebonus.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ru.ll.coffeebonus.domain.user.FirestoreUser
import ru.ll.coffeebonus.domain.user.UserRepository

class UserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val bd: FirebaseFirestore
) : UserRepository {

    companion object {
        const val COLLECTION_USERS = "users"
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
            email = firebaseAuth.currentUser!!.email!!
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
}