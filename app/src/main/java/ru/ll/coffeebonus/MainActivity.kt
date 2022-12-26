package ru.ll.coffeebonus

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.ll.coffeebonus.di.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var test: String

    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    val viewModel: MainActivityViewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[MainActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as CoffeeBonusApp).appComponent.inject(this)
        Timber.d("переменная $test")
        val textView: TextView = findViewById<TextView>(R.id.abc)
        textView.setOnClickListener { viewModel.test() }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }
}