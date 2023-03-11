package ru.ll.coffeebonus.ui.coffee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.domain.CoffeeShop
import ru.ll.coffeebonus.domain.SessionRepository
import ru.ll.coffeebonus.ui.BaseViewModel
import timber.log.Timber

class CoffeeViewModel @AssistedInject constructor(
    @Assisted("coffeeShop") val coffeeShop: CoffeeShop,
    val sessionRepository: SessionRepository
) : BaseViewModel() {

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            coffeeShop: CoffeeShop
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(coffeeShop) as T
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("coffeeShop") coffeeShop: CoffeeShop
        ): CoffeeViewModel
    }

    sealed class Event {
        object ShowNeedAuthorisationMessage : Event()
    }

    private val eventChannel = Channel<CoffeeViewModel.Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun toggleFavorite() {
        Timber.d("toggleFavorite")
        if (sessionRepository.userLogined.value) {
//            TODO
        } else {
            viewModelScope.launch {
                eventChannel.send(Event.ShowNeedAuthorisationMessage)
            }
        }
    }
}
