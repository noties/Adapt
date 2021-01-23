package io.noties.adapt.sample.samples

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.ControlItem
import io.noties.adapt.sample.items.PageIndicatorItem
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.adapt.viewgroup.TransitionChangeHandler

@AdaptSample(
    "20210122221658",
    "Mirrored in ViewPager2",
    "<tt>ListView</tt>, <tt>RecyclerView</td> and " +
            "<tt>LinearLayout inside ScrollView</tt> are initialized to duplicate content (same items)",
    ["recyclerview", "listview", "viewgroup", "viewpager2"]
)
class MirroredSample : SampleView() {

    override val layoutResId: Int
        get() = R.layout.view_sample_viewpager2

    override fun render(view: View) {
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager2)

        // pass by reference, no copying, keeps initial items
        val commonItems: MutableList<Item<*>> = mutableListOf()

        val pageItems = listOf(
            RecyclerViewItem(commonItems),
            ListViewItem(commonItems),
            ViewGroupItem(commonItems),
        )

        // adapt that controls ViewPager2 and uses PageItems
        val pageAdapt = AdaptRecyclerView.create()
        pageAdapt.setItems(pageItems)

        fun updateItems(process: (List<Item<*>>) -> List<Item<*>>) {
            val items = process(pageItems.first().items)
            pageItems.forEach { it.items = items }
            pageAdapt.notifyAllItemsChanged()
        }

        fun onAdd() = updateItems(ControlItem::addedItems)
        fun onShuffle() = updateItems(ControlItem::shuffledItems)

        commonItems.addAll(initialItems(::onAdd, ::onShuffle))

        viewPager.adapter = pageAdapt.adapter()

        initPageIndicator(viewPager, pageAdapt, view)
    }

    fun initPageIndicator(viewPager2: ViewPager2, adapt: Adapt, view: View) {

        val container: ViewGroup = view.findViewById(R.id.indicator)
        val indicatorAdapt = AdaptViewGroup.init(container)

        fun onClick(item: PageIndicatorItem) {

            val index = indicatorAdapt.items().indexOf(item).takeIf { it > -1 }
                ?: throw IllegalStateException("Item not found, item: $item, items: ${indicatorAdapt.items()}")

            @Suppress("UNCHECKED_CAST")
            (indicatorAdapt.items() as List<PageIndicatorItem>)
                .forEach { it.selected = it == item }
            indicatorAdapt.notifyAllItemsChanged()

            viewPager2.setCurrentItem(index, true)
        }

        val items = adapt.items()
            .filterIsInstance<PageItem>()
            .map { PageIndicatorItem(it.title, false, ::onClick) }

        if (items.size != adapt.items().size) {
            throw IllegalStateException("All page items must implement PageItem")
        }

        // select first by default
        items.first().selected = true

        indicatorAdapt.setItems(items)

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                @Suppress("UNCHECKED_CAST")
                (indicatorAdapt.items() as List<PageIndicatorItem>)
                    .withIndex()
                    .forEach {
                        it.value.selected = it.index == position
                    }
                indicatorAdapt.notifyAllItemsChanged()
            }
        })
    }

    private interface PageItem {
        val title: String
        var items: List<Item<*>>
    }

    private class RecyclerViewItem(override var items: List<Item<*>>) :
        Item<RecyclerViewItem.Holder>(1L), PageItem {

        class Holder(view: View) : Item.Holder(view) {

            val recyclerView: RecyclerView = requireView(R.id.recycler_view)
            val adapt: Adapt

            init {
                recyclerView.layoutManager = LinearLayoutManager(view.context)
                adapt = AdaptRecyclerView.init(recyclerView)
            }
        }

        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
            return Holder(inflater.inflate(R.layout.view_sample_recycler_view, parent, false))
        }

        override fun bind(holder: Holder) {
            holder.adapt.setItems(items)
        }

        override val title: String
            get() = "RecyclerView"
    }

    private class ListViewItem(override var items: List<Item<*>>) : Item<ListViewItem.Holder>(2L),
        PageItem {
        class Holder(view: View) : Item.Holder(view) {
            val listView: ListView = requireView(R.id.list_view)
            val adapt: Adapt = AdaptListView.init(listView)
        }

        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
            return Holder(inflater.inflate(R.layout.view_sample_list_view, parent, false))
        }

        override fun bind(holder: Holder) {
            holder.adapt.setItems(items)
        }

        override val title: String
            get() = "ListView"
    }

    private class ViewGroupItem(override var items: List<Item<*>>) : Item<ViewGroupItem.Holder>(3L),
        PageItem {

        class Holder(view: View) : Item.Holder(view) {
            val viewGroup: ViewGroup = requireView(R.id.view_group)
            val adapt: Adapt = AdaptViewGroup.init(viewGroup) {
                it.changeHandler(TransitionChangeHandler.create())
            }
        }

        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
            return Holder(inflater.inflate(R.layout.view_sample_view_group, parent, false))
        }

        override fun bind(holder: Holder) {
            holder.adapt.setItems(items)
        }

        override val title: String
            get() = "ViewGroup"
    }
}