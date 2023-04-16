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

    val coffeeShops = mutableMapOf<String, CoffeeShopOnMap>()
    private val _searchResult = MutableStateFlow<List<CoffeeShopOnMap>>(listOf())
    val searchResult = _searchResult.asStateFlow().debounce(1000)

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun mapClick(coffeeShop: CoffeeShopOnMap) {
        viewModelScope.launch {
            eventChannel.send(
                Event.NavigateToCoffee(
                    CoffeeShop(
                        firestoreId = coffeeShop.firestoreId,
                        id = coffeeShop.id,
                        name = coffeeShop.name,
                        address = coffeeShop.address,
                        longitude = coffeeShop.longitude,
                        latitude = coffeeShop.latitude
                    )
                )
            )
        }
    }

//    TODO различные варианты отображения маркера на карте в зависимости от наличия кофейни в базе
//     и избранности кофейни

    fun onSearchResult(coffeeShopsFromYandexSearch: List<CoffeeShop>) {
        if (coffeeShopsFromYandexSearch.isEmpty()) {
            return
        }
        viewModelScope.launch {
            try {
                val favoriteCoffeeShopIds = if (sessionRepository.userLogined.value) {
                    userRepository.getFirestoreUser().favoriteCoffeeShop
                } else {
                    listOf()
                }
                val coffeeResult =
                    coffeeShopRepository.getByYandexId(coffeeShopsFromYandexSearch.map { it.id })
                val coffeeShopsOnMap = coffeeShopsFromYandexSearch.map { coffeeShop ->
                    val firestoreId = coffeeResult.find { it.id == coffeeShop.id }?.firestoreId
                    CoffeeShopOnMap(
                        firestoreId = firestoreId,
                        id = coffeeShop.id,
                        name = coffeeShop.name,
                        address = coffeeShop.address,
                        longitude = coffeeShop.longitude,
                        latitude = coffeeShop.latitude,
                        favorite = favoriteCoffeeShopIds.contains(firestoreId)
                    )
                }
                coffeeShopsOnMap.forEach {
                    coffeeShops[it.id] = it
                }
                _searchResult.emit(coffeeShops.values.toList())
            } catch (t: Throwable) {
                Timber.e(t, "Какая-то ошибка")
            }
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
