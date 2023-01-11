package ru.ll.coffeebonus.ui.map

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    val test: String
) : BaseViewModel() {

    sealed class Event {
        data class NavigateToCoffee(
            val longitude: Float,
            val latitude: Float,
            val nameCoffee: String
        ) : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun mapClick(longitude: Float, latitude: Float, nameCoffee: String) {
        viewModelScope.launch {
            eventChannel.send(Event.NavigateToCoffee(longitude, latitude, nameCoffee))
        }
    }
}
