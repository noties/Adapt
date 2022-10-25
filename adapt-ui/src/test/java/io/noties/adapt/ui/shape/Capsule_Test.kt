package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import io.noties.adapt.ui.testutil.mockt
import io.noties.adapt.ui.testutil.value
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Capsule_Test {

    @Test
    fun clone() {
        val capsule = Capsule()
        val cloned = capsule.clone()
        assertEquals(Capsule::class.java, cloned::class.java)
    }

    @Test
    fun radius() {
        val inputs = listOf(
            Rect(10, 20) to 5F,
            Rect(100, 10) to 5F
        )

        for ((bounds, radius) in inputs) {
            val result = Capsule().radius(bounds)
            assertEquals(radius, result)
        }
    }

    @Test
    fun draw() {
        val inputs = listOf(
            Rect(20, 40),
            Rect(1000, 4)
        )
        for (input in inputs) {
            val canvas = mockt<Canvas>()
            val paint = mockt<Paint>()
            val capsule = Capsule()
            val radius = capsule.radius(input)
            capsule.drawShape(canvas, input, paint)
            val captor = argumentCaptor<RectF>()
            verify(canvas).drawRoundRect(
                captor.capture(),
                eq(radius),
                eq(radius),
                eq(paint)
            )
            assertEquals(RectF(input), captor.value)
        }
    }

    @Suppress("TestFunctionName")
    fun Rect(width: Int, height: Int): Rect = Rect(0, 0, width, height)
}