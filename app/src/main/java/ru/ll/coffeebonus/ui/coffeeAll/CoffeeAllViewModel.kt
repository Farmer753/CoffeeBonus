package ru.ll.coffeebonus.ui.coffeeAll

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.domain.CoffeeShop
import ru.ll.coffeebonus.domain.coffeeshop.CoffeeShopRepository
import ru.ll.coffeebonus.domain.coffeeshop.ModelConverter
import ru.ll.coffeebonus.domain.user.UserRepository
import ru.ll.coffeebonus.ui.BaseViewModel
import ru.ll.coffeebonus.ui.adapter.AdapterItem
import ru.ll.coffeebonus.ui.profile.CoffeeShopUiItem
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CoffeeAllViewModel @Inject constructor(
//    val sessionRepository: SessionRepository,
    val coffeeShopRepository: CoffeeShopRepository,
    val userRepository: UserRepository,
    val converter: ModelConverter,
) : BaseViewModel() {

    sealed class Event {
        object CloseScreen : Event()
        data class NavigateToCoffee(val coffeeShop: CoffeeShop) : Event()
    }

    private val eventChannel = Channel<CoffeeAllViewModel.Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _errorStateFlow = MutableStateFlow<String?>(null)
    val errorStateFlow = _errorStateFlow.asStateFlow()

    private val _loadingStateFlow = MutableStateFlow<Boolean>(false)
    val loadingStateFlow = _loadingStateFlow.asStateFlow()

    sealed class State {
        object Loading : State()
        data class Error(val message: String) : State()
        data class Success(val data: List<AdapterItem>) : State()
    }

    private val _stateFlow: MutableStateFlow<CoffeeAllViewModel.State> = MutableStateFlow(
        CoffeeAllViewModel.State.Loading
    )
    val stateFlow: StateFlow<CoffeeAllViewModel.State> = _stateFlow

    init {
        loadFavoriteCoffeeShop()
    }

    fun loadFavoriteCoffeeShop() {
        viewModelScope.launch {
            _stateFlow.emit(CoffeeAllViewModel.State.Loading)
            try {
                val favoriteCoffeeShopIds =
                    userRepository.getFirestoreUser().favoriteCoffeeShop
                val favoriteCoffeeShopAll = mutableListOf<CoffeeShopUiItem>()
                for (i in 0..favoriteCoffeeShopIds.size / 10) {
                    val toIndex = if ((i + 1) * 10 > favoriteCoffeeShopIds.size) {
                        favoriteCoffeeShopIds.size
                    } else {
                        (i + 1) * 10
                    }
                    val coffeeShopIdsToLoad = favoriteCoffeeShopIds.subList(i * 10, toIndex)
                    Timber.d("coffeeShopIdsToLoad $coffeeShopIdsToLoad")
                    val favoriteCoffeeShops =
                        coffeeShopRepository.getCoffeeShopsByIds(coffeeShopIdsToLoad)
                            .map { converter.convert(it) }
                    Timber.d("ЧТО ЭТО $favoriteCoffeeShops")
                    favoriteCoffeeShopAll += favoriteCoffeeShops
                }
                Timber.d("favoriteCoffeeShopIds $favoriteCoffeeShopIds")
//                if (Random.nextBoolean()) {
//                    throw IllegalStateException("рандомная ошибка")
//                }
                _stateFlow.emit(CoffeeAllViewModel.State.Success(favoriteCoffeeShopAll))
                Timber.d("favoriteCoffeeShopAll $favoriteCoffeeShopAll")
            } catch (t: Throwable) {
                Timber.e(t, "ошибка получения списка избранных кофеен")
                _stateFlow.emit(
                    CoffeeAllViewModel.State.Error(
                        t.message ?: "неизвестная ошибка coroutines"
                    )
                )
            }
        }
    }

    fun onCoffeeShopClick(coffeeShop: CoffeeShopUiItem) {
        viewModelScope.launch {
            eventChannel.send(CoffeeAllViewModel.Event.NavigateToCoffee(converter.convert(coffeeShop)))
        }
    }
}
