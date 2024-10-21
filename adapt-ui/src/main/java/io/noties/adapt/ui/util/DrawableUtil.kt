package io.noties.adapt.ui.util

import android.graphics.Rect
import android.graphics.drawable.Drawable
import kotlin.math.roundToInt

fun Rect.scale(height: Int): Rect {
    // 10x20
    //  height is 12 =>
    val w = width()
    val h = height()
    val ratio = (w.toFloat()) / h
    val targetWidth = (height * ratio).roundToInt()
    return Rect(
        left,
        top,
        left + targetWidth,
        top + height
    )
}

fun Drawable.createIntrinsicBounds() = Rect(0, 0, intrinsicWidth, intrinsicHeight)

fun Drawable.applyIntrinsicBounds() {
    bounds = createIntrinsicBounds()
}