package io.noties.adapt.sample.samples.recyclerview

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Item
import io.noties.adapt.Item.Wrapper
import io.noties.adapt.wrapper.ItemWrapper
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.recyclerview.RecyclerViewGridWrapperSample.GridWrapped.Companion.grid
import java.util.concurrent.atomic.AtomicInteger

@AdaptSample(
    id = "20220220144816",
    title = "Recycler Grid Wrapper",
    description = "<tt>Wrapper</tt> that determines a grid <b>span count</b> for an item",
    tags = ["recyclerview", "grid", "wrapper"]
)
class RecyclerViewGridWrapperSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_recycler_view

    override fun render(view: View) {
        val spanCount = 3

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val manager = GridLayoutManager(view.context, spanCount)
            .also(recyclerView::setLayoutManager)
        val adapt = AdaptRecyclerView.init(recyclerView)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = adapt.items()[position]
                val grid = ItemWrapper.findWrapper(item, GridWrapped::class.java)
                // force non-null value (we know that we wrap all items in grid here)
                return grid!!.spanCount
            }
        }

        val id = AtomicInteger()

        // first we need 111
        // then we need 12
        // then we need 3
        initSampleItems(
            adapt,
            processItem = {
                val count = when (id.incrementAndGet() % 6) {
                    5 -> 2
                    0 -> 3
                    else -> 1
                }
                // wrap grid item also to indicate `findWrapped` method would properly
                //  obtain GridWrapper
                if (count == 3) {
                    it.grid(3)
                        .wrap(::NoOpWrapper)
                } else {
                    it.grid(count)
                }
            }
        )
    }

    private open class GridWrapped(val spanCount: Int, item: Item<*>) : ItemWrapper(item) {
        companion object {
            fun create(spanCount: Int): Wrapper = Wrapper {
                GridWrapped(spanCount, it)
            }

            // create extension function
            fun Item<*>.grid(spanCount: Int): Item<*> = wrap(GridWrapped.create(spanCount))
        }

        override fun toString(): String {
            return "GridWrapped{spanCount=$spanCount,${item()}}"
        }
    }

    private class NoOpWrapper(item: Item<*>) : ItemWrapper(item)
}