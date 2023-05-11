package ru.ll.coffeebonus.domain.user

data class FirestoreUser @JvmOverloads constructor(
    val id: String = "",
    val name: String = "",
    val avatarUrl: String = "",
    val email: String = "",
    val favoriteCoffeeShop: List<String> = listOf(),
    val bonusPrograms: List<UserBonusPrograms> = listOf()
)

data class UserBonusPrograms @JvmOverloads constructor(
    val id: String = "",
    val count: Int = 0
)