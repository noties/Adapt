package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Rect
import io.noties.adapt.ui.shape.Dimension.Exact
import io.noties.adapt.ui.shape.Dimension.Relative
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
class ShapeTranslation_Test {

    @Test
    fun empty() {
        val inputs = listOf(
            Shape.Translation(),
            Shape.Translation(Exact(0), Exact(0)),
            Shape.Translation(null, Exact(0)),
            Shape.Translation(Exact(0), null),
            Shape.Translation(Exact(0), Relative(0F)),
            Shape.Translation(Relative(0F), Relative(0F)),
            Shape.Translation(Relative(0F), Exact(0)),
        )

        for (input in inputs) {
            val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
            input.draw(canvas, io.noties.adapt.ui.testutil.mockt())
            verifyNoInteractions(canvas)
        }
    }

    @Test
    fun resolve() {
        // x and y should use dedicated width/height properties as a relative reference

        val inputs = listOf(
            (null to 1) to (0F to 1F),
            (2 to null) to (2F to 0F),
            (3 to 4) to (3F to 4F)
        )

        for ((exact, expected) in inputs) {
            val translation = Shape.Translation(
                exact.first?.let(::Exact),
                exact.second?.let(::Exact)
            )
            val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
            translation.draw(canvas, io.noties.adapt.ui.testutil.mockt())
            verify(canvas).translate(
                eq(expected.first),
                eq(expected.second)
            )
        }
    }

    @Test
    fun `resolve - relative`() {
        // x and y should use dedicated width/height properties as a relative reference

        val rect = Rect(0, 0, 10, 20)

        val inputs = listOf(
            (null to 1F) to (0F to 20F),
            (0.5F to null) to (5F to 0F),
            (0.1F to 0.25F) to (1F to 5F)
        )

        for ((exact, expected) in inputs) {
            val translation = Shape.Translation(
                exact.first?.let(::Relative),
                exact.second?.let(::Relative)
            )
            val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
            translation.draw(canvas, rect)
            verify(canvas).translate(
                eq(expected.first),
                eq(expected.second)
            )
        }
    }

    @Test
    fun copy() {
        val inputs = listOf(
            Shape.Translation(),
            Shape.Translation(Exact(1)),
            Shape.Translation(y = Relative(0.5F)),
            Shape.Translation(Relative(0.1F), Exact(50))
        )

        for (input in inputs) {
            val result = input.copy()
            Assert.assertNotEquals(
                "input:$input result:$result",
                System.identityHashCode(input),
                System.identityHashCode(result)
            )
            Assert.assertEquals(
                "input:$input result:$result",
                input,
                result
            )
        }

        val base = Shape.Translation(Exact(1001), Relative(999F))
        for (input in inputs) {
            val result = input.copy {
                x = base.x
                y = base.y
            }
            Assert.assertNotEquals(
                "input:$input result:$result",
                System.identityHashCode(input),
                System.identityHashCode(result)
            )
            Assert.assertEquals(
                "input:$input base:$base result:$result",
                base,
                result
            )
        }
    }
}