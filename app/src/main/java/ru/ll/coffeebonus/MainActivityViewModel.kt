package ru.ll.coffeebonus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject


class MainActivityViewModel @AssistedInject constructor(
    @Assisted val stringId: String,
    val test: String
) : BaseViewModel() {

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            noteId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(noteId) as T
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