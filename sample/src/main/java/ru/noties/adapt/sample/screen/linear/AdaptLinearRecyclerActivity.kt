package ru.noties.adapt.sample.screen.linear

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import ru.noties.adapt.Adapt
import ru.noties.adapt.sample.ItemGenerator
import ru.noties.adapt.sample.R
import ru.noties.adapt.sample.screen.BaseSampleActivity

class AdaptLinearRecyclerActivity : BaseSampleActivity() {

    override fun layoutResId() = R.layout.activity_recycler

    override fun title() = "RecyclerView"

    override fun addMoreItems() {
        val items = adapt.items.toMutableList().apply {
            val list = generator.generate(3)
                    .map { LinearWrapper(it) }
            addAll(list)
        }
        adapt.setItems(items)
    }

    override fun shuffleItems() {
        adapt.setItems(generator.shuffle(adapt.items))
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