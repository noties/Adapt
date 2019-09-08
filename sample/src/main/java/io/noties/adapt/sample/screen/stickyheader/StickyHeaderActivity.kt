package io.noties.adapt.sample.screen.stickyheader

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.screen.BaseSampleActivity

class StickyHeaderActivity : BaseSampleActivity() {

    override fun layoutResId() = R.layout.activity_recycler

    override fun title() = "Sticky header"

    override fun addMoreItems() {
        val items = adapt.currentItems.toMutableList().apply {
            // ignore first to show that no header is drawn until the first one is pushed out of screen
            if (headers != 0) {
                // header 3 will have multiple lines
                add(HeaderItem("HEADER $headers ${if (headers == 3) "\n\nyo!" else ""}"))
            }
            headers += 1
            addAll(List(9) {
                TextItem("Item ${count() + it}")
            })
        }
        adapt.setItems(items)
    }

    override fun shuffleItems() {
        adapt.setItems(generator.shuffle(adapt.currentItems))
    }

    private val adapt = Adapt.create()
    private val generator = ItemGenerator() // will be used only to shuffle

    private var headers = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)

//        recyclerView.setPadding(
//                48, 48, 48, 48
//        )
//        recyclerView.clipToPadding = false

        recyclerView.layoutManager = LinearLayoutManager(this)

        // reverse layout is available
//        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        // works with grid-layout-manager also
        // just be sure to set span count of header to fill all grids
//        val manager = GridLayoutManager(this, 3)
//        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//
//            val header = Item.generatedViewType(HeaderItem::class.java)
//
//            override fun getSpanSize(position: Int): Int {
//                return if (adapt.getItemViewType(position) == header) 3 else 1
//            }
//        }
//        recyclerView.layoutManager = manager

        recyclerView.adapter = adapt

        addMoreItems()
    }
}