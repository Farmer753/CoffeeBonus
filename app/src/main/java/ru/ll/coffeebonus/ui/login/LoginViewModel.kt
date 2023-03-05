package ru.ll.coffeebonus.ui.login

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.domain.SessionRepository
import ru.ll.coffeebonus.domain.user.UserRepository
import ru.ll.coffeebonus.ui.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val sessionRepository: SessionRepository,
    val userRepository: UserRepository
) : BaseViewModel() {


    sealed class Event {
        object NavigateToProfile : Event()
        data class ShowMessage(val message: String) : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _progress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val progress: StateFlow<Boolean> = _progress

    init {
        viewModelScope.launch {
            sessionRepository.userLogined.filter { it }.collect {
                try {
                    _progress.emit(true)
                    val userExist = userRepository.userExist(userRepository.getAuthorizedUser().id)
                    if (!userExist) {
                        userRepository.saveUser(userRepository.getAuthorizedUser())
                    }
                    eventChannel.send(Event.NavigateToProfile)
                } catch (t: Throwable) {
                    Timber.e(t, "ошибка firestore")
                    eventChannel.send(
                        Event.ShowMessage(
                            t.message ?: "Unexpected error"
                        )
                    )
                    sessionRepository.logout()
                } finally {
                    _progress.emit(false)
                }
            }
        }
    }

    fun showProgress(visibility: Boolean) {
        viewModelScope.launch {
            _progress.emit(visibility)
        }
    }

}