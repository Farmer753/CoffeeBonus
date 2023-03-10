package ru.ll.coffeebonus.ui.profile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.domain.SessionRepository
import ru.ll.coffeebonus.domain.user.FirestoreUser
import ru.ll.coffeebonus.domain.user.UserRepository
import ru.ll.coffeebonus.ui.BaseViewModel
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val sessionRepository: SessionRepository,
    val userRepository: UserRepository
) : BaseViewModel() {

    sealed class Event {
        object CloseScreen : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _errorStateFlow = MutableStateFlow<String?>(null)
    val errorStateFlow = _errorStateFlow.asStateFlow()

    private val _userStateFlow = MutableStateFlow<FirestoreUser?>(null)
    val userStateFlow = _userStateFlow.asStateFlow()

    private val _loadingStateFlow = MutableStateFlow<Boolean>(false)
    val loadingStateFlow = _loadingStateFlow.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            try {
                _errorStateFlow.emit(null)
                _loadingStateFlow.emit(true)
                val user = userRepository.getFirestoreUser()
//                if (Random.nextBoolean()){
//                    throw IllegalStateException("рандомная ошибка")
//                }
                _userStateFlow.emit(user)
                Timber.d("юзер $user")
            } catch (t: Throwable) {
                Timber.e(t, "ошибка загрузки юзера из firestore")
                _errorStateFlow.emit(t.message ?: "Неизвестная ошибка")
            } finally {
                _loadingStateFlow.emit(false)
            }
        }
    }

    fun logout() {
        sessionRepository.logout()
        viewModelScope.launch {
            eventChannel.send(Event.CloseScreen)
        }
    }
}
