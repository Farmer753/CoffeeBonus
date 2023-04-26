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
import ru.ll.coffeebonus.domain.coffeeshop.FirestoreCoffeeShop
import ru.ll.coffeebonus.domain.coffeeshop.ModelConverter
import ru.ll.coffeebonus.domain.user.UserRepository
import ru.ll.coffeebonus.ui.BaseViewModel
import timber.log.Timber

class CoffeeViewModel @AssistedInject constructor(
    @Assisted("coffeeShop") val coffeeShop: CoffeeShop,
    val sessionRepository: SessionRepository,
    val coffeeShopRepository: CoffeeShopRepository,
    val userRepository: UserRepository,
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
        object CloseScreen : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _loadingFavoriteStateFlow = MutableStateFlow(false)
    val loadingFavoriteStateFlow = _loadingFavoriteStateFlow.asStateFlow()

    private val _loadingStateFlow = MutableStateFlow<Boolean>(false)
    val loadingStateFlow = _loadingStateFlow.asStateFlow()

    private val _errorStateFlow = MutableStateFlow<String?>(null)
    val errorStateFlow = _errorStateFlow.asStateFlow()

    private val _favoriteCoffeeShopStateFlow = MutableStateFlow<Boolean>(false)
    val favoriteCoffeeShopStateFlow = _favoriteCoffeeShopStateFlow.asStateFlow()

    private val _firestoreCoffeeShopStateFlow = MutableStateFlow<FirestoreCoffeeShop?>(null)
    val firestoreCoffeeShopStateFlow = _firestoreCoffeeShopStateFlow.asStateFlow()

    init {
        loadCoffeeShop()
    }

    fun toggleFavorite() {
        Timber.d("toggleFavorite")
        if (sessionRepository.userLogined.value) {
            viewModelScope.launch {
                try {
                    _loadingFavoriteStateFlow.emit(true)
                    val coffeeShopExist = coffeeShopRepository.exists(coffeeShop.id)
//                    throw IllegalStateException("ошибка")
                    if (!coffeeShopExist) {
                        coffeeShopRepository.save(converter.convert(coffeeShop))
                    }
                    val firestoreCoffeeShop = coffeeShopRepository.getByYandexId(coffeeShop.id)!!
                    val coffeeShopFavoriteExist =
                        userRepository.coffeeShopFavoriteExists(firestoreCoffeeShop.firestoreId)
                    Timber.d("coffeeShopFavoriteExist $coffeeShopFavoriteExist")
                    if (!coffeeShopFavoriteExist) {
                        userRepository.addCoffeeFavorite(firestoreCoffeeShop.firestoreId)
                        _favoriteCoffeeShopStateFlow.emit(true)
                    } else {
                        userRepository.removeCoffeeFavorite(firestoreCoffeeShop.firestoreId)
                        _favoriteCoffeeShopStateFlow.emit(false)
                    }
                    eventChannel.send(Event.ShowMessage("Готово"))
                } catch (t: Throwable) {
                    Timber.e(t, "ошибка сохранения кофейни в firestore")
                    eventChannel.send(
                        Event.ShowMessage(
                            t.message ?: "Unexpected error"
                        )
                    )
                } finally {
                    _loadingFavoriteStateFlow.emit(false)
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

    fun loadCoffeeShop() {
        viewModelScope.launch {
            try {
                _loadingStateFlow.emit(true)
                _errorStateFlow.emit(null)
//                if (Random.nextBoolean()){
//                    throw IllegalStateException("рандомная ошибка")
//                }
                val coffeeShopFirestore = coffeeShopRepository.getByYandexId(coffeeShop.id)
                _firestoreCoffeeShopStateFlow.emit(coffeeShopFirestore)
                if (coffeeShopFirestore != null && sessionRepository.userLogined.value) {
                    val coffeeShopFavoriteExist =
                        userRepository.coffeeShopFavoriteExists(coffeeShopFirestore.firestoreId)
                    _favoriteCoffeeShopStateFlow.emit(coffeeShopFavoriteExist)
                }
            } catch (t: Throwable) {
                Timber.e(t, "ошибка получения кофейни в firestore")
                _errorStateFlow.emit(t.message ?: "Неизвестная ошибка")
            } finally {
                _loadingStateFlow.emit(false)
            }

        }

    }

    fun onCloseClick() {
        viewModelScope.launch {
            eventChannel.send(Event.CloseScreen)
        }
    }
}
