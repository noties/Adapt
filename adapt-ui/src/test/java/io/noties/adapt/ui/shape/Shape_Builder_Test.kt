package io.noties.adapt.ui.shape

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Shape_Builder_Test {

    @Test
    fun builder() {
        val shape = Shape.builder {
            Oval()
        }
        Assert.assertEquals(OvalShape::class, shape::class)
    }

    @Test
    fun drawable() {
        val drawable = Shape.drawable {
            Circle()
        }
        Assert.assertEquals(CircleShape::class, drawable.shape::class)
    }
}