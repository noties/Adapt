package io.noties.adapt.sample.explore

import android.app.Activity
import android.widget.FrameLayout
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text

object ExploreDialog {
    
    fun hey(a: Activity) {
        Dialog.show(a) {
            Header {
                Text("")
                //. style title

                // Close
                //  also, need to dismiss somehow (to receive something that would allow to dismiss)
            }
        }
    }

    class Dialog private constructor() {
        companion object {
            fun show(activity: Activity, block: DialogBuilder.() -> Unit) {
//            val builder = DialogBuilderImpl()
//            val element = block(builder)
                // TODO
            }
        }
    }

    interface DialogBuilder {
        @Suppress("FunctionName")
        fun DialogBuilder.Header(block: ViewFactory<FrameLayout.LayoutParams>.() -> Unit) {

        }

        @Suppress("FunctionName")
        fun DialogBuilder.Content(block: ViewFactory<FrameLayout.LayoutParams>.() -> Unit) {

        }

        @Suppress("FunctionName")
        fun DialogBuilder.Footer(block: ViewFactory<FrameLayout.LayoutParams>.() -> Unit) {

        }
    }
}