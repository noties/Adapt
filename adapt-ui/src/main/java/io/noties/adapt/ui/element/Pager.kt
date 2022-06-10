package io.noties.adapt.ui.element

import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

@Suppress("FunctionName", "unused")
fun <LP : ViewGroup.LayoutParams> ViewFactory<LP>.Pager(): ViewElement<ViewPager, LP> {
    return ViewElement<ViewPager, LP> {
        ViewPager(it)
    }.also(elements::add)
}