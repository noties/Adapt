package io.noties.adapt.sample.explore

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.style.ViewStyles
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.style
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textSize

object ExploreWrapper {
    // can we create an extension to the factory that would take current view
    //  put instead of it our wrapper (or multiple) and add original view there as a child?

    // hm, this could be also complicated, as view could have received some specific customization
    //  that would be invalid for our wrapper. So, it could break the typesafety that we have.
    //  Better would be to explicitly put a view in another

    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.frameWrapper() {
        // could we access parent view-factory?
    }

    val ViewStyles.appBarTitle get() = styleView<TextView> {
        it
            .textSize { 16 }
            .textBold()
    }

    val ViewStyles.appBarTitleInZStack get() = styleViewLayout<TextView, FrameLayout.LayoutParams> {
        it
    }

    fun hey(context: Context) {
        ViewFactory.createView(context) {
            ZStack {
                Text("")
                    .style { appBarTitle }
                    .style { appBarTitleInZStack }

//                VStack {
//                    Text("")
//                        .style { appBarTitleInZStack }
//                }
            }
        }
    }
}