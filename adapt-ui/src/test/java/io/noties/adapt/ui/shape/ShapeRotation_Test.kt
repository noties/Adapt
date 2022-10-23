package io.noties.adapt.ui.shape

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ShapeRotation_Test {

    @Test
    fun `degrees - empty`() {
        // no degrees should not draw
        val rotation = Shape.Rotation()
        Assert.assertNull("rotation.degrees", rotation.degrees)

        val canvas = mockt<Canvas>()
        rotation.draw(canvas, mockt())

        verifyNoInteractions(canvas)
    }

    @Test
    fun `center - bounds`() {
        // center x and center y must resolve proper bounds from width/height
        //  or fallback to supplied bounds centerX and centerY

        // verify density
        Assert.assertEquals("density", 1F, Resources.getSystem().displayMetrics.density)

        val rect = Rect(0, 0, 100, 200)

        // if cx and cy are null -> use center of the rectangle
        val inputs = listOf(
            (null to null) to (null to null),
            (Dimension.Exact(3) to null) to (3F to null),
            (Dimension.Relative(0.25F) to null) to (25F to null),
            (null to Dimension.Exact(4)) to (null to 4F),
            (null to Dimension.Relative(0.25F) to (null to 50F)),
            (Dimension.Exact(5) to Dimension.Relative(0.1F)) to (5F to 20F),
            (Dimension.Relative(0.1F) to Dimension.Exact(7)) to (10F to 7F)
        )

        val degrees = 45F

        for (input in inputs) {
            val canvas = mockt<Canvas>()
            val rotation = Shape.Rotation(degrees).also {
                it.centerX = input.first.first
                it.centerY = input.first.second
            }
            rotation.draw(canvas, Rect(rect))

            val cx = input.second.first ?: rect.centerX().toFloat()
            val cy = input.second.second ?: rect.centerY().toFloat()

            verify(canvas).rotate(
                eq(degrees),
                eq(cx),
                eq(cy)
            )
        }
    }

    @Test
    fun copy() {
        val inputs = listOf(
            Shape.Rotation(),
            Shape.Rotation(47F),
            Shape.Rotation(centerX = Dimension.Exact(98)),
            Shape.Rotation(centerY = Dimension.Relative(123F)),
            Shape.Rotation(100F, Dimension.Relative(0.1F), Dimension.Exact(9))
        )

        // just copy
        for (input in inputs) {
            val result = input.copy()
            // verify new instance
            Assert.assertNotEquals(
                "input:$input result:$result",
                System.identityHashCode(input),
                System.identityHashCode(result)
            )
            // verify contents
            Assert.assertEquals(
                "input:$input result:$result",
                input,
                result
            )
        }

        // copy with block
        val base = Shape.Rotation(99.9F, Dimension.Exact(10), Dimension.Relative(0.9F))

        for (input in inputs) {
            Assert.assertNotEquals("$base - $input", base, input)
            val result = input.copy {
                degrees = base.degrees
                centerX = base.centerX
                centerY = base.centerY
            }
            // verify new instance
            Assert.assertNotEquals(
                "input:$input result:$result",
                System.identityHashCode(input),
                System.identityHashCode(result)
            )
            // verify same contents
            Assert.assertEquals(
                "base:$base input:$input result:$result",
                base,
                result
            )
        }
    }
}