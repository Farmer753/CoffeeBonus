package ru.ll.coffeebonus.ui.bonus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.domain.CoffeeShop
import ru.ll.coffeebonus.domain.SessionRepository
import ru.ll.coffeebonus.domain.bonus.CoffeeBonusRepository
import ru.ll.coffeebonus.domain.bonus.FirestoreBonus
import ru.ll.coffeebonus.domain.coffeeshop.CoffeeShopRepository
import ru.ll.coffeebonus.domain.coffeeshop.FirestoreCoffeeShop
import ru.ll.coffeebonus.domain.coffeeshop.ModelConverter
import ru.ll.coffeebonus.domain.user.UserBonusPrograms
import ru.ll.coffeebonus.domain.user.UserRepository
import ru.ll.coffeebonus.ui.BaseViewModel
import ru.ll.coffeebonus.ui.coffee.CoffeeViewModel
import timber.log.Timber

class BonusViewModel @AssistedInject constructor(
    @Assisted("coffeeShop") val coffeeShop: CoffeeShop,
    val sessionRepository: SessionRepository,
    val coffeeShopRepository: CoffeeShopRepository,
    val userRepository: UserRepository,
    val coffeeBonusRepository: CoffeeBonusRepository,
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
        ): BonusViewModel
    }

    sealed class Event {
        object ShowNeedAuthorisationMessage : Event()
        data class ShowMessage(val message: String) : Event()
        object NavigationToLogin : Event()
    }

    private val eventChannel = Channel<BonusViewModel.Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _loadingStateFlow = MutableStateFlow<Boolean>(false)
    val loadingStateFlow = _loadingStateFlow.asStateFlow()

    private val _loadingButtonStateFlow = MutableStateFlow<Boolean>(false)
    val loadingButtonStateFlow = _loadingButtonStateFlow.asStateFlow()

    private val _errorStateFlow = MutableStateFlow<String?>(null)
    val errorStateFlow = _errorStateFlow.asStateFlow()

    private val _coffeeShopStateFlow = MutableStateFlow<FirestoreCoffeeShop?>(null)
    val coffeeShopStateFlow = _coffeeShopStateFlow.asStateFlow()

    private val _userCoffeeCountStateFlow = MutableStateFlow<Int>(0)
    val userCoffeeCountStateFlow = _userCoffeeCountStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            sessionRepository.userLogined.flatMapConcat {
                if (it) {
                    userRepository.getFirestoreUserFlow()
                } else {
                    flowOf(null)
                }
            }
                .combine(coffeeShopRepository.getByYandexIdFlow(coffeeShop.id)) { user, coffeeShop ->
                    user to coffeeShop
                }.collect { (user, coffeeShop) ->
                    Timber.d("combine $user $coffeeShop")
                    _coffeeShopStateFlow.emit(coffeeShop)
                    _userCoffeeCountStateFlow.emit(
                        user?.bonusPrograms?.find {
                            it.id == coffeeShop?.firestoreId
                        }?.count ?: 0
                    )
                }
        }
    }

    fun loadInitialData() {

        viewModelScope.launch {
            try {
                _loadingStateFlow.emit(true)
                _errorStateFlow.emit(null)
//                delay(5000)
//                if (Random.nextBoolean()) {
//                    throw IllegalStateException("рандомная ошибка")
//                }
//                TODO удалить следующую строчку и подумать перенести ли весь код ниже в источник данных кофейни и юзера
                val firestoreCoffeeShop = coffeeShopRepository.getByYandexId(coffeeShop.id)
                _coffeeShopStateFlow.emit(firestoreCoffeeShop)
                if (firestoreCoffeeShop == null) {
                    Timber.d("нет кофешопа")
                } else {
                    Timber.d("есть кофешоп")
                    if (firestoreCoffeeShop.coffeeBonus == null) {
                        Timber.d("нет бонусной программы")
                    } else {
                        Timber.d("есть бонусная программа")
                        val userAuthorized = sessionRepository.userLogined.value
                        if (userAuthorized) {
                            Timber.d("юзер авторизован")
                            val user = userRepository.getFirestoreUser()
                            Timber.d("юзер $user")
                            val currentCoffeeShopUserBonusProgram: UserBonusPrograms? =
                                user.bonusPrograms.find { it.id == firestoreCoffeeShop.firestoreId }
                            if (currentCoffeeShopUserBonusProgram != null) {
                                delay(1000)
                                _userCoffeeCountStateFlow.emit(currentCoffeeShopUserBonusProgram.count)
                            }
                        } else {
                            Timber.d("юзер не авторизован")
                        }
                    }
                }

            } catch (t: Throwable) {
                Timber.e(t, "ошибка")
                _errorStateFlow.emit(t.message ?: "Неизвестная ошибка")
            } finally {
                _loadingStateFlow.emit(false)
            }
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            eventChannel.send(BonusViewModel.Event.NavigationToLogin)
        }
    }

    fun addCoffeeButtonClick() {
        viewModelScope.launch {
            try {
                userRepository.upsertUserBonusProgram(
                    _coffeeShopStateFlow.value!!.firestoreId,
                    _userCoffeeCountStateFlow.value + 1
                )
            } catch (t: Throwable) {
                Timber.e(t, "ошибка")
            }
        }
    }

    fun clearCoffeeBonusButtonClick() {
        viewModelScope.launch {
            try {
                userRepository.upsertUserBonusProgram(
                    _coffeeShopStateFlow.value!!.firestoreId,
                    0
                )
            } catch (t: Throwable) {
                Timber.e(t, "ошибка")
            }
        }
    }

    fun editCoffeeBonusButtonClick() {
//        TODO("Not yet implemented")
    }

    fun deleteBonusButtonClick() {
        viewModelScope.launch {
            try {
                _loadingButtonStateFlow.emit(true)
                coffeeBonusRepository.delete(_coffeeShopStateFlow.value!!.firestoreId)
//                TODO подумать, можно ли легко удалить выпитые всеми юзерами чашки кофе в этой кофейне
            } catch (t: Throwable) {
                Timber.e(t, "ошибка")
//                TODO показать юзеру ошибку
            } finally {
                _loadingButtonStateFlow.emit(false)
            }
        }
    }

    fun createBonusProgram(count: Int) {
        if (!sessionRepository.userLogined.value) {
            viewModelScope.launch {
                eventChannel.send(BonusViewModel.Event.ShowNeedAuthorisationMessage)
            }
            return
        }
        viewModelScope.launch {
            try {
//                  TODO: показать лоудер
                //  получить кофейню по яндекс id
                val firestoreCoffeeShopFromServer =
                    coffeeShopRepository.getByYandexId(coffeeShop.id)
                // если кофейни нет - создать кофейню с помощью конвертера и сделать копию созданной кофейни с бонусной программой внутри и записать ее в базу
                if (firestoreCoffeeShopFromServer == null) {
                    coffeeShopRepository.createCoffeeShop(
                        converter.convert(coffeeShop).copy(coffeeBonus = FirestoreBonus(count))
                    )
                } else {
//                    если  кофейня есть - вызвать метод добавления бонусной программы в кофейне
                    coffeeBonusRepository.saveBonusProgram(
                        firestoreCoffeeShopFromServer.firestoreId,
                        FirestoreBonus(count)
                    )
                }
                val coffeeShopFromServer =
                    coffeeShopRepository.getByYandexIdFromServer(coffeeShop.id)
//                TODO не выводит в логи
                Timber.d("coffeeShopFromServer $coffeeShopFromServer")
                if (coffeeShopFromServer?.coffeeBonus == null) {
                    throw IllegalStateException("Не удалось убедиться в создании бонусной программы")
                }
            } catch (t: Throwable) {
//                TODO: показать ошибку
                Timber.e(t, "ошибка")
            } finally {
//                TODO убрать лоудер
            }

        }
    }
//    Заинжектить coffeeShop
//     CoffeeShopRepository, SessionRepository, UserRepository

//       StateFlow для прогресса, для ошибки, для бонусной программы, для количества выпитых чашек кофе юзером
//        Эмитить в них из if/else

//      Запустить крутилку
//      Получить кофейню из firestore
//        Если кофейни нет - отображаем контейнер с созданием бонусной программы
//        Если кофейня есть - проверяем есть ли в ней бонусная программа
//             Если бонусной программы нет - отображаем контейнер с созданием бонусной программы
//             Если бонусная программа есть - проверяем, есть ли авторизация
//                Если авторизации нет - то скрываем кнопки редактировать/удалить/добавить и показываем кнопку авторизоваться
//                Если авторизация есть - узнаем сколько ля этой кофейни юзер выпил чашек кофе
//     Отловить ошибку, показать кнопку retry
//     Закончить крутилку в любом случае
}



