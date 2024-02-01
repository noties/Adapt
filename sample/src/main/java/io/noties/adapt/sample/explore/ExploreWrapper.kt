package io.noties.adapt.sample.explore

import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement

object ExploreWrapper {
    // can we create an extension to the factory that would take current view
    //  put instead of it our wrapper (or multiple) and add original view there as a child?

    // hm, this could be also complicated, as view could have received some specific customization
    //  that would be invalid for our wrapper. So, it could break the typesafety that we have.
    //  Better would be to explicitly put a view in another

    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.frameWrapper() {
        // could we access parent view-factory?
    }
}