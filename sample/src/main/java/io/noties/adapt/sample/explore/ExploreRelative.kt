package io.noties.adapt.sample.explore

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.ElementGroup
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.util.Gravity

object ExploreRelative {

    @Suppress("FunctionName")
    fun <LP : ViewGroup.LayoutParams> ViewFactory<LP>.Relative(
        gravity: Gravity? = null,
        children: ViewFactory<RelativeLayout.LayoutParams>.() -> Unit
    ) = ElementGroup(
        { RelativeLayout(it) },
        {
            if (gravity != null) {
                it.gravity = gravity.value
            }
        },
        children
    )

/*
setIgnoreGravity(viewId), view would have ignored gravity
setGravity
setHorizontalGravity
setVerticalGravity
 */

    fun <V : View, LP : RelativeLayout.LayoutParams> ViewElement<V, LP>.layoutPosition(
        toLeading: ViewElement<*, LP>? = null,
        above: ViewElement<*, LP>? = null,
        toTrailing: ViewElement<*, LP>? = null,
        below: ViewElement<*, LP>? = null,
    ) = this.also {
        toLeading?.also { addRule(RelativeLayout.LEFT_OF, this, it) }
        above?.also { addRule(RelativeLayout.ABOVE, this, it) }
        toTrailing?.also { addRule(RelativeLayout.RIGHT_OF, this, it) }
        below?.also { addRule(RelativeLayout.BELOW, this, it) }
    }

    fun <V : View, LP : RelativeLayout.LayoutParams> ViewElement<V, LP>.layoutAlign(
        leading: ViewElement<*, LP>? = null,
        top: ViewElement<*, LP>? = null,
        trailing: ViewElement<*, LP>? = null,
        bottom: ViewElement<*, LP>? = null,
        baseline: ViewElement<*, LP>? = null
    ) = this.also {
        leading?.also { addRule(RelativeLayout.ALIGN_LEFT, this, it) }
        top?.also { addRule(RelativeLayout.ALIGN_TOP, this, it) }
        trailing?.also { addRule(RelativeLayout.ALIGN_RIGHT, this, it) }
        bottom?.also { addRule(RelativeLayout.ALIGN_BOTTOM, this, it) }
        baseline?.also { addRule(RelativeLayout.ALIGN_BASELINE, this, it) }
    }

    // TODO: should those be by default true?
    fun <V : View, LP : RelativeLayout.LayoutParams> ViewElement<V, LP>.layoutAlignParent(
        horizontal: Boolean? = null,
        vertical: Boolean? = null
    ) = layoutAlignParent(
        leading = horizontal,
        top = vertical,
        trailing = horizontal,
        bottom = vertical
    )

    // TODO: should those be by default true?
    fun <V : View, LP : RelativeLayout.LayoutParams> ViewElement<V, LP>.layoutAlignParent(
        leading: Boolean? = null,
        top: Boolean? = null,
        trailing: Boolean? = null,
        bottom: Boolean? = null
    ) = this.also {
        it.onLayoutParams { lp ->

            fun apply(boolean: Boolean?, rule: Int) {
                if (boolean != null && boolean) {
                    lp.addRule(rule)
                }
            }

            apply(leading, RelativeLayout.ALIGN_PARENT_LEFT)
            apply(top, RelativeLayout.ALIGN_PARENT_TOP)
            apply(trailing, RelativeLayout.ALIGN_PARENT_RIGHT)
            apply(bottom, RelativeLayout.ALIGN_PARENT_BOTTOM)
        }
    }

    fun <V : View, LP : RelativeLayout.LayoutParams> ViewElement<V, LP>.layoutCenter(
        horizontal: Boolean? = null,
        vertical: Boolean? = null
    ) = onLayoutParams { lp ->
        horizontal?.also { lp.addRule(RelativeLayout.CENTER_HORIZONTAL) }
        vertical?.also { lp.addRule(RelativeLayout.CENTER_VERTICAL) }
    }

    fun <V : View, LP : RelativeLayout.LayoutParams> ViewElement<V, LP>.layoutCenterInParent() =
        onLayoutParams {
            it.addRule(RelativeLayout.CENTER_IN_PARENT)
        }

    private fun <V : View, LP : RelativeLayout.LayoutParams> addRule(
        rule: Int,
        source: ViewElement<V, LP>,
        target: ViewElement<*, LP>
    ): ViewElement<V, LP> = source.also {
        target.ensureId { id ->
            source.onLayoutParams {
                it.addRule(rule, id)
            }
        }
    }

    private fun <LP : RelativeLayout.LayoutParams> ViewElement<*, LP>.ensureId(block: (Int) -> Unit) {

        fun process(view: View) {
            val id = view.id
            if (id != View.NO_ID) {
                block(id)
            } else {
                val newId = View.generateViewId()
                view.id = newId
                block(newId)
            }
        }

        if (isInitialized) {
            process(view)
        } else {
            onView {
                process(it)
            }
        }
    }

    fun hey(context: Context) {
        class Ref {
            // will hold a reference to created TextView
            lateinit var textView: TextView
        }

        val ref = Ref()

        val view = ViewFactory.createView(context) {
            VStack {
                Text("My Text")
                Text("Another text")
                    // pass mutable property to the `reference` extension
                    .reference(ref::textView)
                    // which can be also done with `onView` extension
                    .onView { ref.textView = it }
            }
        }

        // view is LinearLayout with VERTICAL orientation

        // will update referenced TextView
        ref.textView.text = "Hello another text"
    }
}