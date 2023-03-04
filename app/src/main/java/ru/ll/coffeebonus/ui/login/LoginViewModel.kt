package ru.ll.coffeebonus.ui.login

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
    }

    private val eventChannel = Channel<LoginViewModel.Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            sessionRepository.userLogined.filter { it }.collect {
                Timber.d("Значение $it")
//                eventChannel.send(LoginViewModel.Event.NavigateToProfile)
//                TODO вернуть навигацию и переписать
                val userExist = userRepository.userExist(userRepository.getAuthorizedUser().id)
                Timber.d("Существование юзера $userExist")
                userRepository.saveUserCoroutines(userRepository.getAuthorizedUser())
                Timber.d("Юзер из Firestore ${userRepository.getFirestoreUser()}")
            }

        }
    }

}