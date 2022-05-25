package io.noties.adapt.ui

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.GravityInt

class ViewFactory<out LP : LayoutParams> {

    fun <V : View, TLP : LayoutParams> ViewElement<V, TLP>.layout(
        block: TLP.() -> Unit
    ): ViewElement<V, TLP> = this.also {
        it.layoutBlocks.add(block)
    }

    fun <V : View, TLP : LayoutParams> ViewElement<V, TLP>.layout(
        width: Int,
        height: Int,
        block: (TLP.() -> Unit)? = null
    ): ViewElement<V, TLP> = this.also {
        it.layoutBlocks.add {
            // special values
            if (width == FILL || width == WRAP) {
                this.width = width
            } else {
                // else exact dp values, convert to px
                this.width = width.dip
            }
            if (height == FILL || height == WRAP) {
                this.height = height
            } else {
                this.height = height.dip
            }
        }
        if (block != null) {
            it.layoutBlocks.add(block)
        }
    }

    fun <V : View, LLP : LinearLayout.LayoutParams> ViewElement<V, LLP>.layoutWeight(
        weight: Float
    ): ViewElement<V, LLP> {
        return this.also {
            it.layoutBlocks.add { this.weight = weight }
        }
    }

    @JvmName("linearLayoutGravity")
    fun <V : View, LLP : LinearLayout.LayoutParams> ViewElement<V, LLP>.layoutGravity(
        @GravityInt gravity: Int
    ): ViewElement<V, LLP> {
        return this.also {
            it.layoutBlocks.add { this.gravity = gravity }
        }
    }

    @JvmName("frameLayoutGravity")
    fun <V : View, FLP : FrameLayout.LayoutParams> ViewElement<V, FLP>.layoutGravity(
        @GravityInt gravity: Int
    ): ViewElement<V, FLP> {
        return this.also {
            it.layoutBlocks.add { this.gravity = gravity }
        }
    }

    fun <V : View, MLP : ViewGroup.MarginLayoutParams> ViewElement<V, MLP>.margin(
        all: Int
    ) = margin(all, all, all, all)

    fun <V : View, MLP : ViewGroup.MarginLayoutParams> ViewElement<V, MLP>.margin(
        horizontal: Int? = null,
        vertical: Int? = null
    ) = margin(horizontal, vertical, horizontal, vertical)

    fun <V : View, MLP : ViewGroup.MarginLayoutParams> ViewElement<V, MLP>.margin(
        leading: Int? = null,
        top: Int? = null,
        trailing: Int? = null,
        bottom: Int? = null
    ) = this.also {
        it.layoutBlocks.add {
            leading?.dip?.also { marginStart = it }
            top?.dip?.also { topMargin = it }
            trailing?.dip?.also { marginEnd = it }
            bottom?.dip?.also { bottomMargin = it }
        }
    }

    var elements: MutableList<ViewElement<out View, *>> = mutableListOf()

    // empty companion object to be used in extensions
    companion object
}

// `*` would match all
// `ViewGroup.LayoutParams` would match ONLY `ViewGroup.LayoutParams`, not type children
typealias AnyViewFactory = ViewFactory<*>