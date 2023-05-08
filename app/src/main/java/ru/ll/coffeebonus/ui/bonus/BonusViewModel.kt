package ru.ll.coffeebonus.ui.bonus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.ll.coffeebonus.ui.BaseViewModel
import ru.ll.coffeebonus.ui.coffee.CoffeeViewModel

class BonusViewModel@AssistedInject constructor(): BaseViewModel() {
    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create() as T
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(): BonusViewModel
    }

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
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


}
