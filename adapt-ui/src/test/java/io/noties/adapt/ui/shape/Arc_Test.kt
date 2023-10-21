package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import io.noties.adapt.ui.testutil.mockt
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
class Arc_Test {

    @Test
    fun clone() {
        val arc: Shape = ArcShape(3F, 97F, false).clone()
        assertEquals(ArcShape::class.java, arc::class.java)

        arc as ArcShape
        assertEquals("startAngle", 3F, arc.startAngle)
        assertEquals("sweepAngle", 97F, arc.sweepAngle)
        assertEquals("useCenter", false, arc.useCenter)
    }

    @Test
    fun arc() {
        val inputs = listOf(
            Triple(null, null, null),
            Triple(1F, null, null),
            Triple(null, 2F, null),
            Triple(null, null, false),
            Triple(3F, 4F, true)
        )

        val arc = ArcShape(-1F, -2F)

        for (input in inputs) {
            // assertNotEquals... what to do with `useCenter`?
            val (startAngle, sweepAngle, useCenter) = input

            val result = arc.copy {
                arc(startAngle, sweepAngle, useCenter)
            }

            assertEquals(
                "startAngle:$input",
                startAngle ?: arc.startAngle,
                result.startAngle
            )

            assertEquals(
                "sweepAngle:$input",
                sweepAngle ?: arc.sweepAngle,
                result.sweepAngle
            )

            assertEquals(
                "useCenter:$input",
                useCenter ?: arc.useCenter,
                result.useCenter
            )
        }
    }

    @Test
    fun draw() {
        val arc = ArcShape(-1F, -2F, false)
        val canvas = mockt<Canvas>()
        val bounds = Rect(0, 0, 55, 33)
        arc.drawShape(canvas, bounds, mockt(), 1F)
        verify(canvas).drawArc(
            eq(RectF(bounds)),
            eq(-1F),
            eq(-2F),
            eq(false),
            any()
        )
    }
}