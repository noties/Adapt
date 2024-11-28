package io.noties.adapt.ui.util

import android.app.Activity
import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory

/**
 * ```kotlin
 * fun onCreate(sis: Bundle?) {
 *   super.onCreate(sis)
 *
 *   setContentUI {
 *     // .layout(FILL, FILL) by default
 *     ZStack {
 *       Text("Hello!")
 *     }
 *   }
 * }
 * ```
 */
fun Activity.setContentUI(
    block: ViewFactory<LayoutParams>.() -> Unit
): View {
    return ViewFactory.createView(this) {
        block()
    }.also {
        // if view does not set LayoutParams MATCH|MATCH are used as defaults
        if (it.layoutParams == null) {
            it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        setContentView(it)
    }
}