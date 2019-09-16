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
import io.noties.adapt.ViewState
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.screen.BaseSampleActivity
import io.noties.debug.Debug

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

    // temp
    companion object {
        var items: List<Item<*>>? = null
    }

    private val adapt = Adapt.create(DiffUtilDataSetChanged.create())
    private val generator = ItemGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recycler = findViewById<RecyclerView>(R.id.recycler_view)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)

        recycler.adapter = adapt

        if (savedInstanceState != null) {
            val viewState = savedInstanceState.getBundle("view-state");
            Debug.i(", restore: %s, viewState: %s",
                    ViewState.onRestoreInstanceState(recycler, viewState), viewState)
            adapt.setItems(items)
        } else {
            addMoreItems()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        // hm, until view is detached, its state won't be processed and saved...
        // so, we can obtain current views from recyclerView and process then manually, but...
        //  we must have adapt id to save them...

        val recycler = findViewById<RecyclerView>(R.id.recycler_view)

        for (i in 0 until recycler.childCount) {
            val view = recycler.getChildAt(i)
            val holder = recycler.findContainingViewHolder(view) ?: continue
            val id = adapt.getItemId(holder.adapterPosition)
            ViewState.save(id, view)
        }

        val viewState = ViewState.onSaveInstanceState(recycler)
        Debug.i("viewState: %s", viewState)
        outState?.putBundle("view-state", viewState)
        items = adapt.currentItems
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
