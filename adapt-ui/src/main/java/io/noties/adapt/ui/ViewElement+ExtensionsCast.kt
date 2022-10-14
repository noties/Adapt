package io.noties.adapt.ui

import android.view.View

/**
 * Casts a [ViewElement] to specified view type. If cast fails an exception
 * is raised - [AdaptClassCastException]
 * There should be no need to cast element _down_, as by default this element
 * has all parent extensions available
 * @see ifCastView
 */
fun <V : View, LP : LayoutParams, RV : V> ViewElement<V, LP>.castView(
    view: Class<RV>,
): ViewElement<out RV, LP> {

    fun matches() = view.isAssignableFrom(this.view::class.java)
    fun deliver(cause: Throwable?) {
        throw AdaptClassCastException(
            "View ${this.view::class.java.name} cannot be cast to ${view.name}",
            cause
        )
    }

    if (isInitialized) {
        if (!matches()) {
            deliver(null)
        }
    } else {
        val cause = Throwable()
        onView {
            if (!matches()) {
                deliver(cause)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    return this as ViewElement<out RV, LP>
}

fun <V : View, LP : LayoutParams, RV : V> ViewElement<V, LP>.ifCastView(
    view: Class<RV>,
    block: (ViewElement<out RV, LP>) -> Unit
): ViewElement<V, LP> {

    fun matches() = view.isAssignableFrom(this.view::class.java)
    fun deliver() {
        @Suppress("UNCHECKED_CAST")
        block(this as ViewElement<out RV, LP>)
        render()
    }

    if (isInitialized) {
        if (matches()) deliver()
        return this
    }

    return onView {
        if (matches()) {
            post { deliver() }
        }
    }
}

fun <RLP, V : View, LP : LayoutParams> ViewElement<V, LP>.castLayout(
    layoutParams: Class<RLP>,
): ViewElement<V, out RLP> where RLP : LP {

    fun matches(): Boolean = layoutParams.isAssignableFrom(view.layoutParams::class.java)
    fun deliver(cause: Throwable?) {
        throw AdaptClassCastException(
            "LayoutParams ${this.view.layoutParams::class.java.name} cannot be cast to ${layoutParams.name}",
            cause
        )
    }

    if (isInitialized) {
        // no need to pass cause here, as we happen immediately where the call happens
        if (!matches()) deliver(null)
    } else {
        // keep track of current stacktrace, so it is easier explorable where the call happened
        val cause = Throwable()
        // castLayout must use onLayout -> so any further modification of the layoutParams
        //  would happen after we validate them
        onLayout {
            if (!matches()) deliver(cause)
        }
    }

    // here casting would not fail and always succeed
    //  crash would happen only when trying to access casted object
    @Suppress("UNCHECKED_CAST")
    return this as ViewElement<V, out RLP>
}

fun <V : View, LP : LayoutParams, RLP : LP> ViewElement<V, LP>.ifCastLayout(
    layoutParams: Class<RLP>,
    block: (ViewElement<V, out RLP>) -> Unit
): ViewElement<V, LP> {

    fun matches(): Boolean = layoutParams.isAssignableFrom(view.layoutParams::class.java)
    fun deliver() {
        @Suppress("UNCHECKED_CAST")
        block(this as ViewElement<V, out RLP>)
        render()
    }

    // if element is initialized, at this point it should not be in rendering phase
    // it could be if, for example, element tries to ifCastLayout during one of the callbacks
    /*
    Text()
      .also { element ->
        // but this is super crazy and must be discouraged
        element.onView {
          element.ifCastLayout(LinearLayout.LayoutParams::class.java)
        }
      }
     */
    if (isInitialized) {
        if (matches()) deliver()
        return this
    }

    val element = this
    return onLayout {
        // here we are inside rendering phase, post to view, to customize and render
        if (matches()) {
            element.view.post { deliver() }
        }
    }
}

internal class AdaptClassCastException(
    message: String,
    cause: Throwable?
) : RuntimeException(message, cause)