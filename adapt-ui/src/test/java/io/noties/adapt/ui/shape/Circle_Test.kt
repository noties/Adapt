package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Rect
import io.noties.adapt.ui.testutil.mockt
import io.noties.adapt.ui.util.Gravity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Circle_Test {

    @Test
    fun clone() {
        val circle: Shape = CircleShape()
        val copied = circle.clone()
        assertEquals(CircleShape::class.java, copied::class.java)
    }

    @Test
    fun draw() {
        // padding?
        val rect = Rect(0, 0, 100, 20)
        val inputs = listOf(
            // null gravity does not affect incoming bounds
            null to Rect(0, 0, 100, 20),
            Gravity.leading to Rect(0, 0, 20, 20),
            Gravity.leading.top to Rect(0, 0, 20, 20),
            Gravity.center to Rect(40, 0, 60, 20),
            Gravity.trailing to Rect(80, 0, 100, 20),
            Gravity.trailing.bottom to Rect(80, 0, 100, 20),
        )
        for ((gravity, bounds) in inputs) {
            val canvas = mockt<Canvas>()
            val circle = CircleShape().also {
                if (gravity != null) it.gravity(gravity)
            }

            val r = circle.buildRect(rect, 10)
            assertEquals(gravity.toString(), bounds.toShortString(), r.toShortString())

            circle.drawShape(canvas, rect, mockt(), 1F)
            verify(canvas).drawCircle(
                eq(bounds.centerX().toFloat()),
                eq(bounds.centerY().toFloat()),
                eq(10F),
                any()
            )
        }
    }
}