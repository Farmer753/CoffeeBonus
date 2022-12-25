package ru.ll.coffeebonus

import android.app.Application
import ru.ll.coffeebonus.di.AppComponent
import ru.ll.coffeebonus.di.DaggerAppComponent
import timber.log.Timber

class CoffeeBonusApp: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        appComponent = DaggerAppComponent.create()
    }
}