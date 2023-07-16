package io.noties.adapt.ui.shape

import android.graphics.Typeface
import android.text.TextPaint
import io.noties.adapt.ui.testutil.assertDensity
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class CommonTextPaintData_Test {

    private class Impl(
        override var textSize: Int? = null,
        override var textColor: Int? = null,
        override var textTypeface: Typeface? = null,
        override var textBold: Boolean? = null,
        override var textItalic: Boolean? = null,
        override var textUnderline: Boolean? = null,
        override var textStrikethrough: Boolean? = null,
        override var textShadow: Shape.Shadow? = null,
        override var textLetterSpacing: Float? = null
    ) : CommonTextPaintData, CommonTextPaintDataSetter<Impl>

    @Test
    fun textSize() {
        assertDensity(1F)

        val textSize = 42

        val inputs = listOf(
            Impl(textSize = textSize),
            Impl().textSize(textSize)
        )

        for (impl in inputs) {
            val paint = mockt<TextPaint>()

            impl.applyTo(paint)

            verify(paint).textSize = eq(textSize.toFloat())
        }
    }

    @Test
    fun textColor() {
        val textColor = 912712
        val inputs = listOf(
            Impl(textColor = textColor),
            Impl().textColor(textColor)
        )
        for (input in inputs) {
            val paint = mockt<TextPaint>()
            input.applyTo(paint)
            verify(paint).color = eq(textColor)
        }
    }

    @Test
    fun textTypeface() {
        val typeface = Typeface.DEFAULT_BOLD
        val inputs = listOf(
            Impl(textTypeface = typeface),
            Impl().textTypeface(typeface)
        )
        for (input in inputs) {
            val paint = mockt<TextPaint>()
            input.applyTo(paint)
            verify(paint).typeface = eq(typeface)
        }
    }

    @Test
    fun textBold() {
        val inputs = listOf(
            Impl(textBold = true),
            Impl().textBold()
        )
        for (input in inputs) {
            val paint = mockt<TextPaint>()
            input.applyTo(paint)
            // no typeface - fake bold text
            verify(paint).isFakeBoldText = eq(true)
        }
    }

    @Test
    fun textItalic() {
        val inputs = listOf(
            Impl(textItalic = true),
            Impl().textItalic()
        )
        for (input in inputs) {
            val paint = mockt<TextPaint>()
            input.applyTo(paint)
            verify(paint).textSkewX = eq(-0.25F)
        }
    }

    @Test
    fun `textBold - typeface`() {
        val typeface = Typeface.MONOSPACE
        val description = typeface.shadow().fontDescription

        val inputs = listOf(
            Impl(textTypeface = typeface, textBold = true),
            Impl().textTypeface(typeface).textBold()
        )

        for (input in inputs) {
            val paint = mockt<TextPaint>()
            input.applyTo(paint)

            val font = kotlin.run {
                val captor = ArgumentCaptor.forClass(Typeface::class.java)
                verify(paint).typeface = captor.capture()
                captor.value
            }
            val desc = font.shadow().fontDescription
            Assert.assertEquals(description.familyName, desc.familyName)
            Assert.assertEquals(Typeface.BOLD, desc.style)
        }
    }

    @Test
    fun `textItalic - typeface`() {
        val typeface = Typeface.MONOSPACE
        val description = typeface.shadow().fontDescription

        val inputs = listOf(
            Impl(textTypeface = typeface, textItalic = true),
            Impl().textTypeface(typeface).textItalic()
        )

        for (input in inputs) {
            val paint = mockt<TextPaint>()
            input.applyTo(paint)

            val font = kotlin.run {
                val captor = ArgumentCaptor.forClass(Typeface::class.java)
                verify(paint).typeface = captor.capture()
                captor.value
            }
            val desc = font.shadow().fontDescription
            Assert.assertEquals(description.familyName, desc.familyName)
            Assert.assertEquals(Typeface.ITALIC, desc.style)
        }
    }

    @Test
    fun `textItalic textBold - typeface`() {
        val typeface = Typeface.MONOSPACE
        val description = typeface.shadow().fontDescription

        val inputs = listOf(
            Impl(textTypeface = typeface, textBold = true, textItalic = true),
            Impl().textTypeface(typeface).textBold().textItalic()
        )

        for (input in inputs) {
            val paint = mockt<TextPaint>()
            input.applyTo(paint)

            val font = kotlin.run {
                val captor = ArgumentCaptor.forClass(Typeface::class.java)
                verify(paint).typeface = captor.capture()
                captor.value
            }
            val desc = font.shadow().fontDescription
            Assert.assertEquals(description.familyName, desc.familyName)
            Assert.assertEquals(Typeface.BOLD_ITALIC, desc.style)
        }
    }

    @Test
    fun textUnderline() {
        val inputs = listOf(
            Impl(textUnderline = true),
            Impl().textUnderline()
        )
        for (input in inputs) {
            val paint = mockt<TextPaint>()
            input.applyTo(paint)
            verify(paint).isUnderlineText = eq(true)
        }
    }

    @Test
    fun textStrikethrough() {
        val inputs = listOf(
            Impl(textStrikethrough = true),
            Impl().textStrikethrough()
        )
        for (input in inputs) {
            val paint = mockt<TextPaint>()
            input.applyTo(paint)
            verify(paint).isStrikeThruText = eq(true)
        }
    }

    @Test
    fun textShadow() {
        assertDensity(1F)

        val color = 712
        val radius = 8
        val offsetX = 16
        val offsetY = 1

        val inputs = listOf(
            Impl(
                textShadow = Shape.Shadow(
                    Dimension.Exact(radius),
                    color,
                    Dimension.Exact(offsetX),
                    Dimension.Exact(offsetY)
                )
            ),
            Impl().textShadow(radius, color, offsetX, offsetY)
        )

        for (input in inputs) {
            val paint = mockt<TextPaint>()
            input.applyTo(paint)
            verify(paint).setShadowLayer(
                eq(radius.toFloat()),
                eq(offsetX.toFloat()),
                eq(offsetY.toFloat()),
                eq(color)
            )
        }
    }

    @Test
    fun textLetterSpacing() {
        val letterSpacing = 0.1F
        val inputs = listOf(
            Impl(textLetterSpacing = letterSpacing),
            Impl().textLetterSpacing(letterSpacing)
        )
        for (input in inputs) {
            val paint = mockt<TextPaint>()
            input.applyTo(paint)
            verify(paint).letterSpacing = eq(letterSpacing)
        }
    }

    private fun Typeface.shadow() = Shadows.shadowOf(this)
}