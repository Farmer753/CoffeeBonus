package ru.ll.coffeebonus.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

//@HiltViewModel
class LoginViewModel @AssistedInject constructor(
    @Assisted("openProfile") val openProfile: Boolean,
    val sessionRepository: SessionRepository,
    val userRepository: UserRepository
) : BaseViewModel() {

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            openProfile: Boolean
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(openProfile) as T
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("openProfile") openProfile: Boolean
        ): LoginViewModel
    }

    sealed class Event {
        object NavigateToProfile : Event()
        data class ShowMessage(val message: String) : Event()
        object CloseScreen : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _progress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val progress: StateFlow<Boolean> = _progress

    init {
        Timber.d("Аргумент $openProfile")
        viewModelScope.launch {
            sessionRepository.userLogined.filter { it }.collect {
                try {
                    _progress.emit(true)
                    val userExist = userRepository.userExists(userRepository.getAuthorizedUser().id)
                    if (!userExist) {
                        userRepository.saveUser(userRepository.getAuthorizedUser())
                    }
                    if (openProfile) {
                        eventChannel.send(Event.NavigateToProfile)
                    } else {
                        eventChannel.send(Event.CloseScreen)
                    }
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