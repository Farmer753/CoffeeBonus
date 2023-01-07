package ru.ll.coffeebonus

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class CoffeeBonusApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        MapKitFactory.setApiKey("2070246f-f8be-4796-be12-73040a223348")
    }
}