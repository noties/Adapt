package io.noties.adapt.ui.state

import android.graphics.drawable.StateListDrawable
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.imageTint
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textHintColor
import io.noties.adapt.ui.foreground
import java.util.Arrays

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.backgroundWithState(
    shapes: ShapeStateListBuilder
) = this.background(ShapeStateListFactory.build(shapes).stateListDrawable)

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.backgroundColorWithState(
    colors: ColorStateListBuilder
) = this.background(ColorStateListFactory.build(colors).stateListDrawable)


fun <V : View, LP : LayoutParams> ViewElement<V, LP>.foregroundWithState(
    shapes: ShapeStateListBuilder
) = this.foreground(ShapeStateListFactory.build(shapes).stateListDrawable)

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.foregroundColorWithState(
    colors: ColorStateListBuilder
) = this.foreground(ColorStateListFactory.build(colors).stateListDrawable)


fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textColorWithState(
    colors: ColorStateListBuilder
) = this.textColor(ColorStateListFactory.build(colors).colorStateList)

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textHintColorWithState(
    colors: ColorStateListBuilder
) = this.textHintColor(ColorStateListFactory.build(colors).colorStateList)


fun <V : ImageView, LP : LayoutParams> ViewElement<V, LP>.imageTintWithState(
    colors: ColorStateListBuilder
) = this.imageTint(
    colorStateList = ColorStateListFactory.build(colors).colorStateList
)

/**
 * `pressed` is not reported without a stateful drawable for the `android.R.attr.state_pressed`, as
 * a workaround a [io.noties.adapt.ui.shape.StatefulShape], [StateListDrawable] can be used with
 * pressed state defined. Or a stateful drawable
 * with this state defined, for example, `backgroundDefaultSelectable()` or `foregroundDefaultSelectable()`.
 * Additionally, a regular shape can be turned into stateful drawable with `shape.newDrawable().stateful()`
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onViewStateChange(
    action: (view: V, viewState: ViewState) -> Unit
) = onView {
    it.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        var previous = it.drawableState
        override fun onPreDraw(): Boolean {
            if (!it.viewTreeObserver.isAlive) {
                it.viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }

            val current = it.drawableState
            if (!Arrays.equals(previous, current)) {
                previous = current
                action(it, ViewState(current))
            }
            return true
        }
    })
}