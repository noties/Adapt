package io.noties.adapt.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory

/**
 * [android.view.ViewStub]-like view that would put itself as a placeholder in a layout
 * and then replace self with children of the supplied factory. Multiple children
 * are supported. Setting visibility [View.VISIBLE] or [View.INVISIBLE] is the same
 * as calling [LazyView.inject] directly
 */
@SuppressLint("ViewConstructor")
open class LazyView internal constructor(
    context: Context,
    internal var children: (ViewFactory<LayoutParams>.() -> Unit)?
) : View(context) {

    init {
        // NB! important to send to super
        super.setVisibility(GONE)
        setWillNotDraw(true)
    }

    val isInjected: Boolean get() = children == null

    // Namespace is polluted
    //  fun:show val:isShown (isShown is taken to check visibility)
    //  fun:display val:isDisplayed (display() is a function that returns Display)
    //  fun:layOut (interferes with `layout`)
    fun inject() {
        // if injection did not happen, we should not change visibility to VISIBLE
        if (inject(VISIBLE)) {
            // NB! as we override visibility, send to super
            super.setVisibility(VISIBLE)
        }
    }

    override fun setVisibility(visibility: Int) {
        val value = if (inject(visibility)) visibility else GONE
        super.setVisibility(value)
    }

    private fun inject(forVisibility: Int): Boolean {

        // we could have checked for GONE, but as it is an Int, anything can be there
        //  check for values that we expect explicitly
        if (forVisibility != VISIBLE && forVisibility != INVISIBLE) return false

        val children = children ?: return false
        val parent = parent as? ViewGroup ?: return false
        val index = parent.indexOfChild(this)
            .takeIf { it > -1 } ?: return false

        // if we are here, children are going to be added, null-out factory
        this.children = null

        val factory = ViewFactory<LayoutParams>(parent)
        children(factory)

        // remove self
        parent.removeViewAt(index)

        factory.useElements()
            .withIndex()
            .forEach {
                val view = it.value.init(context)
                view.visibility = forVisibility // in case of INVISIBLE
                parent.addView(view, index + it.index)
                it.value.render()
            }

        return true
    }

    override fun onDraw(canvas: Canvas?) = Unit
    override fun dispatchDraw(canvas: Canvas?) = Unit

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(0, 0)
    }
}