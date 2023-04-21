package ru.ll.coffeebonus.util

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.yandex.runtime.image.ImageProvider

class ProgramaticalDrawableImageProvider(
    val drawable: Drawable
) : ImageProvider() {
    override fun getImage() = drawable.toBitmap()

    override fun getId() = drawable.toString()
}