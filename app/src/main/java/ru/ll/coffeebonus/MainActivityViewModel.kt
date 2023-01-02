package ru.ll.coffeebonus

import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(val test: String) : BaseViewModel() {

    init {
        Timber.d("Переменная $test из init MainActivityViewModel")
    }

    fun test() {
        Timber.d("Новая переменная $test из метода test MainActivityViewModel")
    }
}