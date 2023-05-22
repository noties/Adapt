package io.noties.adapt.sample.explore

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import io.noties.adapt.sample.samples.adaptui.Colors
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.ViewFactoryConstants
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutWeight
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding

object ExploreStyle {

    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.style(
        style: ElementStyle<V, LP>
    ) = this.also {
        style.block.invoke(ViewFactoryConstants.Impl, it)
    }

    class ElementStyle<in V : View, in LP : LayoutParams> private constructor(
        val block: ViewFactoryConstants.(ViewElement<@UnsafeVariance V, @UnsafeVariance LP>) -> Unit
    ) {
        companion object {
            operator fun <V : View, LP : LayoutParams> invoke(
                block: ViewFactoryConstants.(ViewElement<V, LP>) -> Unit
            ) = ElementStyle(block)

            fun generic(
                block: ViewFactoryConstants.(ViewElement<out View, out LayoutParams>) -> Unit
            ) = ElementStyle(block)

            fun <V : View> view(
                block: ViewFactoryConstants.(ViewElement<V, out LayoutParams>) -> Unit
            ) = ElementStyle(block)

            fun <LP : LayoutParams> layout(
                block: ViewFactoryConstants.(ViewElement<out View, LP>) -> Unit
            ) = ElementStyle(block)
        }
    }


    object ViewStyles {
//        val withBackground = Style {
//            it.background(Rectangle())
//        }

        val withBackground = ElementStyle.generic {
            it.background(Color.RED)
                .padding(16)
        }

        val another = ElementStyle<View, LayoutParams> {
            it.layout(FILL, WRAP)
                .background(Colors.black)
                .padding(16)
        }

        val px1 = ElementStyle.generic { it.layout(1, 1) }
        val weight1 = ElementStyle.layout<LinearLayout.LayoutParams> {
            it.layoutWeight(1F)
        }
    }

    object TextStyles {
        val primary = ElementStyle.view<TextView> {
            it.textSize(16)
                .textColor(Colors.black)
        }

        val spacedSecondary = ElementStyle<TextView, LinearLayout.LayoutParams> {
            it.background(0)
                .padding(16)
                .layout(0, 0)
                .layoutWrap()
                .layoutWeight(1F)
        }
    }

    fun hey(context: Context) {
        ViewFactory.createView(context) {
            Text()
                .style(TextStyles.primary)
                .style(ViewStyles.withBackground)
//                .style(TextStyles.spacedSecondary)
            Element { Button(it) }
                .style(TextStyles.primary)
            Text("blah")
//                .style { primary }
                .style(ViewStyles.withBackground)

            VStack {
                Text()
//                    .style { spacedSecondary }
                    .style(ViewStyles.withBackground)
                    .style(TextStyles.spacedSecondary)

                ZStack {
                    Text()
//                        .style { primary }
                        .style(ViewStyles.withBackground)
                }
            }

            Image()
//                .style(TextStyles.primary)
                .style(ViewStyles.withBackground)

            View()
//                .style(TextStyles.primary)
                .style(ViewStyles.withBackground)
        }
    }
}