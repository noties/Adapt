package io.noties.adapt.sample.explore

import android.content.Context
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.Item
import io.noties.adapt.sample.items.PlainItem
import io.noties.adapt.sample.samples.adaptui.Colors
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.view.AdaptView

@Suppress("FunctionName")
object ExploreDynamicItem {
    // adapt-view
    //  1. it is a little hard to manage, is it adding a view to view-group or not
    //      maybe, it is better to not add anything implicitly
    //  2. also, it is a little weird, that it requires view-group, well, it is not,
    //      but again, adding view to it directly is a little weird

    // AdaptView.init(viewGroup) -> creates placeholder view and then replaces it with item
    // AdaptView.init(viewGroup, item) -> immediately attaches the view to view-group
    //

    // so, can we can item directly when building layout with view-factory?

    // TODO: should view configuration be preserved? so, we apply some configuration
    //  to returned element, should we also send it to the view-created? what if now
    //  configuration is applying some styling to placeholder, but then, should we
    //  also apply it to the created view? at least once? hm... this is interesting, and what if
    //  item changes? maybe this element should not apply changing actual item, so it is created
    //  only once? yep, seems right. then, we should replace the views somehow BEFORE
    //  onView callbacks (maybe just registering first one, would do?)
    //  UPD, so, element keeps referencing placeholder view, so everything is applied to it,
    //  even though it is no longer in layout... CAN WE REPLACE REFERENCED VIEW IN LAYOUT?
    //  OR MAYBE WE can create it right away... it is a little hard to do as we have no access
    //  to the view-group, maybe add onView? but we need to return it right away
    class ItemElement<H : Item.Holder, I : Item<H>, LP : LayoutParams>(
        val viewGroup: ViewGroup,
        val item: I,
        private var changeHandlerConfigurator: ItemAdaptViewChangeHandlerConfigurator?
    ) : ViewElement<View, LP>({ context ->
        // create here placeholder view, when it is going to be attached -> replace it
        //  with adapt-view view
        View(context).also {
            // use explicit 0 dimensions (some view-groups can treat it as FILL-FILL, which is bad)
            it.layoutParams = ViewGroup.LayoutParams(0, 0)
        }
    }) {

        init {
            onView { view ->
                adapt = AdaptView.init(viewGroup) { configuration ->
                    configuration.item(item)
                    configuration.placeholderView(view)
                    changeHandlerConfigurator?.also {
                        it.configure(object : ItemAdaptViewChangeHandlerConfiguration {
                            override fun changeHandler(changeHandler: AdaptView.ChangeHandler) {
                                configuration.changeHandler(changeHandler)
                            }

                            override fun changeHandlerTransitionSelf() {
                                configuration.changeHandlerTransitionSelf()
                            }

                            override fun changeHandlerTransitionParent() {
                                configuration.changeHandlerTransitionParent()
                            }
                        })
                    }
                }
                // no longer needed
                changeHandlerConfigurator = null
            }
        }

        private lateinit var adapt: AdaptView

        fun update(block: (I) -> Unit = {}) = this.also { element ->
            block(element.item)
            if (::adapt.isInitialized) {
                element.render()
                adapt.notifyChanged()
            }
        }

        operator fun component1(): I = item
        operator fun component2(): ((I) -> Unit) -> Unit = {
            update(it)
        }
    }

    fun interface ItemAdaptViewChangeHandlerConfigurator {
        fun configure(configuration: ItemAdaptViewChangeHandlerConfiguration)
    }

    interface ItemAdaptViewChangeHandlerConfiguration {
        fun changeHandler(changeHandler: AdaptView.ChangeHandler)
        fun changeHandlerTransitionSelf()
        fun changeHandlerTransitionParent()
    }

    // Let's make it requirement for view-factory to contain view-group...
    //  otherwise it is a weird thing
    // we need a way to reference it and a way to update it (triggering update)
    // TODO: Cannot not be root ViewFactory { Item() } -> nope
    fun <LP : LayoutParams, H : Item.Holder, I : Item<H>> ViewFactory<LP>.Item(
        item: I,
        changeHandlerConfigurator: ItemAdaptViewChangeHandlerConfigurator? = null
    ): ItemElement<H, I, LP> {
        return ItemElement<H, I, LP>(viewGroup, item, changeHandlerConfigurator).also { add(it) }
    }

    fun hey(context: Context) = ViewFactory.createView(context) {
        VStack {
            Item(PlainItem("P", Colors.black, "The title"))
        }.layoutFill()
    }
}