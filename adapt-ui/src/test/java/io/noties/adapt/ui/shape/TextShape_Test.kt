package io.noties.adapt.ui.shape

import android.graphics.Typeface
import android.text.TextUtils.TruncateAt
import io.noties.adapt.ui.element.BreakStrategy
import io.noties.adapt.ui.element.HyphenationFrequency
import io.noties.adapt.ui.element.JustificationMode
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.util.Gravity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.reflect.KProperty1

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class TextShape_Test {
    @Test
    fun `data - return self`() {
        class Input<T>(
            val input: T,
            val property: KProperty1<TextShape, T>,
            val setter: TextShape.() -> Any
        )

        val inputs = listOf<Input<*>>(
            // common
            1.let { Input(it, TextShape::textSize) { textSize(it) } },
            2.let { Input(it, TextShape::textColor) { textColor((it)) } },
            Typeface.DEFAULT.let { Input(it, TextShape::textTypeface) { textTypeface(it) } },
            true.let { Input(it, TextShape::textBold) { textBold(it) } },
            true.let { Input(it, TextShape::textItalic) { textItalic(it) } },
            true.let { Input(it, TextShape::textUnderline) { textUnderline(it) } },
            true.let { Input(it, TextShape::textStrikethrough) { textStrikethrough(it) } },
            Shape.Shadow(
                Dimension.Exact(3),
                4,
                Dimension.Exact(5),
                Dimension.Exact(6)
            ).let {
                Input(it, TextShape::textShadow) {
                    textShadow(
                        (it.radius as Dimension.Exact).value,
                        it.color,
                        (it.offsetX as Dimension.Exact).value,
                        (it.offsetY as Dimension.Exact).value
                    )
                }
            },
            0.2F.let { Input(it, TextShape::textLetterSpacing) { textLetterSpacing(it) } },

            // base
            ("Hello!" as CharSequence).let { Input(it, TextShape::text) { text(it) } },
            LinearGradient.angle(1F)
                .setColors(1, 2)
                .let { Input(it, TextShape::textGradient) { textGradient(it) } },
            Gravity.center.horizontal.let { Input(it, TextShape::textGravity) { textGravity(it) } },
            Shape.Rotation(
                99F,
                Dimension.Relative(0.25F),
                Dimension.Relative(0.82F)
            ).let {
                Input(it, TextShape::textRotation) {
                    textRotation(
                        it.degrees!!,
                        (it.centerX as Dimension.Relative).percent,
                        (it.centerY as Dimension.Relative).percent
                    )
                }
            },
            2.let { Input(it, TextShape::textMaxLines) { textMaxLines(it) } },
            TruncateAt.MARQUEE.let {
                Input(it, TextShape::textEllipsize) {
                    textMaxLines(
                        null,
                        it
                    )
                }
            },
            BreakStrategy.balanced.let {
                Input(
                    it,
                    TextShape::textBreakStrategy
                ) { textBreakStrategy(it) }
            },
            HyphenationFrequency.full.let {
                Input(
                    it,
                    TextShape::textHyphenationFrequency
                ) { textHyphenationFrequency(it) }
            },
            JustificationMode.interWord.let {
                Input(
                    it,
                    TextShape::textJustificationMode
                ) { textJustificationMode(it) }
            },
            4.let { Input(it, TextShape::textLineSpacingAdd) { textLineSpacing(add = it) } },
            0.5F.let {
                Input(
                    it,
                    TextShape::textLineSpacingMultiplier
                ) { textLineSpacing(mult = it) }
            }
        )

        for (input in inputs) {
            val shape = TextShape()
            // initially null
            Assert.assertNull(input.property.get(shape))
            // returned self
            val set = input.setter.invoke(shape)
            Assert.assertEquals(shape, set)
            // value is persisted after setter
            Assert.assertEquals(
                shape.toStringDedicatedProperties(),
                input.input,
                input.property.get(shape)
            )
        }
    }
}