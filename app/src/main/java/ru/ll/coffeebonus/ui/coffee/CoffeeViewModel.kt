package ru.ll.coffeebonus.ui.coffee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.domain.CoffeeShop
import ru.ll.coffeebonus.domain.SessionRepository
import ru.ll.coffeebonus.domain.coffeeshop.CoffeeShopRepository
import ru.ll.coffeebonus.domain.coffeeshop.ModelConverter
import ru.ll.coffeebonus.ui.BaseViewModel
import timber.log.Timber

class CoffeeViewModel @AssistedInject constructor(
    @Assisted("coffeeShop") val coffeeShop: CoffeeShop,
    val sessionRepository: SessionRepository,
    val coffeeShopRepository: CoffeeShopRepository,
    val converter: ModelConverter
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
        data class ShowMessage(val message: String) : Event()
        object NavigationToLogin : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _loadingStateFlow = MutableStateFlow(false)
    val loadingStateFlow = _loadingStateFlow.asStateFlow()

    fun toggleFavorite() {
        Timber.d("toggleFavorite")
        if (sessionRepository.userLogined.value) {
            viewModelScope.launch {
                try {
                    _loadingStateFlow.emit(true)
//                    throw IllegalStateException("ошибка")
                    coffeeShopRepository.save(converter.convert(coffeeShop))
                    eventChannel.send(
                        Event.ShowMessage(
                            "Готово"
                        )
                    )
                } catch (t: Throwable) {
                    Timber.e(t, "ошибка сохранения кофейни в firestore")
                    eventChannel.send(
                        Event.ShowMessage(
                            t.message ?: "Unexpected error"
                        )
                    )
                } finally {
                    _loadingStateFlow.emit(false)
                }
            }
        } else {
            viewModelScope.launch {
                eventChannel.send(Event.ShowNeedAuthorisationMessage)
            }
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            eventChannel.send(Event.NavigationToLogin)
        }
    }
}
