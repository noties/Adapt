package io.noties.adapt.ui.flex

import android.content.Context
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout

object FlexElementViewFactory {
    lateinit var Flex: (Context) -> FlexboxLayout

    init {
        reset()
    }

    fun reset() {
        Flex = {
            FlexboxLayout(it).also { fl ->
                // let it wrap by default
                fl.flexWrap = FlexWrap.WRAP
            }
        }
    }
}