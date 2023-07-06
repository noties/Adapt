package io.noties.adapt.ui.element

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.obtainView
import io.noties.adapt.ui.renderView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.RETURNS_MOCKS
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Image_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun init() {
        val inputs = ImageView.ScaleType.values().toList()
        for (input in inputs) {
            val view = obtainView { Image().imageScaleType(input) }
            assertEquals(ImageView::class.java, view::class.java)
            assertDefaultInit(view, input)
        }
    }

    @Test
    fun `init - resource`() {
        val inputs = listOf(
            1,
            2
        )
        for (id in inputs) {
            val view = obtainView { Image(id) }
            assertEquals(ImageView::class.java, view::class.java)
        }
    }

    @Test
    fun `init - drawable`() {
        val inputs = listOf(
            mock(Drawable::class.java)
        )
        for (drawable in inputs) {
            val view = obtainView { Image(drawable) }
            assertEquals(ImageView::class.java, view::class.java)
        }
    }

    @Test
    fun `init - bitmap`() {
        val inputs = listOf(
            mock(Bitmap::class.java)
        )
        for (bitmap in inputs) {
            val view = obtainView { Image(bitmap) }
            assertEquals(ImageView::class.java, view::class.java)
        }
    }

    @Test
    fun factory() {
        val mocked = mock(ImageView::class.java, RETURNS_MOCKS)
        ElementViewFactory.Image = { mocked }
        assertEquals(mocked, obtainView { Image() })
    }

    @Test
    fun `image - resId`() {
        val input = 76
        newElementOfType<ImageView>()
            .image(input)
            .renderView {
                verify(this).setImageResource(eq(input))
            }
    }

    @Test
    fun `image - drawable`() {
        val input = mock(Drawable::class.java)
        newElementOfType<ImageView>()
            .image(input)
            .renderView {
                verify(this).setImageDrawable(input)
            }
    }

    @Test
    fun `image - bitmap`() {
        val input = mock(Bitmap::class.java)
        newElementOfType<ImageView>()
            .image(input)
            .renderView {
                verify(this).setImageBitmap(eq(input))
            }
    }

    @Test
    fun imageScaleType() {
        val input = ImageView.ScaleType.FIT_END
        newElementOfType<ImageView>()
            .imageScaleType(input)
            .renderView {
                verify(this).scaleType = eq(input)
            }
    }

    @Test
    fun `imageTint - color`() {
        val inputs = listOf(
            567 to null,
            8762 to PorterDuff.Mode.ADD
        )
        for ((color, mode) in inputs) {
            newElementOfType<ImageView>()
                .imageTint(color, mode)
                .renderView {
                    verify(this).imageTintList = eq(ColorStateList.valueOf(color))
                    if (mode == null) {
                        verify(this, never()).imageTintMode = any(PorterDuff.Mode::class.java)
                    } else {
                        verify(this).imageTintMode = eq(mode)
                    }
                }
        }
    }

    @Test
    fun `imageTint - colorStateList`() {
        val inputs = listOf(
            ColorStateList.valueOf(1) to null,
            ColorStateList.valueOf(-456789) to PorterDuff.Mode.MULTIPLY
        )
        for ((color, mode) in inputs) {
            newElementOfType<ImageView>()
                .imageTint(color, mode)
                .renderView {
                    verify(this).imageTintList = eq(color)
                    if (mode == null) {
                        verify(this, never()).imageTintMode = any(PorterDuff.Mode::class.java)
                    } else {
                        verify(this).imageTintMode = eq(mode)
                    }
                }
        }
    }

    private fun assertDefaultInit(view: View, scaleType: ImageView.ScaleType?) {
        assertEquals(
            scaleType ?: ImageView.ScaleType.CENTER_INSIDE,
            (view as ImageView).scaleType
        )
    }
}