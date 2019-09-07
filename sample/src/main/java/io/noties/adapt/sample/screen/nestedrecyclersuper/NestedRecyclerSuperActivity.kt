package io.noties.adapt.sample.screen.nestedrecyclersuper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.AdaptViewGroup
import io.noties.adapt.Item
import io.noties.adapt.ItemGroup
import io.noties.adapt.ItemLayoutWrapper
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.screen.BaseSampleActivity

class NestedRecyclerSuperActivity : BaseSampleActivity() {

    override fun layoutResId() = R.layout.activity_nested_super

    override fun title() = "Super nested RecyclerView"

    override fun addMoreItems() {
        val items = group.currentItems.toMutableList().apply {
            // add horizontal items which will have vertical items
            val vertical = List(6) {
                VerticalItemGroup(it.toLong(), generator.generate(6).map { item ->
                    // wrap in frame layout with width/height limited
                    ItemLayoutWrapper.create(R.layout.item_frame, item)
                })
            }
            add(HorizontalItemGroup(recyclerViewPool, count().toLong(), vertical))
        }
        group.setItems(items)
    }

    override fun shuffleItems() {
        group.setItems(generator.shuffle(group.currentItems))
    }

    private lateinit var group: AdaptViewGroup

    //    private val adapt = Adapt.create()
    private val generator = ItemGenerator()
    private val recyclerViewPool = RecyclerView.RecycledViewPool()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // unfortunately the last level (vertical) doesn't work if our root is RecyclerView
        // most likely there is a hack to work-around it... but it should never be so -
        //      a vertical recycler-view inside another vertical recycler-view

        // another thing -> as we no longer inside a recycler view, we must manually set recycler-view-pool
        // currently this pool is reused only in horizontal item, but we want all horizontal items
        // also share it

//        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
//        recyclerView.layoutManager = LinearLayoutManager(this) // VERTICAL
//        recyclerView.setHasFixedSize(true)
//        recyclerView.isNestedScrollingEnabled = false
//
//        recyclerView.adapter = adapt

        group = AdaptViewGroup.create(findViewById(R.id.view_group))

        addMoreItems()
    }
}

// 1-st level - HORIZONTAL
class HorizontalItemGroup(
        private val pool: RecyclerView.RecycledViewPool,
        id: Long,
        children: List<Item<*>>
) : ItemGroup(id, children) {
    override fun createView(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.item_nested_recycler_horizontal, parent, false)
    }

    override fun initNestedRecyclerView(view: View): RecyclerView {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        return recyclerView
    }

    override fun processRecyclerViewPool(parent: ViewGroup, recyclerView: RecyclerView) {
        // default implementation just uses parent view pool if it's a recycler view
        // it is no longer the case in this example, so we manually apply the pool that we'd
        // received in constructor
        //
        // please note that we do not need to do it for vertical item, as it will automatically
        // use this pool also
        recyclerView.setRecycledViewPool(pool)
    }
}

// 2-nd level VERTICAL
class VerticalItemGroup(id: Long, children: List<Item<*>>) : ItemGroup(id, children) {
    override fun createView(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.item_nested_recycler_vertical, parent, false)
    }

    override fun initNestedRecyclerView(view: View): RecyclerView {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        return recyclerView
    }
}