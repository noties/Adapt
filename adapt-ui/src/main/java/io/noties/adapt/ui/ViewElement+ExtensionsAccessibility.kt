package io.noties.adapt.ui

import android.os.Build
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes

/**
 * Accessibility, content description
 * @see View.setContentDescription
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.accessibilityDescription(
    contentDescription: CharSequence?,
): ViewElement<V, LP> = onView {
    this.contentDescription = contentDescription
}

/**
 * @see View.setContentDescription
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.accessibilityDescription(
    @StringRes contentDescriptionResId: Int,
): ViewElement<V, LP> = onView {
    this.contentDescription = resources.getString(contentDescriptionResId)
}

@JvmInline
value class ImportantForAccessibility(val value: Int) {
    companion object {
        val yes: ImportantForAccessibility get() = ImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES)
        val no: ImportantForAccessibility get() = ImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO)
        val noHideDescendants: ImportantForAccessibility get() = ImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS)
        val auto: ImportantForAccessibility get() = ImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO)
    }
}

/**
 * @see View.setImportantForAccessibility
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.accessibilityImportant(
    importantForAccessibility: Boolean
): ViewElement<V, LP> =
    accessibilityImportant(if (importantForAccessibility) ImportantForAccessibility.yes else ImportantForAccessibility.no)

/**
 * @see View.setImportantForAccessibility
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.accessibilityImportant(
    importantForAccessibility: ImportantForAccessibility
): ViewElement<V, LP> = onView {
    this.importantForAccessibility = importantForAccessibility.value
}

/**
 * @see View.setLabelFor
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.accessibilityLabelFor(
    @IdRes targetId: Int,
): ViewElement<V, LP> = onView {
    labelFor = targetId
}

/**
 * @see View.setLabelFor
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.accessibilityLabelFor(
    provider: () -> ViewElement<out View, *>,
): ViewElement<V, LP> = this.also {
    ensureTarget(it, provider) { view, id ->
        view.labelFor = id
    }
}

/**
 * @see View.setAccessibilityTraversalBefore
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.accessibilityTraversalBefore(
    @IdRes targetId: Int,
): ViewElement<V, LP> = onView {
    accessibilityTraversalBefore = targetId
}

/**
 * @see View.setAccessibilityTraversalBefore
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.accessibilityTraversalBefore(
    provider: () -> ViewElement<out View, *>,
): ViewElement<V, LP> = this.also {
    ensureTarget(it, provider) { view, id ->
        view.accessibilityTraversalBefore = id
    }
}

/**
 * @see View.setAccessibilityTraversalAfter
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.accessibilityTraversalAfter(
    @IdRes targetId: Int,
): ViewElement<V, LP> = onView {
    accessibilityTraversalAfter = targetId
}

/**
 * @see View.setAccessibilityTraversalAfter
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.accessibilityTraversalAfter(
    provider: () -> ViewElement<out View, *>,
): ViewElement<V, LP> = this.also {
    ensureTarget(it, provider) { view, id ->
        view.accessibilityTraversalAfter = id
    }
}

@JvmInline
value class AccessibilityLiveRegion(val value: Int) {
    companion object {
        val none: AccessibilityLiveRegion get() = AccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_NONE)
        val polite: AccessibilityLiveRegion get() = AccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE)
        val assertive: AccessibilityLiveRegion get() = AccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_ASSERTIVE)
    }
}

/**
 * @see View.setAccessibilityLiveRegion
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.accessibilityLiveRegion(
    liveRegion: AccessibilityLiveRegion,
): ViewElement<V, LP> = onView {
    accessibilityLiveRegion = liveRegion.value
}


// if view has id use it, otherwise generate a new id (and assign it to the view)
private fun View.ensureId(): Int = this.id
    .takeIf { it != View.NO_ID }
    ?: View.generateViewId().also { this.id = it }

private fun ensureTarget(
    element: ViewElement<out View, *>,
    targetProvider: () -> ViewElement<out View, *>,
    onTargetIdReady: (view: View, id: Int) -> Unit
) {
    element.onView {
        val view = this
        val targetElement = targetProvider()
        val targetView = targetElement.takeIf { it.isInitialized }?.view
        if (targetView == null) {
            targetElement.onView {
                onTargetIdReady(view, this.ensureId())
            }
        } else {
            onTargetIdReady(view, targetView.ensureId())
        }
    }
}