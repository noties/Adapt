package io.noties.adapt.sample.explore

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

@Suppress("StopShip")
object ExploreDistributeLayout {
    // predefined factories:
    // - Distribute.equalSpacing
    // - Distribute.spacedAround
    //  - etc
    // but in the end, it accepts the whole logic:
    //  Distribute
    //      getChildWeight (shouldn't it be specified in LP?)

    // maybe for this we can use FlexboxLayout?

    class DistributeLayout(context: Context?) : ViewGroup(context) {
        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            TODO("Not yet implemented")
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

        class LayoutParams: ViewGroup.LayoutParams {
            constructor(c: Context, attrs: AttributeSet?) : super(c, attrs)
            constructor(width: Int, height: Int) : super(width, height)
            constructor(source: ViewGroup.LayoutParams) : super(source)
        }
    }
}