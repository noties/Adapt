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
class LazyView internal constructor(
    context: Context,
    internal var children: (ViewFactory<LayoutParams>.() -> Unit)?
) : View(context) {

    init {
        visibility = GONE
        setWillNotDraw(true)
    }

    val isInjected: Boolean get() = children == null

    // Namespace is polluted
    //  fun:show val:isShown (isShown is taken to check visibility)
    //  fun:display val:isDisplayed (display is a function that returns Display)
    //  fun:layOut (interferes with `layout`)
    fun inject() {
        visibility = VISIBLE
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)

        val children = children ?: return
        val parent = parent as? ViewGroup ?: return
        val index = parent.indexOfChild(this)
            .takeIf { it > -1 } ?: return

        if (visibility == VISIBLE || visibility == INVISIBLE) {

            // if we are here, children are going to be added, null-out factory
            this.children = null

            val factory = ViewFactory<LayoutParams>(parent)
            children(factory)

            // remove self
            parent.removeViewAt(index)

            factory.elements
                .withIndex()
                .forEach {
                    val view = it.value.init(context)
                    view.visibility = visibility // in case of INVISIBLE
                    parent.addView(view, index + it.index)
                    it.value.render()
                }
        }
    }

    override fun onDraw(canvas: Canvas?) = Unit
    override fun dispatchDraw(canvas: Canvas?) = Unit

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(0, 0)
    }
}