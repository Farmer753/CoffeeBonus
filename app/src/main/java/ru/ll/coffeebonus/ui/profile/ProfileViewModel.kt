package ru.ll.coffeebonus.ui.profile

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ll.coffeebonus.domain.SessionRepository
import ru.ll.coffeebonus.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val sessionRepository: SessionRepository
) : BaseViewModel() {

    fun logout() {
        sessionRepository.logout()
    }
}
