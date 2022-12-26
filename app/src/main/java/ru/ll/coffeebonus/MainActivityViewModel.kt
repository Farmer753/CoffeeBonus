package ru.ll.coffeebonus

import timber.log.Timber
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(val test: String) : BaseViewModel() {

    init {
        Timber.d("Переменная $test")
    }

    fun test() {
        Timber.d("Новая переменная $test")
    }
}