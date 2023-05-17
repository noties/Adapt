package io.noties.adapt.ui.shape

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.gradient.RadialGradient
import io.noties.adapt.ui.gradient.SweepGradient
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ShapeShaderCache_Test {

    @Test
    fun empty() {
        val cache = Shape.ShaderCache()
        val paint = mock(Paint::class.java)
        cache.shader(
            null,
            Rect(),
            paint
        )

        verify(paint).shader = eq(null)
    }

    @Test
    fun alreadySet() {
        val cache = Shape.ShaderCache()
        val rect = Rect(1, 2, 3, 4)
        val paint1 = mock(Paint::class.java)
        val gradient = LinearGradient.edges { bottom to trailing }
            .setColors(777, 123)

        cache.shader(
            gradient,
            rect,
            paint1
        )

        val captor1 = ArgumentCaptor.forClass(Shader::class.java)
        verify(paint1).shader = captor1.capture()
        val shader = captor1.value
        assertNotNull(shader)

        // now call with new paint instance and track `setShader` to not be called
        val paint2 = mock(Paint::class.java)
        `when`(paint2.shader).thenReturn(shader)
        cache.shader(
            gradient,
            rect,
            paint2
        )
        verify(paint2, never()).shader = any(Shader::class.java)
    }

    @Test
    fun differentBounds() {
        // different bounds creates new shader

        val cache = Shape.ShaderCache()
        val rect1 = Rect(1, 2, 3, 4)
        val rect2 = Rect(5, 6, 7, 8)

        assertNotEquals(rect1, rect2)

        val gradient = RadialGradient.center().setColors(1, 567)
        val paint1 = mock(Paint::class.java)

        cache.shader(
            gradient,
            rect1,
            paint1
        )

        val captor1 = ArgumentCaptor.forClass(Shader::class.java)
        verify(paint1).shader = captor1.capture()
        val shader1 = captor1.value
        assertNotNull(shader1)

        val paint2 = mock(Paint::class.java)
        `when`(paint2.shader).thenReturn(shader1)

        cache.shader(
            gradient,
            rect2,
            paint2
        )
        val captor2 = ArgumentCaptor.forClass(Shader::class.java)
        verify(paint2).shader = captor2.capture()
        val shader2 = captor2.value
        assertNotNull(shader2)

        assertNotEquals(shader1, shader2)
    }

    @Test
    fun differentGradient() {
        val cache = Shape.ShaderCache()
        val rect = Rect(10, 20, 30, 40)

        val gradient1 = RadialGradient.center().setColors(1, 567)
        val gradient2 = SweepGradient(42, 24)

        val paint1 = mock(Paint::class.java)

        cache.shader(
            gradient1,
            rect,
            paint1
        )

        val captor1 = ArgumentCaptor.forClass(Shader::class.java)
        verify(paint1).shader = captor1.capture()
        val shader1 = captor1.value
        assertNotNull(shader1)

        val paint2 = mock(Paint::class.java)
        `when`(paint2.shader).thenReturn(shader1)

        cache.shader(
            gradient2,
            rect,
            paint2
        )
        val captor2 = ArgumentCaptor.forClass(Shader::class.java)
        verify(paint2).shader = captor2.capture()
        val shader2 = captor2.value
        assertNotNull(shader2)

        assertNotEquals(shader1, shader2)
    }
}