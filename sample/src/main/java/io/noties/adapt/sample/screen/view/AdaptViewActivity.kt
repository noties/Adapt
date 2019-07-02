package io.noties.adapt.sample.screen.view

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Adapt
import io.noties.adapt.AdaptView
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.items.CircleItem
import io.noties.adapt.sample.screen.BaseSampleActivity

class AdaptViewActivity : BaseSampleActivity() {

    override fun layoutResId() = R.layout.activity_view

    override fun title() = "View"

    override fun addMoreItems() {
        val items = adapt.currentItems.toMutableList().apply {
            addAll(generator.generate(3))
        }
        adapt.setItems(items).also {
            bindLastCircle()
        }
    }

    override fun shuffleItems() {
        adapt.setItems(generator.shuffle(adapt.currentItems)).also {
            bindLastCircle()
        }
    }

    private val generator = ItemGenerator()

    private lateinit var adapt: Adapt
    private lateinit var adaptView: AdaptView<CircleItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapt = Adapt.create()

        val recycler = findViewById<RecyclerView>(R.id.recycler_view)
        recycler.layoutManager = GridLayoutManager(this, 3)
        recycler.setHasFixedSize(true)
        recycler.adapter = adapt

        addMoreItems()
    }

    private fun bindLastCircle() {

        // for example, if our item wrapped in OnClick wrapper, we still can
        // bind it

        adapt.currentItems.lastOrNull { it is CircleItem }?.let { item ->

            // cast
            item as CircleItem

            if (this::adaptView.isInitialized) {
                adaptView.setItem(item)
            } else {
                adaptView = AdaptView.append(findViewById(R.id.last_circle_group), item)
            }
        }
    }
}