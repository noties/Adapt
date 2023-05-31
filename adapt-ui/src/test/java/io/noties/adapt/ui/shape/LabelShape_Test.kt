package io.noties.adapt.ui.shape

import android.graphics.Typeface
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
class LabelShape_Test {

    @Test
    fun `data - return self`() {
        class Input<T>(
            val input: T,
            val property: KProperty1<LabelShape, T>,
            val setter: LabelShape.() -> Any
        )

        val inputs = listOf<Input<*>>(
            // common
            1.let { Input(it, LabelShape::textSize) { textSize(it) } },
            2.let { Input(it, LabelShape::textColor) { textColor((it)) } },
            Typeface.DEFAULT.let { Input(it, LabelShape::textTypeface) { textTypeface(it) } },
            true.let { Input(it, LabelShape::textBold) { textBold(it) } },
            true.let { Input(it, LabelShape::textItalic) { textItalic(it) } },
            true.let { Input(it, LabelShape::textUnderline) { textUnderline(it) } },
            true.let { Input(it, LabelShape::textStrikethrough) { textStrikethrough(it) } },
            Shape.Shadow(
                Dimension.Exact(3),
                4,
                Dimension.Exact(5),
                Dimension.Exact(6)
            ).let {
                Input(it, LabelShape::textShadow) {
                    textShadow(
                        (it.radius as Dimension.Exact).value,
                        it.color,
                        (it.offsetX as Dimension.Exact).value,
                        (it.offsetY as Dimension.Exact).value
                    )
                }
            },
            0.2F.let { Input(it, LabelShape::textLetterSpacing) { textLetterSpacing(it) } },

            // base
            "Hello!".let { Input(it, LabelShape::text) { text(it) } },
            Gravity.bottom.trailing.let { Input(it, LabelShape::textGravity) { textGravity(it) } },
            Shape.Rotation(
                82F,
                Dimension.Relative(0.25F),
                Dimension.Relative(0.75F)
            ).let {
                Input(it, LabelShape::textRotation) {
                    textRotation(
                        it.degrees!!,
                        (it.centerX as Dimension.Relative).percent,
                        (it.centerY as Dimension.Relative).percent
                    )
                }
            }
        )

        for (input in inputs) {
            val shape = LabelShape()
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