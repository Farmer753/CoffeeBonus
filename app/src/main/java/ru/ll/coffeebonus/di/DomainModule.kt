package ru.ll.coffeebonus.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ll.coffeebonus.data.CoffeeBonusRepositoryImpl
import ru.ll.coffeebonus.data.CoffeeShopRepositoryImpl
import ru.ll.coffeebonus.data.SessionRepositoryImpl
import ru.ll.coffeebonus.data.UserRepositoryImpl
import ru.ll.coffeebonus.domain.SessionRepository
import ru.ll.coffeebonus.domain.bonus.CoffeeBonusRepository
import ru.ll.coffeebonus.domain.coffeeshop.CoffeeShopRepository
import ru.ll.coffeebonus.domain.coffeeshop.ModelConverter
import ru.ll.coffeebonus.domain.user.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    @Singleton
    fun provideSessionRepository(firebaseAuth: FirebaseAuth): SessionRepository {
        return SessionRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        bd: FirebaseFirestore
    ): UserRepository {
        return UserRepositoryImpl(firebaseAuth, bd)
    }

    @Provides
    @Singleton
    fun provideCoffeeShopRepository(
        bd: FirebaseFirestore
    ): CoffeeShopRepository {
        return CoffeeShopRepositoryImpl(bd)
    }

    @Provides
    @Singleton
    fun provideCoffeeBonusRepository(
        bd: FirebaseFirestore
    ): CoffeeBonusRepository {
        return CoffeeBonusRepositoryImpl(bd)
    }

    @Provides
    fun provideModelConverter(): ModelConverter {
        return ModelConverter()
    }
}