package ru.ll.coffeebonus.ui.coffee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.ll.coffeebonus.ui.BaseViewModel

class CoffeeViewModel @AssistedInject constructor(
    @Assisted("latitude") val latitude: Float,
    @Assisted("longitude") val longitude: Float,
    @Assisted("nameCoffee") val nameCoffee: String
) : BaseViewModel() {

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            latitude: Float,
            longitude: Float,
            nameCoffee: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(latitude, longitude, nameCoffee) as T
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("latitude") latitude: Float,
            @Assisted("longitude") longitude: Float,
            @Assisted("nameCoffee") nameCoffee: String
        ): CoffeeViewModel
    }
}
