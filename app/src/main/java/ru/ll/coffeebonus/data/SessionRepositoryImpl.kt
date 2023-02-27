package ru.ll.coffeebonus.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.ll.coffeebonus.domain.SessionRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : SessionRepository {

    private val _userLogined = MutableStateFlow(firebaseAuth.currentUser != null)

    override val userLogined = _userLogined.asStateFlow()

    init {
        firebaseAuth.addAuthStateListener {
            _userLogined.tryEmit(it.currentUser != null)
            Timber.d("выводим из init ${it.currentUser}")
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

}