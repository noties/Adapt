package io.noties.adapt.ui.element

import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.widget.LinearLayoutContentMeasuredLast

/**
 * @see LinearLayoutContentMeasuredLast
 * @see stackContentMeasureLast
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.VStackContentMeasuredLast(
    children: ViewFactory<LinearLayoutContentMeasuredLast.LayoutParams>.() -> Unit
) = ElementGroup(
    provider = { LinearLayoutContentMeasuredLast(it) },
    children = children
)


@Suppress("FINAL_UPPER_BOUND")
fun <V : View, LP : LinearLayoutContentMeasuredLast.LayoutParams> ViewElement<V, LP>.stackContentMeasureLast() =
    onLayoutParams {
        it.isContent = true
    }