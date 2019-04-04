package ru.noties.adapt.sample.screen.view

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import ru.noties.adapt.Adapt
import ru.noties.adapt.AdaptView
import ru.noties.adapt.sample.ItemGenerator
import ru.noties.adapt.sample.R
import ru.noties.adapt.sample.items.CircleItem
import ru.noties.adapt.sample.screen.BaseSampleActivity

class AdaptViewActivity : BaseSampleActivity() {

    override fun layoutResId() = R.layout.activity_view

    override fun title() = "View"

    override fun addMoreItems() {
        val items = adapt.items.toMutableList().apply {
            addAll(generator.generate(3))
        }
        adapt.setItems(items).also {
            bindLastCircle()
        }
    }

    override fun shuffleItems() {
        adapt.setItems(generator.shuffle(adapt.items)).also {
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
        adapt.items.lastOrNull { it is CircleItem }?.let { item ->

            // cast
            item as CircleItem

            if (this::adaptView.isInitialized) {
                adaptView.bind(item)
            } else {
                adaptView = AdaptView.append(findViewById(R.id.last_circle_group), item)
            }
        }
    }
}