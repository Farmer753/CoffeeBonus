package ru.ll.coffeebonus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.domain.SessionRepository
import timber.log.Timber


class MainActivityViewModel @AssistedInject constructor(
    @Assisted val stringId: String,
    val test: String,
    val sessionRepository: SessionRepository
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
        viewModelScope.launch {
            sessionRepository.userLogined.collect { Timber.d("Значение $it") }
        }
    }

    fun test() {
        Timber.d("Новая переменная $test из метода test MainActivityViewModel")
    }
}