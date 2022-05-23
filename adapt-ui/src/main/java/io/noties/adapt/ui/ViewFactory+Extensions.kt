package io.noties.adapt.ui

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams

@Suppress("PropertyName", "unused")
val ViewFactory<*>.FILL: Int
    get() {
        return LayoutParams.MATCH_PARENT
    }

@Suppress("PropertyName", "unused")
val ViewFactory<*>.WRAP: Int
    get() {
        return LayoutParams.WRAP_CONTENT
    }


fun <G : ViewGroup, LP : LayoutParams> ViewFactory.Companion.addChildren(
    g: G,
    children: ViewFactory<LP>.() -> Unit
) {

    val factory = ViewFactory<LP>()
    children(factory)

    factory.elements.forEach { el ->
        @Suppress("UNCHECKED_CAST")
        el as ViewElement<View, LP>

        val view = el.provider(g.context)

        // now layoutParams are generated
        g.addView(view)

        @Suppress("UNCHECKED_CAST")
        val lp = view.layoutParams as LP
        el.layoutBlocks.forEach { it(lp) }
        el.viewBlocks.forEach { it(view) }

        view.requestLayout()
    }
}

// TODO: create view method
//fun ViewFactory.Companion.createView()