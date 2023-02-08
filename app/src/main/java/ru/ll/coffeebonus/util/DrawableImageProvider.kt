package ru.ll.coffeebonus.util

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.yandex.runtime.image.ImageProvider

class DrawableImageProvider(
    private val context: Context,
    @DrawableRes private val id: Int
) : ImageProvider() {
    override fun getImage() = ContextCompat.getDrawable(context, id)?.toBitmap()

    override fun getId() = id.toString()
}