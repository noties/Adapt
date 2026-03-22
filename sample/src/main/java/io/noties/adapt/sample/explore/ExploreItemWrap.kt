package io.noties.adapt.sample.explore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.Item
import io.noties.adapt.sample.items.CollectionItem
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layoutFill

object ExploreItemWrap {

    // not good , as only root element will use this factory
    //  and it would not be availabel for children (children could call it, but it would crash? nothing
    //  is closed yet, if this function adds element there, it would be fine, also add extension for factory
    class ItemWrapViewFactory(
        context: Context,
        val view: View
    ) : ViewFactory<LayoutParams>(context) {

        // renders original item content here
        // item, original, wrappedContent
        // only mark position
        fun <LP : LayoutParams, VF : ViewFactory<LP>> VF.content() = Element { view }
    }

    fun <H : Item.Holder, I : Item<H>> I.wrap2(view: ItemWrapViewFactory.() -> Unit): Item<*> {
        val item = this
        // create mock view
        //  return it from content in factory
        // after finished, find it, (replace with adaptview), save it and bind item when bound
        //  actually, let's right away create adapt-view and use it instead of mock view
        return object : Item<Item.Holder>(id()) {
            override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
                val placeholder = View(inflater.context).also {
                    it.layoutParams = ViewGroup.LayoutParams(0, 0)
                }
                val factory = ItemWrapViewFactory(inflater.context, placeholder)
                view(factory)

                // TODO: find placeholder view in the hierarchy adn replace it with item view (persist also the holder)
                val itemHolder = item.createHolder(inflater, parent)

                return itemHolder
            }

            override fun bind(holder: Holder) {
                // assume original was returned
                item.bind(holder as H)
            }
        }
    }

    fun hey(context: Context) {
        val item = CollectionItem(emptyList())
            .wrap2 {
                ZStack {
                    HStack {
                        content()
                        Text("Some other content")
                    }
                }.indent()
                    .layoutFill()
            }
    }
}