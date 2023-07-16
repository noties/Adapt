package io.noties.adapt.ui.flex

import android.view.View
import android.view.ViewGroup.LayoutParams
import com.google.android.flexbox.AlignContent as _AlignContent
import com.google.android.flexbox.AlignItems as _AlignItems
import com.google.android.flexbox.AlignSelf as _AlignSelf
import com.google.android.flexbox.FlexDirection as _FlexDirection
import com.google.android.flexbox.FlexWrap as _FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.JustifyContent as _JustifyContent
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.ElementGroup
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.util.dip

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.Flex(
    children: ViewFactory<FlexboxLayout.LayoutParams>.() -> Unit
): ViewElement<FlexboxLayout, LP> = ElementGroup(
    FlexElementViewFactory.Flex,
    children
)


/**
 * Customization of the FlexLayout
 */
@JvmInline
value class FlexDirection(@_FlexDirection val value: Int) {
    companion object {
        val row: FlexDirection get() = FlexDirection(_FlexDirection.ROW)
        val column: FlexDirection get() = FlexDirection(_FlexDirection.COLUMN)
    }

    val reverse: FlexDirection get() = if (value == _FlexDirection.COLUMN) {
        _FlexDirection.COLUMN_REVERSE
    } else {
        _FlexDirection.ROW_REVERSE
    }.let { FlexDirection(it) }
}

fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.flexDirection(
    flexDirection: FlexDirection
): ViewElement<V, LP> = onView {
    it.flexDirection = flexDirection.value
}

@JvmInline
value class FlexWrap(@_FlexWrap val value: Int) {
    companion object {
        val nowrap: FlexWrap get() = FlexWrap(_FlexWrap.NOWRAP)
        val wrap: FlexWrap get() = FlexWrap(_FlexWrap.WRAP)
        val wrapReverse: FlexWrap get() = FlexWrap(_FlexWrap.WRAP_REVERSE)
    }
}

fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.flexWrap(
    flexWrap: FlexWrap
): ViewElement<V, LP> = onView {
    it.flexWrap = flexWrap.value
}

@JvmInline
value class JustifyContent(@_JustifyContent val value: Int) {
    companion object {
        val flexStart: JustifyContent get() = JustifyContent(_JustifyContent.FLEX_START)
        val flexEnd: JustifyContent get() = JustifyContent(_JustifyContent.FLEX_END)
        val center: JustifyContent get() = JustifyContent(_JustifyContent.CENTER)
        val spaceBetween: JustifyContent get() = JustifyContent(_JustifyContent.SPACE_BETWEEN)
        val spaceAround: JustifyContent get() = JustifyContent(_JustifyContent.SPACE_AROUND)
        val spaceEvenly: JustifyContent get() = JustifyContent(_JustifyContent.SPACE_EVENLY)
    }
}

fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.flexJustifyContent(
    justifyContent: JustifyContent
): ViewElement<V, LP> = onView {
    it.justifyContent = justifyContent.value
}

@JvmInline
value class AlignItems(@_AlignItems val value: Int) {
    companion object {
        val flexStart: AlignItems get() = AlignItems(_AlignItems.FLEX_START)
        val flexEnd: AlignItems get() = AlignItems(_AlignItems.FLEX_END)
        val center: AlignItems get() = AlignItems(_AlignItems.CENTER)
        val baseline: AlignItems get() = AlignItems(_AlignItems.BASELINE)
        val stretch: AlignItems get() = AlignItems(_AlignItems.STRETCH)
    }
}

fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.flexAlignItems(
    alignItems: AlignItems
): ViewElement<V, LP> = onView {
    it.alignItems = alignItems.value
}

