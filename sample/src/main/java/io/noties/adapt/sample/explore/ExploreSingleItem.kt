package io.noties.adapt.sample.explore

import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.reference

// sometimes it is useful to keep the same item in the list,
//  so it can use some internal state that would not be lost,
//  as otherwise local state need to be persisted in a view (which is actually
//  another reliable way of doing it, maybe just use holder or ref to save it?)
object ExploreSingleItem {

    class Cache {
        fun get(id: String): String? = id as? String
        fun set(id: String, value: String) = Unit
    }

    class MyItemWithCache(
        val id: String,
        cache: Cache
    ): ElementItem<MyItemWithCache.Ref>(0L, { Ref(cache) }) {

        // ref persists item creation, so any state needs to be persisted here
        class Ref(val cache: Cache) {
            lateinit var textView: TextView
        }

        override fun bind(holder: Holder<Ref>) {
            with(holder.ref) {
                textView.text = cache.get(id)
            }
        }

        override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
            Text()
                .reference(ref::textView)
        }
    }
}