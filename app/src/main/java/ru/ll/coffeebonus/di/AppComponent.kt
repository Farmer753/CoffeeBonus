package ru.ll.coffeebonus.di

import dagger.Component
import ru.ll.coffeebonus.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
}