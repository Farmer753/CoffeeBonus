package ru.ll.coffeebonus.di

import dagger.Component
import ru.ll.coffeebonus.MainActivity

@Component(modules = [ApplicationModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
}