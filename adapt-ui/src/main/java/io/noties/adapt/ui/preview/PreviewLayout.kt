package io.noties.adapt.ui.preview

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.noties.adapt.ui.ViewFactory

/**
 * @since $UNRELEASED;
 */
abstract class PreviewLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        initialize(this)

        val factory = ViewFactory<LayoutParams>(context)
        factory.body()

        factory.elements.forEach { e ->
            val view = e.init(context)
            addView(view)
            e.render()
        }
    }

    abstract fun ViewFactory<LayoutParams>.body()

    open fun initialize(layout: PreviewLayout) = Unit
}