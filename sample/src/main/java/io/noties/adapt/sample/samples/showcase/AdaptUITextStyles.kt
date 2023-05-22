package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.ViewFactoryConstants
import io.noties.adapt.ui.element.ElementStyle
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.style
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.AdaptUIPreviewLayout
import kotlin.math.roundToInt

// NB! different colors just to show customizations
object TextStyles {
    val largeTitle = create {
        it.textSize(34)
            .textColor(Color.BLACK)
    }

    val title1 = create {
        it.textSize(28)
            .textColor(Color.BLUE)
    }

    val title2 = create {
        it.textSize(22)
            .textColor(Color.RED)
    }

    val title3 = create {
        it.textSize(20)
            .textColor(Color.MAGENTA)
    }

    val headline = create {
        it.textSize(17)
            .textColor(Color.CYAN)
            .textBold()
    }

    val body = create {
        it.textSize(17)
            .textColor(Color.GREEN)
    }

    val callout = create {
        it.textSize(16)
            .textColor(Color.GRAY)
    }

    //@formatter:off
    private fun create(
        block: ViewFactoryConstants.(ViewElement<TextView, out LayoutParams>) -> Unit
    ) = ElementStyle.view(block)
    //@formatter:on
}

private class PreviewTextStyles(context: Context, attrs: AttributeSet?) :
    AdaptUIPreviewLayout(context, attrs) {
    override fun ViewFactory<LayoutParams>.body() {
        val styles = listOf(
            TextStyles::largeTitle,
            TextStyles::title1,
            TextStyles::title2,
            TextStyles::title3,
            TextStyles::headline,
            TextStyles::body,
            TextStyles::callout,
        ).map {
            it.name to it.get()
        }

        VStack {
            for ((name, style) in styles) {
                VStack {

                    val element = Text(name).style(style)

                    Text().also { text ->
                        element.onView { textView ->
                            val styleName = textView.typeface
                                ?.let { tp -> if (tp.isBold) "Bold" else null }
                                ?: "Regular"
                            val textSize = textView.textSize.roundToInt()
                                .let {
                                    val sd = textView.resources.displayMetrics.scaledDensity
                                    (it / sd).roundToInt()
                                }
                            text.text("$styleName - $textSize")
                        }
                    }

                }.indent()
                    .padding(16)
            }
        }
    }

}