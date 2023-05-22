package io.noties.adapt.sample.explore

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import io.noties.adapt.sample.samples.adaptui.Colors
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactoryConstants
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutWeight
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.shape.Rectangle

object ExploreStyle {

    // TODO: maybe create a StyleContainer? But in case of multiple we would not create
    //  an extension that covers all

    class Style(
        val block: Unit
    )

    // Basic,
    @Suppress("FunctionName")
    fun Style(
        block: ViewFactoryConstants.(ViewElement<out View, out LayoutParams>) -> Unit
    ): _Style<View, LayoutParams> {
        return _Style(block)
    }

    fun <V : View> ViewStyle(
        block: ViewFactoryConstants.(ViewElement<out V, in Nothing>) -> Unit
    ): _Style<V, Nothing> {
        return _Style(block)
    }

    fun <LP : LayoutParams> LayoutStyle(
        block: ViewFactoryConstants.(ViewElement<out View, out LP>) -> Unit
    ): _Style<View, LP> {
        return _Style(block)
    }

    fun <V : View, LP : LayoutParams> ViewLayoutStyle(
        block: ViewFactoryConstants.(ViewElement<out V, out LP>) -> Unit
    ): _Style<V, LP> {
        return _Style(block)
    }

    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.style(style: _Style<V, LP>) = this.also {
        style.block(ViewFactoryConstants.Impl, it)
    }

    class _Style<in V : View, in LP : LayoutParams>(
        val block: ViewFactoryConstants.(ViewElement<@UnsafeVariance V, @UnsafeVariance LP>) -> Unit
    )

//    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.style(block: TextStyles.() -> _Style<V, LP>) =
//        this.also {
//            it.style(block(TextStyles))
//        }

    object ViewStyles {
        val withBackground = Style {
            it.background(Rectangle())
        }
    }

    object TextStyles {
        val primary = ViewStyle<TextView> {
            it.textSize(16)
                .textColor(Colors.black)
        }

        val px1 = LayoutStyle<LayoutParams> { it.layout(1, 1) }

        val spacedSecondary = ViewLayoutStyle<TextView, LinearLayout.LayoutParams> {
            it.textSize(22)
                .textColor(Colors.orange)
                .layout(0, WRAP)
                .layoutWeight(1F)
        }
    }

//    fun hey(context: Context) {
//        ViewFactory.createView(context) {
//            Text()
//                .style(TextStyles.primary)
//                .style(ViewStyles.withBackground)
//            Element { Button(it) }
//                .style(TextStyles.primary)
//            Text("blah")
////                .style { primary }
//                .style(ViewStyles.withBackground)
//
//            VStack {
//                Text()
////                    .style { spacedSecondary }
//                    .style(ViewStyles.withBackground)
//
//                ZStack {
//                    Text()
////                        .style { primary }
//                        .style(ViewStyles.withBackground)
//                }
//            }
//
//            Image()
////                .style(TextStyles.primary)
//                .style(ViewStyles.withBackground)
//
//            View()
////                .style(TextStyles.primary)
//                .style(ViewStyles.withBackground)
//        }
//    }
}