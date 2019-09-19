package io.noties.adapt.sample.screen.linear

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.ItemWrapper
import io.noties.adapt.StickyItemDecoration
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.screen.BaseSampleActivity

class AdaptLinearRecyclerActivity : BaseSampleActivity() {

    class StickyWrapperItem<H : Item.Holder>(item: Item<H>) : ItemWrapper<H>(item) {
        override fun recyclerDecoration(recyclerView: RecyclerView): RecyclerView.ItemDecoration? {
            return StickyItemDecoration.create(recyclerView, this)
        }
    }

    override fun layoutResId() = R.layout.activity_recycler

    override fun title() = "RecyclerView"

    override fun addMoreItems() {
        val items = adapt.currentItems.toMutableList().apply {
            val list = generator.generate(3)
                    .map { LinearWrapper(it) }
            // we can add more wrappers here, for example introduce sticky decoration
            // for triangle
            // NB all decor wrappers must go AFTER layout wrappers
//                    .map {
//                        if (it.item() is TriangleItem) {
//                            StickyWrapperItem(it)
//                        } else {
//                            it as Item<*>
//                        }
//                    }

            addAll(list)
        }
        adapt.setItems(items)
    }

    override fun shuffleItems() {
        adapt.setItems(generator.shuffle(adapt.currentItems))
    }

    private lateinit var adapt: Adapt

    private val generator = ItemGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapt = Adapt.create()

        val recycler = findViewById<RecyclerView>(R.id.recycler_view)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)
        recycler.adapter = adapt

        addMoreItems()
    }
}