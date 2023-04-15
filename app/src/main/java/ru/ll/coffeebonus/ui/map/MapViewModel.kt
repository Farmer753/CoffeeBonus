package ru.ll.coffeebonus.ui.map

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.domain.CoffeeShop
import ru.ll.coffeebonus.domain.SessionRepository
import ru.ll.coffeebonus.domain.coffeeshop.CoffeeShopRepository
import ru.ll.coffeebonus.domain.user.UserRepository
import ru.ll.coffeebonus.ui.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    val test: String,
    val sessionRepository: SessionRepository,
    val coffeeShopRepository: CoffeeShopRepository,
    val userRepository: UserRepository
) : BaseViewModel() {

    sealed class Event {
        data class NavigateToCoffee(
            val coffeeShop: CoffeeShop
        ) : Event()

        object NavigateToProfile : Event()
        object NavigateToLogin : Event()
    }

    val coffeeShops = mutableMapOf<String, CoffeeShop>()
    private val _searchResult = MutableStateFlow<List<CoffeeShop>>(listOf())
    val searchResult = _searchResult.asStateFlow().debounce(1000)

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun mapClick(coffeeShop: CoffeeShop) {
        viewModelScope.launch {
            eventChannel.send(Event.NavigateToCoffee(coffeeShop))
        }
    }
//TODO полученный список кофешопов преобразовать в кофешопы на карте,
// сделав запросы в firestore на предмет наличия кофеен в базе и избранности кофеен юзера.
// И только после этого эмитить в searchResult
//    Считаем нормальным игрнорировать ошибки обработки данных

//    TODO различные варианты отображения маркера на карте в зависимости от наличия кофейни в базе
//     и избранности кофейни

//    TODO посмотреть, насколько сильно все сломается в коде,
//     если мы будем его запускать для незалогиненого юзера

    fun onSearchResult(result: List<CoffeeShop>) {
        result.forEach {
            coffeeShops.put(it.id, it)
        }
        viewModelScope.launch {
//            _searchResult.emit(coffeeShops.values.toList())
            val favoriteCoffeeShopIds = userRepository.getFirestoreUser().favoriteCoffeeShop
            val coffeeResult = coffeeShopRepository.getByYandexId(result.map { it.id })
            Timber.d("результат $coffeeResult")
            val coffeeShopsOnMap = result.map { coffeeShop ->
                val firestoreId = coffeeResult.find { it.id == coffeeShop.id }?.firestoreId
                CoffeeShopOnMap(
                    firestoreId = firestoreId,
                    id= coffeeShop.id,
                    name = coffeeShop.name,
                    address = coffeeShop.address,
                    longitude = coffeeShop.longitude,
                    latitude = coffeeShop.latitude,
                    favorite = favoriteCoffeeShopIds.contains(firestoreId)
                )
            }
            Timber.d("coffeeShopsOnMap $coffeeShopsOnMap")
        }
    }

    fun profileClick() {
        viewModelScope.launch {
            if (sessionRepository.userLogined.value) {
                eventChannel.send(Event.NavigateToProfile)
            } else {
                eventChannel.send(Event.NavigateToLogin)
            }
        }
    }

}
