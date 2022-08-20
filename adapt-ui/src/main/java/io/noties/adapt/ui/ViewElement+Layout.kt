package io.noties.adapt.ui

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.GravityInt
import androidx.annotation.IntDef

@IntDef(
    value = [MATCH_PARENT, WRAP_CONTENT],
    open = true
)
annotation class LayoutDimension

fun <V : View, TLP : ViewGroup.LayoutParams> ViewElement<V, TLP>.layout(
    @LayoutDimension width: Int,
    @LayoutDimension height: Int
): ViewElement<V, TLP> = onLayout {
    // special values
    if (width == MATCH_PARENT || width == WRAP_CONTENT) {
        this.width = width
    } else {
        // else exact dp values, convert to px
        this.width = width.dip
    }
    if (height == MATCH_PARENT || height == WRAP_CONTENT) {
        this.height = height
    } else {
        this.height = height.dip
    }
}

fun <V : View, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.layoutFill() =
    this.layout(MATCH_PARENT, MATCH_PARENT)

fun <V : View, LLP : LinearLayout.LayoutParams> ViewElement<V, LLP>.layoutWeight(
    weight: Float
): ViewElement<V, LLP> = onLayout {
    this.weight = weight
}

@JvmName("linearLayoutGravity")
fun <V : View, LLP : LinearLayout.LayoutParams> ViewElement<V, LLP>.layoutGravity(
    @GravityInt gravity: Int
): ViewElement<V, LLP> = onLayout {
    this.gravity = gravity
}

@JvmName("frameLayoutGravity")
fun <V : View, FLP : FrameLayout.LayoutParams> ViewElement<V, FLP>.layoutGravity(
    @GravityInt gravity: Int
): ViewElement<V, FLP> = onLayout {
    this.gravity = gravity
}

fun <V : View, MLP : ViewGroup.MarginLayoutParams> ViewElement<V, MLP>.layoutMargin(
    all: Int
) = layoutMargin(all, all, all, all)

fun <V : View, MLP : ViewGroup.MarginLayoutParams> ViewElement<V, MLP>.layoutMargin(
    horizontal: Int? = null,
    vertical: Int? = null
) = layoutMargin(horizontal, vertical, horizontal, vertical)

fun <V : View, MLP : ViewGroup.MarginLayoutParams> ViewElement<V, MLP>.layoutMargin(
    leading: Int? = null,
    top: Int? = null,
    trailing: Int? = null,
    bottom: Int? = null
) = onLayout {
    leading?.dip?.also { marginStart = it }
    top?.dip?.also { topMargin = it }
    trailing?.dip?.also { marginEnd = it }
    bottom?.dip?.also { bottomMargin = it }
}