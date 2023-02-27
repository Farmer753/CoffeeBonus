package ru.ll.coffeebonus.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ll.coffeebonus.data.SessionRepositoryImpl
import ru.ll.coffeebonus.domain.SessionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides @Singleton
    fun provideFirebaseAuth(firebaseAuth: FirebaseAuth): SessionRepository {
        return SessionRepositoryImpl(firebaseAuth)
    }
}