@JvmInline
value class AlignContent(@_AlignContent val value: Int) {
    companion object {
        val flexStart: AlignContent get() = AlignContent(_AlignContent.FLEX_START)
        val flexEnd: AlignContent get() = AlignContent(_AlignContent.FLEX_END)
        val center: AlignContent get() = AlignContent(_AlignContent.CENTER)
        val spaceBetween: AlignContent get() = AlignContent(_AlignContent.SPACE_BETWEEN)
        val spaceAround: AlignContent get() = AlignContent(_AlignContent.SPACE_AROUND)
        val stretch: AlignContent get() = AlignContent(_AlignContent.STRETCH)
    }
}

fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.flexAlignContent(
    alignContent: AlignContent
): ViewElement<V, LP> = onView {
    it.alignContent = alignContent.value
}

// FlexboxLayout does not have gap support, but we can use a special drawable as divider
fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.flexGap(
    gap: Int
) = onView {
    it.setDividerDrawable(RectangleShape {
        size(gap, gap)
    }.newDrawable())
    it.setShowDivider(FlexboxLayout.SHOW_DIVIDER_MIDDLE)
}

// FlexboxLayout does not have gap support, but we can use a special drawable as divider
fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.flexGap(
    horizontal: Int? = null,
    vertical: Int? = null,
) = onView { flexbox ->

    horizontal?.also {
        flexbox.dividerDrawableHorizontal = RectangleShape {
            size(it, 0)
        }.newDrawable()
    }
    vertical?.also {
        flexbox.dividerDrawableVertical = RectangleShape {
            size(0, it)
        }.newDrawable()
    }

    if (horizontal != null || vertical != null) {
        flexbox.setShowDivider(FlexboxLayout.SHOW_DIVIDER_MIDDLE)
    }
}

/**
 * Customization of children
 */
/**
 * Order
 */
fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexOrder(
    flexOrder: Int
): ViewElement<V, LP> = onLayoutParams {
    it.order = flexOrder
}

/**
 * FlexGrow
 */
fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexGrow(
    flexGrow: Float
): ViewElement<V, LP> = onLayoutParams {
    it.flexGrow = flexGrow
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexShrink(
    flexShrink: Float
): ViewElement<V, LP> = onLayoutParams {
    it.flexShrink = flexShrink
}

@JvmInline
value class AlignSelf(@_AlignSelf val value: Int) {
    companion object {
        val auto: AlignSelf get() = AlignSelf(_AlignSelf.AUTO)
        val flexStart: AlignSelf get() = AlignSelf(_AlignItems.FLEX_START)
        val flexEnd: AlignSelf get() = AlignSelf(_AlignItems.FLEX_END)
        val center: AlignSelf get() = AlignSelf(_AlignItems.CENTER)
        val baseline: AlignSelf get() = AlignSelf(_AlignItems.BASELINE)
        val stretch: AlignSelf get() = AlignSelf(_AlignItems.STRETCH)
    }
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexAlignSelf(
    alignSelf: AlignSelf
): ViewElement<V, LP> = onLayoutParams {
    it.alignSelf = alignSelf.value
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexMinSize(
    minWidth: Int? = null,
    minHeight: Int? = null
): ViewElement<V, LP> = onLayoutParams { lp ->
    minWidth?.dip?.also { lp.minWidth = it }
    minHeight?.dip?.also { lp.minHeight = it }
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexMaxSize(
    maxWidth: Int? = null,
    maxHeight: Int? = null
): ViewElement<V, LP> = onLayoutParams { lp ->
    maxWidth?.dip?.also { lp.maxWidth = it }
    maxHeight?.dip?.also { lp.maxHeight = it }
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexWrapBefore(
    wrapBefore: Boolean = true
): ViewElement<V, LP> = onLayoutParams {
    it.isWrapBefore = wrapBefore
}

fun <V : View, LP : FlexboxLayout.LayoutParams> ViewElement<V, LP>.layoutFlexBasisPercent(
    flexBasisPercent: Float
): ViewElement<V, LP> = onLayoutParams {
    it.flexBasisPercent = flexBasisPercent
}