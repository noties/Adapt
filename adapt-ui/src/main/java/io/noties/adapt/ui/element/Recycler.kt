package io.noties.adapt.ui.element

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

@Suppress("FunctionName")
fun <LP: LayoutParams> ViewFactory<LP>.Recycler(
    hasFixedSize: Boolean = true
): ViewElement<RecyclerView, LP> = Element(ElementViewFactory.Recycler) {
    it.setHasFixedSize(hasFixedSize)
}

fun <V: RecyclerView, LP: LayoutParams> ViewElement<V, LP>.recyclerLinearLayoutManager(
    isVertical: Boolean = true,
    reverseLayout: Boolean = false
) = onView {
    it.layoutManager = LinearLayoutManager(
        it.context,
        if (isVertical) LinearLayoutManager.VERTICAL else LinearLayoutManager.HORIZONTAL,
        reverseLayout
    )
}

fun <V: RecyclerView, LP: LayoutParams> ViewElement<V, LP>.recyclerGridLayoutManager(
    spanCount: Int,
    spanSizeLookup: ((RecyclerView, spanCount: Int, position: Int) -> Int)? = null
) = onView {
    val manager = GridLayoutManager(it.context, spanCount)
    spanSizeLookup?.also { ssl ->
        manager.spanSizeLookup = object: SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return ssl(it, spanCount, position)
            }
        }
    }
    it.layoutManager = manager
}

fun <V: RecyclerView, LP: LayoutParams> ViewElement<V, LP>.recyclerLayoutManager(
    manager: LayoutManager
) = onView {
    it.layoutManager = manager
}

fun <V: RecyclerView, LP: LayoutParams> ViewElement<V, LP>.recyclerDefaultItemAnimator(
) = recyclerItemAnimator(DefaultItemAnimator())

fun <V: RecyclerView, LP: LayoutParams> ViewElement<V, LP>.recyclerItemAnimator(
    animator: ItemAnimator
) = onView {
    it.itemAnimator = animator
}

fun <V: RecyclerView, LP: LayoutParams> ViewElement<V, LP>.recyclerOnScrollChanged(
    callback: (RecyclerView, deltaX: Int, deltaY: Int) -> Unit
) = onView {
    it.addOnScrollListener(object: OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            callback(recyclerView, dx, dy)
        }
    })
}