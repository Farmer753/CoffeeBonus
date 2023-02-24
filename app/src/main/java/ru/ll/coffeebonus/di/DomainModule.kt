package ru.ll.coffeebonus.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ll.coffeebonus.data.SessionRepositoryImpl
import ru.ll.coffeebonus.domain.SessionRepository

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    fun provideFirebaseAuth(firebaseAuth: FirebaseAuth): SessionRepository {
        return SessionRepositoryImpl(firebaseAuth)
    }
}