package io.noties.adapt.sample.explore

import androidx.annotation.IdRes

object ExploreViewTag {
    // an utility to save/retrieve data from a view given its tag

    @JvmInline
    value class TagId(@IdRes val rawValue: Int) {
        companion object {

            val first get() = TagId(0)
            val second get() = TagId(0)
            val third get() = TagId(0)

            fun raw(@IdRes value: Int) = TagId(value)
        }
    }


}