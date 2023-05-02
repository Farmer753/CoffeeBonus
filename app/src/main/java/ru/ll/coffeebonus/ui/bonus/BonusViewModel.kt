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
}
