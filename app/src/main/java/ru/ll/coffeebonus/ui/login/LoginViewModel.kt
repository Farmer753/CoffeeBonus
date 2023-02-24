package ru.ll.coffeebonus.ui.login

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.ll.coffeebonus.domain.SessionRepository
import ru.ll.coffeebonus.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val sessionRepository: SessionRepository
) : BaseViewModel() {

    val loginStateObservable: StateFlow<Boolean> = sessionRepository.userLogined

//    fun onLoginSuccess() {
//        router.newRootChain(Screens.DialogsScreen())
//    }

}