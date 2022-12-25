package ru.ll.coffeebonus.di

import dagger.Module
import dagger.Provides

@Module
class ApplicationModule {
    @Provides
    fun provideString(): String {
        return "test"
    }
}