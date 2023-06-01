package io.noties.adapt.ui.sticky

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.R
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.onViewAttachedOnce
import io.noties.adapt.ui.util.addOnScrollChangedListener
import io.noties.adapt.ui.util.onAttachedOnce
import io.noties.adapt.ui.util.onDetachedOnce

// adapt-ui helper to mark a view as the sticky scroll container
fun <V : ViewGroup, LP : LayoutParams> ViewElement<V, LP>.stickyVerticalScrollContainer(
    body: (StickyVerticalScroll) -> Unit = {}
) = onView {
    StickyVerticalScroll(it).also(body)
}

// adapt-ui helper to mark a view as a sticky view
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.stickyView() = this.also { el ->
    val view = if (el.isInitialized) el.view else null
    if (view != null && view.isAttachedToWindow) {
        StickyVerticalScroll.findStickyScrollFromChild(view)?.addStickyView(view)
    } else {
        el.onViewAttachedOnce {
            StickyVerticalScroll.findStickyScrollFromChild(it)?.addStickyView(it)
        }
    }
}

/**
 * NB! This class is intended to be used with [LinearLayoutReverseDrawingOrder] inside a
 * scrolling parent view. This class modifies original view\'s [View.setTranslationY] attribute,
 * so for a sticky view to be on top, parent view-group must draw children in reverse order (starting
 * from the bottom), making a sticky view be drawn atop on views underneath.
 *
 * Only vertical scrolling is supported
 */
class StickyVerticalScroll private constructor(
    val scrollContainer: ViewGroup
) {
    companion object {
        // special constructor-like function to check for cached version first
        operator fun invoke(scrollContainer: ViewGroup): StickyVerticalScroll {
            val current = scrollContainer.getTag(tagStickyScrollViewId) as? StickyVerticalScroll
            if (current != null) {
                return current
            }
            val stickyVerticalScroll = StickyVerticalScroll(scrollContainer)
            scrollContainer.setTag(tagStickyScrollViewId, stickyVerticalScroll)
            return stickyVerticalScroll
        }

        // NB! assumes that view is attached to window
        fun findStickyScrollFromChild(child: View): StickyVerticalScroll? {
            var parent: ViewGroup? = child.parent as? ViewGroup
            while (parent != null) {
                val tag = parent.getTag(tagStickyScrollViewId) as? StickyVerticalScroll
                if (tag != null) {
                    return tag
                }
                parent = parent.parent as? ViewGroup
            }
            return null
        }

        private val tagStickyScrollViewId = R.id.adaptui_internal_sticky_scroll_container
        private val tagIsStickyId = R.id.adaptui_internal_sticky_scroll_view
    }

    var stickyViewDecoration: (View, isSticky: Boolean) -> Unit = { _, _ -> }

    init {
        val onLayoutChangeListener = View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            onScrolled()
        }
        scrollContainer.addOnLayoutChangeListener(onLayoutChangeListener)
        scrollContainer.addOnScrollChangedListener { _, _, _ ->
            onScrolled()
        }
        scrollContainer.onDetachedOnce {
            scrollContainer.removeOnLayoutChangeListener(onLayoutChangeListener)
            scrollContainer.setTag(tagStickyScrollViewId, null)
        }
    }

    fun onScrolled(y: Int = scrollContainer.scrollY) {

        initViewPositions()

        val previousSticky = views
            .withIndex()
            .reversed()
            .firstOrNull { it.value.positionY < y }

        if (previousSticky == null) {
            resetSticky()
            return
        }

        val nextSticky = previousSticky.index
            .takeIf { it < (views.size - 1) }
            ?.let { views[it + 1] }

        // amount of y translation for sticky view
        val stickyY = y - previousSticky.value.positionY

        val bottomY = (previousSticky.value.positionY + previousSticky.value.view.height) + stickyY

        val nextStickyTop = nextSticky?.positionY ?: 0

        @Suppress("ConvertTwoComparisonsToRangeCheck")
        val stickyOffsetY = if (nextStickyTop > 0 && nextStickyTop < bottomY) {
            -(bottomY - nextStickyTop)
        } else {
            0
        }

        applySticky(previousSticky.value.view, (stickyY + stickyOffsetY).toFloat())
    }

    fun addStickyView(view: View) {
        addStickyViewInternal(view)
    }

    fun removeStickyView(view: View) {
        val iterator = views.iterator()
        var changed = false

        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.view == view) {
                iterator.remove()
                changed = true
                break
            }
        }

        if (changed) {
            recalculateViewPositions()
        }
    }

    fun containsStickyView(view: View): Boolean {
        return views.firstOrNull { it.view == view } != null
    }

    private class ViewEntry(val view: View, var positionY: Int = 0) {
        operator fun component1() = view
        operator fun component2() = positionY
    }

    private val views = mutableListOf<ViewEntry>()

    private var onPreDrawListener: OnPreDrawListener? = null

    private fun resetSticky() {
        views.forEach { (view, _) ->
            view.translationY = 0F

            changeStickyState(view, false)
        }
    }

    private fun applySticky(view: View, y: Float) {
        views.forEach { (v, _) ->
            if (v == view) {
                v.translationY = y
                changeStickyState(v, true)
            } else {
                v.translationY = 0F
                changeStickyState(v, false)
            }
        }
    }

    private fun addStickyViewInternal(view: View, trace: Throwable? = null) {
        val contains = views.firstOrNull { view == it.view } != null
        if (contains) {
            return
        }

        if (!view.isAttachedToWindow) {
            val throwable = Throwable()
            view.onAttachedOnce {
                addStickyViewInternal(view, throwable)
            }
            return
        }

        if (!isChildOfScrollingView(view)) {
            throw IllegalStateException(
                "Supplied view is not a child of " +
                        "scrolling view, view:$view, scrollingView:$scrollContainer",
                trace
            )
        }

        views.add(ViewEntry(view))
        view.onDetachedOnce { removeStickyView(view) }

        recalculateViewPositions()
    }

    private fun recalculateViewPositions() {
        if (onPreDrawListener == null) {
            // if it is null, then create one
            onPreDrawListener = object : OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // obtain vertical positions
                    initViewPositions()
                    // sort views according to vertical position
                    views.sortBy { it.positionY }

                    scrollContainer.viewTreeObserver
                        .takeIf { it.isAlive }
                        ?.removeOnPreDrawListener(this)
                    onPreDrawListener = null

                    // trigger scroll event
                    onScrolled()
                    return true
                }
            }.also {
                scrollContainer.viewTreeObserver.addOnPreDrawListener(it)
            }
        }
        // else it is going to do it automatically (not null and added)
    }

    private fun relativeY(view: View): Int {
        var y = 0
        var who: View? = view
        while (who != null && who != scrollContainer) {
            y += who.top
            who = who.parent as? View
        }
        return y
    }

    private fun isChildOfScrollingView(view: View): Boolean {
        var who: View? = view.parent as? View
        while (who != null) {
            if (who == scrollContainer) {
                return true
            }
            who = who.parent as? View
        }
        return false
    }

    private fun initViewPositions() {
        views.forEach {
            it.positionY = relativeY(it.view)
        }
    }

    private fun changeStickyState(view: View, isSticky: Boolean) {
        val tag = (view.getTag(tagIsStickyId) as? Boolean) ?: false
        if (isSticky != tag) {
            view.setTag(tagIsStickyId, isSticky)
            stickyViewDecoration(view, isSticky)
        }
    }
}