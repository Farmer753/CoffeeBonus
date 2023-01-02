package ru.ll.coffeebonus

//import ru.ll.coffeebonus.di.ViewModelFactory
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import dagger.hilt.android.AndroidEntryPoint
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

//    @Inject
//    lateinit var viewModelFactory: ViewModelFactory

    lateinit var mapview: MapView


//    val viewModel: MainActivityViewModel by lazy {
//        ViewModelProvider(
//            this,
//            viewModelFactory
//        )[MainActivityViewModel::class.java]
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        Timber.d("переменная $test")
        val textView: TextView = findViewById<TextView>(R.id.abc)
        textView.setOnClickListener { viewModel.test() }
        mapview = findViewById<MapView>(R.id.mapview)
        mapview.map.move(
            CameraPosition(
                Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0f),
            null
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }

    override fun onStop() {
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview.onStart()
    }
}