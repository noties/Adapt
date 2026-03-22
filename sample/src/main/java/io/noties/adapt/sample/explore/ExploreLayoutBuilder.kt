package io.noties.adapt.sample.explore

import android.content.Context
import android.view.View
import androidx.annotation.DimenRes
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.ViewFactoryConstants
import io.noties.adapt.ui.app.ContextHolder
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import kotlin.math.roundToInt

typealias LayoutDimensionBuilder = LayoutDimension.() -> Int

object LayoutDimension: ContextHolder {
    val fill get() = ViewFactoryConstants.fill
    val wrap get() = ViewFactoryConstants.wrap

    fun res(@DimenRes resId: Int): Int = context.resources.let {
        val density = it.displayMetrics.density
        val value = it.getDimensionPixelSize(resId)
        (value / density).roundToInt()
    }
}

object ExploreLayoutBuilder {

    interface LayoutBuilder {
        infix fun width(width: Int): LayoutBuilder
        infix fun height(height: Int): LayoutBuilder
    }

    fun <V: View, LP: LayoutParams> ViewElement<V, LP>.layout3(
        width: LayoutDimensionBuilder,
        height: LayoutDimensionBuilder
    ) = layout(
        width.invoke(LayoutDimension),
        height.invoke(LayoutDimension)
    )



    fun <V: View, LP: LayoutParams> ViewElement<V, LP>.layout(
        builder: ViewFactoryConstants.() -> Pair<Int, Int>
    ) = this

    fun <V: View, LP: LayoutParams> ViewElement<V, LP>.layout2(
        builder: LayoutBuilder.() -> Unit
    ) = this

    fun hey(context: Context) {
        ViewFactory.createView(context) {
            ZStack {

            }.indent()
                .layout { 1 to 4 }
                .layout { fill to wrap }
                .layout(fill, wrap)
                .layout { 24 to 24 }

                // does not work without `this`
                .layout2 { this width 2 height 4 }
//                .layout2 { width 2 }
                .layout3(width = { fill }, height = { wrap })
        }
    }
}