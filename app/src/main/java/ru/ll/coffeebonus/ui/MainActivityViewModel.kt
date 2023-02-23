package ru.ll.coffeebonus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import timber.log.Timber


class MainActivityViewModel @AssistedInject constructor(
    @Assisted val stringId: String,
    val test: String
) : BaseViewModel() {

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            stringId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(stringId) as T
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(stringId: String): MainActivityViewModel
    }

    init {
        Timber.d("Переменная $test из init MainActivityViewModel")
        Timber.d("Переменная $stringId из init MainActivityViewModel")
    }

    fun test() {
        Timber.d("Новая переменная $test из метода test MainActivityViewModel")
    }
}