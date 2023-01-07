package ru.ll.coffeebonus.ui.coffee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.ll.coffeebonus.ui.BaseViewModel

class CoffeeViewModel @AssistedInject constructor(
    @Assisted("latitude") val latitude: Float,
    @Assisted("longitude") val longitude: Float
) : BaseViewModel() {

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            latitude: Float,
            longitude: Float
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(latitude, longitude) as T
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("latitude") latitude: Float,
            @Assisted("longitude") longitude: Float
        ): CoffeeViewModel
    }
}
