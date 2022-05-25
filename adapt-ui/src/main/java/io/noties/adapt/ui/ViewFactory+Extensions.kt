package io.noties.adapt.ui

import android.content.Context
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

@JvmName("addChildrenViewGroup")
fun ViewFactory.Companion.addChildren(
    group: ViewGroup,
    children: ViewFactory<LayoutParams>.() -> Unit
) = addChildren<ViewGroup, LayoutParams>(group, children)

@JvmName("addChildrenGeneric")
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

fun ViewFactory.Companion.createView(
    context: Context,
    children: ViewFactory<LayoutParams>.() -> Unit
): View {
    return createView(context, Unit) { children() }
}

fun <R> ViewFactory.Companion.createView(
    context: Context,
    references: R,
    children: ViewFactory<LayoutParams>.(R) -> Unit
): View {

    val factory = ViewFactory<LayoutParams>()
    children(factory, references)

    // ensure single element
    if (factory.elements.size != 1) {
        throw IllegalStateException("Unexpected state, view must contain exactly one root element")
    }

    @Suppress("UNCHECKED_CAST")
    val root = factory.elements[0] as ViewElement<View, LayoutParams>

    val view = root.provider(context)

    // default layout params
    val lp = LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT
    )

    view.layoutParams = lp

    root.layoutBlocks.forEach { it(lp) }
    root.viewBlocks.forEach { it(view) }

    return view
}