package io.noties.adapt.sample.explore

import android.content.Context
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.Item
import io.noties.adapt.sample.R
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
    // so, can we can item directly when building layout with view-factory?

    // TODO: proper tag
    private const val TAG = R.id.adapt_internal

//    // should come up with a name...
//    interface SomeItem<H : Item.Holder, I : Item<H>> {
//        val item: I
//        fun update(block: (I) -> Unit = {})
//    }

    // TODO: should we open the render method in ViewElement? or give ability to receive
    //  render event somehow?
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
        val item: I
    ) : ViewElement<View, LP>({ _ ->
//        // create here placeholder view, when it is going to be attached -> replace it
//        //  with adapt-view view
//        val view = View(context)
//        // use explicit 0 dimensions (some view-groups can treat it as FILL-FILL, which is bad)
//        view.layoutParams = ViewGroup.LayoutParams(0, 0)
//        // TODO: should we substitute to `onView`?
////        view.onAttachedOnce {
////            // TODO: validate this... would parent be present in this callback?
////            val parent = it.parent as ViewGroup
////            val index = parent.indexOfChild(it)
////            parent.removeViewAt(index)
////
////            // TODO: adapt-view somehow should receive the view that we are interested in..
////            //  it can be at different index
////            val adapt = AdaptView.init(parent, item)
////        }
//        view
        // do we actually need AdaptView here, maybe we can do behind the scenes ourselves?
        // placeholder is nice... but now it is actual view, can we pass it? or should we?
        //  wouldn't it be in proper place already? as children are being called one-by-one?
        // TODO: Ah, shit, adapt-view adds view automatically... we must not add it!!!
        val adapt = AdaptView.init(viewGroup, item)
        // now.. we need to send created adapt to the element...
        adapt.view().also {
            it.setTag(TAG, adapt)
        }
    }) {

        init {
            onView {
                adapt = it.getTag(TAG) as AdaptView
            }
        }

        private lateinit var adapt: AdaptView
//        private val isAdaptInitialized: Boolean get() = this::adapt.isInitialized

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

    // TODO: create ItemElement? hm, we already have similar name
    // TODO: create items? hm, it is better to use view-group, then adapt* on it

    // TODO: let's make it requirement for view-factory to contain view-group...
    //  otherwise it is a weird thing
    // we need a way to reference it and a way to update it (triggering update)
    fun <LP : LayoutParams, H : Item.Holder, I : Item<H>> ViewFactory<LP>.Item(
        item: I
    ): ItemElement<H, I, LP> {
        return ItemElement<H, I, LP>(viewGroup, item).also { add(it) }
    }

    fun hey(context: Context) = ViewFactory.createView(context) {
        VStack {
            Item(PlainItem("P", Colors.black, "The title"))
        }.layoutFill()
    }
}