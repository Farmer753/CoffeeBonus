package ru.ll.coffeebonus.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.ll.coffeebonus.domain.SessionRepository
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(firebaseAuth: FirebaseAuth) : SessionRepository {
    private val _userLogined: MutableStateFlow<Boolean> =
        MutableStateFlow<Boolean>(firebaseAuth.currentUser != null)
    override val userLogined: StateFlow<Boolean> = _userLogined

}