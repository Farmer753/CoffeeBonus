package ru.ll.coffeebonus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var test: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as CoffeeBonusApp).appComponent.inject(this)
        Timber.d("переменная $test")
    }
}