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
    // just in case supplied activity would be used as a reference
    val activity = this
    return ViewFactory.newView(this)
        // MATCH|MATCH is used by default (if supplied view has it -> they would not be overriding
        //  supplied values)
        .layoutParams(LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        .create(block)
        .also {
            setContentView(it)
        }
}