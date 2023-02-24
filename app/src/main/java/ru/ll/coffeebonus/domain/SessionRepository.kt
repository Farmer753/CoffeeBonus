package ru.ll.coffeebonus.domain

import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {
    val userLogined: StateFlow<Boolean>
}