package io.noties.adapt.sample.explore

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.ElementGroup
import io.noties.adapt.ui.util.addOnHierarchyChangeListener

@Suppress("FINAL_UPPER_BOUND")
object ExploreButton {
    // style would be a little hard, as we have no views (no TextView) specifically, which we can style
    // we could collect text-style here (somehow) and apply to teh first TextView child
    @Suppress("FunctionName")
    fun <LP : LayoutParams> ViewFactory<LP>.Button(
        children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
    ) = ElementGroup(
        provider = { ButtonLayout(it) },
        children = children
    )

    class ButtonStyle(
        val container: (ViewElement<ButtonLayout, *>) -> Unit,
        val text: (ViewElement<TextView, *>) -> Unit
    ) {
        companion object {
            fun empty() = ButtonStyle(
                container = {},
                text = {}
            )
        }
    }

    interface ButtonStyleFactory {
        fun container(container: (ViewElement<ButtonLayout, *>) -> Unit)
        fun text(text: (ViewElement<TextView, *>) -> Unit)

        fun build(): ButtonStyle
    }

    fun <V : ButtonLayout, LP : LayoutParams> ViewElement<V, LP>.buttonStyle(
        style: ButtonStyleFactory.() -> Unit
    ) = onView {
        val factory: ButtonStyleFactory = run { TODO() }
        style(factory)
        val buttonStyle = factory.style()
    }

    class ButtonLayout : FrameLayout {

        var style: ButtonStyle = ButtonStyle.empty()
            set(value) {
                field = value
                applyStyle(value)
            }

        constructor(context: Context) : super(context)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        init {
            addOnHierarchyChangeListener(object: OnHierarchyChangeListener {
                override fun onChildViewAdded(parent: View?, child: View?) {
                    applyStyle(style)
                }

                override fun onChildViewRemoved(parent: View?, child: View?) {
                }
            })
        }

        private fun applyStyle(style: ButtonStyle) {
            invalidate()
        }
    }
}