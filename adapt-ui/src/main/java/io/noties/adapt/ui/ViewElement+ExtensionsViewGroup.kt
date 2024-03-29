package io.noties.adapt.ui

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.util.children

/**
 * Enabled. Additionally can apply specified `enabled` to all children of the [ViewGroup] recursively,
 * as normally ViewGroup does not change the state of its children.
 * @see View.setEnabled
 */
fun <V : ViewGroup, LP : LayoutParams> ViewElement<V, LP>.enabled(
    enabled: Boolean,
    applyToChildren: Boolean = false
): ViewElement<V, LP> = onView { view ->
    if (applyToChildren) {
        fun enable(view: View) {
            view.isEnabled = enabled
            //noinspection NewApi
            (view as? ViewGroup)?.children?.forEach { enable(it) }
        }
        enable(view)
    } else {
        view.isEnabled = enabled
    }
}

/**
 * Activated. Additionally can apply specified `activated` to all children of this [ViewGroup] recursively,
 * as normally ViewGroup does not change the state of its children.
 * @see View.setActivated
 */
fun <V : ViewGroup, LP : LayoutParams> ViewElement<V, LP>.activated(
    activated: Boolean,
    applyToChildren: Boolean = false
): ViewElement<V, LP> = onView { view ->
    if (applyToChildren) {
        fun activate(view: View) {
            view.isActivated = activated
            //noinspection NewApi
            (view as? ViewGroup)?.children?.forEach { activate((it)) }
        }
        activate(view)
    } else {
        view.isActivated = activated
    }
}

/**
 * ClipChildren
 * @see ViewGroup.setClipChildren
 */
fun <V : ViewGroup, LP : LayoutParams> ViewElement<V, LP>.clipChildren(
    clipChildren: Boolean
): ViewElement<V, LP> = onView {
    it.clipChildren = clipChildren
}

/**
 * ClipToPadding
 * @see ViewGroup.setClipToPadding
 */
fun <V : ViewGroup, LP : LayoutParams> ViewElement<V, LP>.clipToPadding(
    clipToPadding: Boolean
): ViewElement<V, LP> = onView {
    it.clipToPadding = clipToPadding
}


/**
 * NoClip
 * @see clipToPadding
 * @see clipChildren
 */
fun <V : ViewGroup, LP : LayoutParams> ViewElement<V, LP>.noClip(): ViewElement<V, LP> =
    this.clipChildren(false)
        .clipToPadding(false)

/**
 * @see ViewGroup.setTransitionGroup
 */
fun <V : ViewGroup, LP : LayoutParams> ViewElement<V, LP>.transitionGroup(
    transitionGroup: Boolean = true
): ViewElement<V, LP> = onView {
    it.isTransitionGroup = transitionGroup
}