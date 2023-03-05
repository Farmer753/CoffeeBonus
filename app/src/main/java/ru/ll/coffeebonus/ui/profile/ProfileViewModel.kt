package ru.ll.coffeebonus.ui.profile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.domain.SessionRepository
import ru.ll.coffeebonus.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val sessionRepository: SessionRepository
) : BaseViewModel() {

    sealed class Event {
        object CloseScreen : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun logout() {
        sessionRepository.logout()
        viewModelScope.launch {
            eventChannel.send(Event.CloseScreen)
        }
    }
}
