package io.noties.adapt.sample.samples.adaptui

import android.view.View
import android.view.ViewGroup.LayoutParams
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.AlignSelf
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.JustifyContent
import io.noties.adapt.sample.util.dip
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.addChildren

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.Flex(
    children: ViewFactory<FlexboxLayout.LayoutParams>.() -> Unit
): ViewElement<FlexboxLayout, LP> = ViewElement<FlexboxLayout, LP> {
    FlexboxLayout(it).also { fl ->
        ViewFactory.addChildren(fl, children)
    }
}.also(elements::add)


/**
 * Customization of the FlexLayout
 */
fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.flexDirection(
    @FlexDirection flexDirection: Int
): ViewElement<V, LP> = onView {
    this.flexDirection = flexDirection
}

fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.flexWrap(
    @FlexWrap flexWrap: Int
): ViewElement<V, LP> = onView {
    this.flexWrap = flexWrap
}

fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.flexJustifyContent(
    @JustifyContent justifyContent: Int
): ViewElement<V, LP> = onView {
    this.justifyContent = justifyContent
}

fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.flexAlignItems(
    @AlignItems alignItems: Int
): ViewElement<V, LP> = onView {
    this.alignItems = alignItems
}

fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.flexAlignContent(
    @AlignContent alignContent: Int
): ViewElement<V, LP> = onView {
    this.alignContent = alignContent
}


/**
 * Customization of children
 */
/**
 * Order
 */
fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexOrder(
    flexOrder: Int
): ViewElement<V, LP> = onLayout {
    this.order = flexOrder
}

/**
 * FlexGrow
 */
fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexGrow(
    flexGrow: Float
): ViewElement<V, LP> = onLayout {
    this.flexGrow = flexGrow
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexShrink(
    flexShrink: Float
): ViewElement<V, LP> = onLayout {
    this.flexShrink = flexShrink
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexAlignSelf(
    @AlignSelf alignSelf: Int
): ViewElement<V, LP> = onLayout {
    this.alignSelf = alignSelf
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexMinWidth(
    minWidth: Int
): ViewElement<V, LP> = onLayout {
    this.minWidth = minWidth.dip
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexMinHeight(
    minHeight: Int
): ViewElement<V, LP> = onLayout {
    this.minHeight = minHeight.dip
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexMaxWidth(
    maxWidth: Int
): ViewElement<V, LP> = onLayout {
    this.maxWidth = maxWidth.dip
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexMaxHeight(
    maxHeight: Int
): ViewElement<V, LP> = onLayout {
    this.maxHeight = maxHeight.dip
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexWrapBefore(
    wrapBefore: Boolean
): ViewElement<V, LP> = onLayout {
    this.isWrapBefore = wrapBefore
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexBasisPercent(
    flexBasisPercent: Float
): ViewElement<V, LP> = onLayout {
    this.flexBasisPercent = flexBasisPercent
}