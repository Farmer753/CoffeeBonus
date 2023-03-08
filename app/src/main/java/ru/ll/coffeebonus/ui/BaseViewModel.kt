package ru.ll.coffeebonus.ui

import androidx.lifecycle.ViewModel
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {


    init {
        Timber.d("BaseViewModel init: ${this::class.java}")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared ${javaClass.simpleName}")
    }

}