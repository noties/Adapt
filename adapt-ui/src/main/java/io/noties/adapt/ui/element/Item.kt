package io.noties.adapt.ui.element

import android.os.Build
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.Item
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.R
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import kotlin.reflect.KMutableProperty0

/**
 * A typealias to facilitate item update process
 * ```kotlin
 * lateinit var updateItem: UpdateItem<MyItem>
 *
 * fun hey() {
 *   updateItem { item ->
 *     item.myVariable = true
 *   }
 * }
 * ```
 * @see referenceUpdate
 * @see ItemElement.component2
 */
typealias UpdateItem<I> = ((I) -> Unit) -> Unit

/**
 * ```kotlin
 * // NB! this `Item` cannot be root for `createView`:
 * ViewFactory.createView(context) {
 *   Item()
 * }
 *
 * // Can be root if viewGroup is available:
 * ViewFactory.newView(viewGroup).create {
 *   Item()
 * }
 *
 * // Or if a child of another container:
 * ViewFactory.createView(context) {
 *   VStack {
 *     // this is ok, as it is inside VStack, which has viewGroup
 *     Item()
 *   }
 * }
 */
@Suppress("FunctionName")
fun <LP : LayoutParams, H : Item.Holder, I : Item<H>> ViewFactory<LP>.Item(
    item: I,
    changeHandlerConfigurator: ItemElement.ChangeHandlerConfigurator? = null
): ItemElement<H, I, LP> {
    return ItemElement<H, I, LP>(viewGroup, item, changeHandlerConfigurator).also { add(it) }
}

fun <H : Item.Holder, I : Item<H>, LP : LayoutParams> ItemElement<H, I, LP>.referenceUpdate(
    property: KMutableProperty0<in UpdateItem<I>>
) = this.also {
    property.set(component2())
}

/**
 * ```kotlin
 * VStack {
 *   val (item, updateItem) = Item(MyItem())
 *
 *   Text("A button")
 *     .onClick {
 *       updateItem { it.toggleSomeVariable() }
 *     }
 *
 *   // in order to reference `updateItem` and also customize view
 *   val (_, updateItem2) = Item(MyItem())
 *     .also {
 *       it.background(Color.RED)
 *     }
 * }
 * ```
 */
class ItemElement<H : Item.Holder, I : Item<H>, LP : LayoutParams>(
    val viewGroup: ViewGroup,
    val item: I,
    changeHandlerConfigurator: ChangeHandlerConfigurator?
) : ViewElement<View, LP>({ context ->
    // at first, AdaptView was used, but it is not the best here, as we need to create
    //  actual item view here, so it is possible to configure it (via regular
    //  view-element extensions). Before a placeholder view was created here and
    //  then in `onView` AdaptView initialized, but this would discard any
    //  possible element customizations (paddings, backgrounds, etc), as view would be
    //  immediately replaced by item..
    val holder = item.createHolder(LayoutInflater.from(context), viewGroup)
    val view = holder.itemView()
    view.setTag(TAG_HOLDER, holder)
    view
}) {

    init {
        onView { bind(it) }
    }

    fun interface ChangeHandlerConfigurator {
        fun configure(configuration: ChangeHandlerConfiguration)
    }

    interface ChangeHandlerConfiguration {
        fun changeHandler(changeHandler: (View) -> Unit)
        fun changeHandlerTransitionSelf()
        fun changeHandlerTransitionParent()
    }

    fun update(block: (I) -> Unit = {}) = this.also { element ->
        block(element.item)

        if (element.isInitialized) {
            configuration.changeHandler?.invoke(element.view)
            bind(element.view)
        }
    }

    operator fun component1(): I = item
    operator fun component2(): UpdateItem<I> = {
        update(it)
    }

    private fun bind(view: View) {
        @Suppress("UNCHECKED_CAST")
        val holder = view.getTag(TAG_HOLDER) as H
        item.bind(holder)
    }

    private val configuration =
        Configuration().also { changeHandlerConfigurator?.configure(it) }

    private class Configuration : ChangeHandlerConfiguration {

        var changeHandler: ((View) -> Unit)? = null

        override fun changeHandler(changeHandler: (View) -> Unit) {
            this.changeHandler = changeHandler
        }

        override fun changeHandlerTransitionSelf() {
            changeHandler = {
                val viewGroup = (it as? ViewGroup) ?: it.parent as? ViewGroup
                if (viewGroup != null) {
                    endTransitions(viewGroup)
                    TransitionManager.beginDelayedTransition(viewGroup)
                }
            }
        }

        override fun changeHandlerTransitionParent() {
            changeHandler = {
                val viewGroup = it.parent as? ViewGroup
                if (viewGroup != null) {
                    endTransitions(viewGroup)
                    TransitionManager.beginDelayedTransition(viewGroup)
                }
            }
        }

        private fun endTransitions(viewGroup: ViewGroup) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TransitionManager.endTransitions(viewGroup)
            }
        }
    }

    private companion object {
        val TAG_HOLDER = R.id.adaptui_internal_element_item
    }
}