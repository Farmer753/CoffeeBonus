package ru.ll.coffeebonus.ui.util

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.util.DrawableImageProvider

private const val ZOOM = 15f
private const val AZIMUTH = 0f
private const val TILT = 0f

fun MapView.showMarker(latitude: Float, longitude: Float) {
    setNoninteractive(true)
    val imageProvider = DrawableImageProvider(
        context,
        R.drawable.ic_action_name
    )
    map.mapObjects.clear()
    map.mapObjects.addPlacemark(
        Point(
            latitude.toDouble(),
            longitude.toDouble()
        ),
        imageProvider
    )
    map.move(
        CameraPosition(
            Point(
                latitude.toDouble(),
                longitude.toDouble()
            ),
            ZOOM, AZIMUTH, TILT
        )
    )
}