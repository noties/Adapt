package io.noties.adapt.ui.windowinset

import android.graphics.Insets
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.util.pxToDip

fun interface OnWindowInsetsChangedListenerRegistration {
    fun unregisterOnWindowInsetsChangedListener()
}

interface WindowInsetsFactoryBlock : OnWindowInsetsChangedListenerRegistration {

    val insetsLeading: Int
    val insetsTop: Int
    val insetsTrailing: Int
    val insetsBottom: Int

    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.applyWindowInsetsPadding(): ViewElement<V, LP> {
        return padding(
            leading = insetsLeading,
            top = insetsTop,
            trailing = insetsTrailing,
            bottom = insetsBottom
        )
    }
}

private class WindowInsetsFactoryBlockPre30(
    val insets: Rect,
    val unregister: () -> Unit
) : WindowInsetsFactoryBlock {
    override val insetsLeading: Int get() = insets.left.pxToDip
    override val insetsTop: Int get() = insets.top.pxToDip
    override val insetsTrailing: Int get() = insets.right.pxToDip
    override val insetsBottom: Int get() = insets.bottom.pxToDip

    override fun unregisterOnWindowInsetsChangedListener() {
        unregister()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private class WindowInsetsFactoryBlockInsets30(
    val insets: Insets,
    val unregister: () -> Unit
) : WindowInsetsFactoryBlock {
    override val insetsLeading: Int get() = insets.left.pxToDip
    override val insetsTop: Int get() = insets.top.pxToDip
    override val insetsTrailing: Int get() = insets.right.pxToDip
    override val insetsBottom: Int get() = insets.bottom.pxToDip

    override fun unregisterOnWindowInsetsChangedListener() {
        unregister()
    }

    override fun toString(): String {
        return "WindowInsetsFactoryBlockInsets(insets=$insets)"
    }
}

/**
 *  Multiple callbacks can be registered and will receive proper events. But to
 *  receive proper values specify all _affecting_ types in one call:
 *  ```kotlin
 *  Text()
 *    // default type: `systemBars`
 *    .onWindowInsetsChanged { applyWindowInsetsPadding() }
 *    // additionally listen to keyboard
 *    .onWindowInsetsChanged(insets = { ime }) { applyWindowInsetsPadding() }
 *    // 🛑 this will result in content being padded with `ime` values (which
 *    //   do not include `systemBars`), thus, top=0
 *
 *    // 🟠 to solve this, specify which types you are interested in one call:
 *    .onWindowInsetsChanged(insets = { systemBars.ime }) {
 *       applyWindowInsetsPadding()
 *    }
 *  ```
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onWindowInsetsChanged(
    insets: WindowInsetsFactory.() -> WindowInsetsType = { systemBars },
    element: WindowInsetsFactoryBlock.(ViewElement<V, LP>) -> Unit
) = this
    .also { el ->
        el.onView { v ->
            // in case of multiple subsribers for the same view - it is possible that some certain
            //  insert type would not be affected, like statusbars and ime, but after ime is hiden
            //  it will make sense to update elemnets listening for bars.
            val targetInsetsTypes = insets(WindowInsetsType)
            val delegate = OnWindowInsetsChangedListenerDelegate.getOrCreate(v)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                var lastInsets: Insets? = null
                delegate.add(object : OnWindowInsetsChangedListener {
                    override fun onWindowInsetsChanged(view: View, windowInsets: WindowInsets) {
                        val targeted = windowInsets.getInsets(targetInsetsTypes.rawValue)

                        if (targeted != lastInsets) {
                            lastInsets = targeted
                            val block =
                                WindowInsetsFactoryBlockInsets30(targeted) { delegate.remove(this) }
                            element(block, el)
                            el.render()
                        }
                    }
                })
            } else {
                val lastInsets = Rect()
                val rect = Rect()

                delegate.add(object : OnWindowInsetsChangedListener {
                    override fun onWindowInsetsChanged(view: View, windowInsets: WindowInsets) {
                        @Suppress("DEPRECATION")
                        rect.set(
                            windowInsets.systemWindowInsetLeft,
                            windowInsets.systemWindowInsetTop,
                            windowInsets.systemWindowInsetRight,
                            windowInsets.systemWindowInsetBottom
                        )
                        if (lastInsets != rect) {
                            lastInsets.set(rect)
                            val block =
                                WindowInsetsFactoryBlockPre30(rect) { delegate.remove(this) }
                            element(block, el)
                            el.render()
                        }
                    }
                })
            }
        }
    }

/**
 * Apply padding to the view when window-insets change. Reacts to window-insets
 * changes via [onWindowInsetsChanged] function and applies padding to the view.
 *
 * @see WindowInsetsType.systemBars
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.windowInsetsPadding(
    insets: WindowInsetsFactory.() -> WindowInsetsType = { systemBars }
) = onWindowInsetsChanged(insets = insets) {
    it.applyWindowInsetsPadding()
}

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.windowInsetsPaddingCompat(
) = windowInsetsPadding {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        systemBars.ime
    } else {
        systemBars
    }
}
