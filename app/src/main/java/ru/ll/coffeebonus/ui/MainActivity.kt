package ru.ll.coffeebonus.ui

//import ru.ll.coffeebonus.di.ViewModelFactory
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.ll.coffeebonus.R
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var test: String

    @Inject
    lateinit var viewModelAssistedFactory: MainActivityViewModel.Factory

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModel.provideFactory(viewModelAssistedFactory, "String Id")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.d("переменная $test")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }

}