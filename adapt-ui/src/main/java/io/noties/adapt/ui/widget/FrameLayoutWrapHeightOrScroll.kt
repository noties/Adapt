package io.noties.adapt.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layoutParams

/**
 * @see io.noties.adapt.ui.element.ZStackWrapHeightOrScroll
 * @see LinearLayoutContentMeasuredLast
 */
@SuppressLint("ViewConstructor")
class FrameLayoutWrapHeightOrScroll(
    context: Context,
    private val scrollStyle: (ViewElement<ScrollView, LayoutParams>) -> Unit,
    child: ViewFactory<LayoutParams>.() -> ViewElement<out View, LayoutParams>
) : FrameLayout(context) {

    private val content: View

    private var callbacks: Runnable? = null

    init {
        content = ViewFactory<LayoutParams>(
            context,
            this
        ).let { child.invoke(it) }
            .also { it.init(context) }
            .also {
                if (it.view.layoutParams == null) {
                    it.view.layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                }
            }
            .also { it.render() }
            .also {
                addView(it.view)
            }
            .view
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val child = content

        // if child is wrapped, then measure parent scroll-view
        if (child.parent != this) {
            // measure scroll
            getChildAt(0).measure(
                MeasureSpec.makeMeasureSpec(
                    width - paddingRight - paddingRight,
                    MeasureSpec.EXACTLY
                ),
                MeasureSpec.makeMeasureSpec(
                    height - paddingTop - paddingBottom,
                    MeasureSpec.AT_MOST
                )
            )
        } else {
            // else measure the whole view (do not pass at-most, just let it be whatever
            //  height it wants)
            child.measure(
                MeasureSpec.makeMeasureSpec(
                    width - paddingRight - paddingLeft,
                    MeasureSpec.EXACTLY
                ),
                MeasureSpec.makeMeasureSpec(
                    0,
                    MeasureSpec.UNSPECIFIED
                )
            )
        }

        val childMeasuredHeight = child.measuredHeight

        val isOverflow = childMeasuredHeight > (height - paddingTop - paddingBottom)

        if (!isOverflow) {
            setMeasuredDimension(
                width,
                childMeasuredHeight + paddingTop + paddingBottom
            )

            unwrapContentFromScrollView()

        } else {
            // just take maximum available (at-most)
            setMeasuredDimension(
                width,
                height
            )

            wrapContentInScrollView(height)
        }
    }

    private fun wrapContentInScrollView(height: Int) {
        removeWrapUnwrapCallbacks()

        // cannot do anything until parent is present
        val parent = content.parent as? ViewGroup ?: return
        if (parent != this) {
            // assume already wrapped
            return
        }

        postWrapUnwrapCallbacks {
            // remove from this layout
            removeAllViews()

            val view = ViewFactory.createView(this) {
                VScroll {
                    VStack {
                        Element { content }
                    }
                }.indent()
                    .layoutParams(LayoutParams(LayoutParams.MATCH_PARENT, height))
                    .also(scrollStyle)
            }

            addView(view)
        }
    }

    private fun unwrapContentFromScrollView() {
        removeWrapUnwrapCallbacks()

        val parent = content.parent as? ViewGroup ?: return
        if (parent == this) {
            // assume already unwrapped
            return
        }

        postWrapUnwrapCallbacks {
            parent.removeAllViews()

            removeAllViews()

            addView(content)
        }
    }

    private fun removeWrapUnwrapCallbacks() {
        callbacks?.also { removeCallbacks(it) }
    }

    private fun postWrapUnwrapCallbacks(callbacks: Runnable) {
        this.callbacks = callbacks
        post(callbacks)
    }

    override fun canScrollVertically(direction: Int): Boolean {
        // if contains scroll-view -> pass to it, else false
        // why not always passing to the child?
        val scrollView = getChildAt(0) as? ScrollView ?: return false
        return scrollView.canScrollVertically(direction)
    }
}