package ru.ll.coffeebonus.ui.map

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ll.coffeebonus.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    val test: String): BaseViewModel() {


}
