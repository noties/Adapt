package io.noties.adapt.ui.element

import android.widget.LinearLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.util.Gravity

val VStackDefaultGravity: Gravity get() = Gravity.center.top

val HStackDefaultGravity: Gravity get() = Gravity.center.leading

/**
 * @see LinearLayout.setGravity
 */
fun <V : LinearLayout, LP : LayoutParams> ViewElement<V, LP>.stackGravity(
    gravity: Gravity
) = onView {
    it.gravity = gravity.value
}

/**
 * @see LinearLayout.setBaselineAligned
 */
fun <V : LinearLayout, LP : LayoutParams> ViewElement<V, LP>.stackBaselineAligned(
    baselineAligned: Boolean = true
) = onView {
    it.isBaselineAligned = baselineAligned
}

/**
 * @see LinearLayout.setWeightSum
 */
fun <V : LinearLayout, LP : LayoutParams> ViewElement<V, LP>.stackWeightSum(
    weightSum: Float
) = onView {
    it.weightSum = weightSum
}