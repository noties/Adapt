package io.noties.adapt.ui.element

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.reference
import kotlin.reflect.KMutableProperty0

/**
 * Special subclass of a [ViewElement] that allows special extensions on it,
 * like [inflatedView].
 * One of the downside is that obtaining inflated views should happen before any possible
 * generic customizations, as those do return just [ViewElement]
 */
class InflatedViewElement<LP : LayoutParams>(
    @LayoutRes val layoutResId: Int,
    provider: (Context) -> View
) : ViewElement<View, LP>(provider) {
    internal companion object {
        fun resourceName(context: Context, id: Int): String {
            return try {
                context.resources.getResourceName(id)
            } catch (e: Resources.NotFoundException) {
                id.toString()
            }
        }
    }
}

/**
 * Special element that uses [LayoutInflater] to create view and view-element
 * given the XML layout resource.
 * @see inflatedView
 * @see inflatedViewReference
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.Inflated(
    @LayoutRes layout: Int,
    layoutInflater: LayoutInflater? = null
) = InflatedViewElement<LP>(layout) {
    val inflater = layoutInflater ?: LayoutInflater.from(it)
    val parent = if (hasViewGroup) viewGroup else null
    // NB! we are not attaching, because view factory will do it
    inflater.inflate(layout, parent, false)
}.also { add(it) }

/**
 * Obtains a [View] given its [id]. Please note that this function assumes view to be
 * present in layout, otherwise it would throw an error.
 * Also, please note that returned [InflatedViewElement] does not have any information
 * about [LayoutParams], as looked-up view can be a child of a nested layout. You could
 * cast manually with [io.noties.adapt.ui.castLayout] or [io.noties.adapt.ui.ifCastLayout]
 */
fun <V : View, LP : LayoutParams> InflatedViewElement<LP>.inflatedView(
    @IdRes id: Int,
    block: (ViewElement<V, out LayoutParams>) -> Unit
) = this.also { ie ->
    ie.onView {

        val view = it.findViewById(id) as? V
            ?: kotlin.run {
                val idName = InflatedViewElement.resourceName(it.context, id)
                val layoutName = InflatedViewElement.resourceName(it.context, ie.layoutResId)
                error("View with id:'$idName' not found in layout:'$layoutName'")
            }

        ViewElement.create(view)
            .also(block)
            // as view is initialized already, call render immediately
            .render()
    }
}

/**
 * ```kotlin
 * Inflated(0)
 *   .inflatedViewReference(0, ::myView)
 * ```
 * @see inflatedView
 */
@JvmName("inflatedViewReferenceView")
fun <V : View, LP : LayoutParams> InflatedViewElement<LP>.inflatedViewReference(
    @IdRes id: Int,
    property: KMutableProperty0<in V>,
): InflatedViewElement<LP> = inflatedView(id) {
    it.reference(property)
}

/**
 * ```kotlin
 * Inflated(0)
 *   .inflatedViewReference(0, ::myElement)
 * ```
 * @see inflatedView
 */
@JvmName("inflatedViewReferenceElement")
fun <V : View, LP : LayoutParams> InflatedViewElement<LP>.inflatedViewReference(
    @IdRes id: Int,
    property: KMutableProperty0<in ViewElement<V, out LayoutParams>>
): InflatedViewElement<LP> = inflatedView<V, _>(id) {
    it.reference(property)
}