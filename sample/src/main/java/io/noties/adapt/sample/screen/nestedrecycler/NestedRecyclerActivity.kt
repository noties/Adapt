package io.noties.adapt.sample.screen.nestedrecycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Adapt
import io.noties.adapt.DiffUtilDataSetChanged
import io.noties.adapt.Item
import io.noties.adapt.ItemGroup
import io.noties.adapt.ItemLayoutWrapper
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.screen.BaseSampleActivity

class NestedRecyclerActivity : BaseSampleActivity() {

    override fun layoutResId() = R.layout.activity_recycler

    override fun title() = "RecyclerView Nested items"

    override fun addMoreItems() {
        val items = adapt.currentItems.toMutableList().apply {
            add(NestedItemGroup(count().toLong(), generator.generate(10).map {
                // wrap in a framelayout that limits size and adds padding
                ItemLayoutWrapper.create(R.layout.item_frame, it)
            }))
        }
        adapt.setItems(items)
    }

    override fun shuffleItems() {
        adapt.setItems(generator.shuffle(adapt.currentItems))
    }

    private val adapt = Adapt.create(DiffUtilDataSetChanged.create())
    private val generator = ItemGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recycler = findViewById<RecyclerView>(R.id.recycler_view)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)

        recycler.adapter = adapt

        addMoreItems()
    }
}

class NestedItemGroup(id: Long, children: List<Item<*>>) : ItemGroup(id, children) {

    override fun createView(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.item_nested_recycler, parent, false)
    }

    override fun initNestedRecyclerView(view: View): RecyclerView {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
        return recyclerView
    }
}
