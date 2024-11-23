package io.noties.adapt.ui.preview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

abstract class PreviewLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    abstract fun createView(context: Context, parent: PreviewLayout): View


    open fun initialize(layout: PreviewLayout) = Unit

    open fun onCreated() = Unit


    init {
        PreviewApplication.install(context)

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        initialize(this)

        val view = createView(context, this)

        addView(view)

        onCreated()
    }
}