package ru.noties.adapt.sample.screen.grid

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import ru.noties.adapt.Adapt
import ru.noties.adapt.AsyncDiffUtilDataSetChanged
import ru.noties.adapt.DiffUtilDataSetChanged
import ru.noties.adapt.sample.ItemGenerator
import ru.noties.adapt.sample.R
import ru.noties.adapt.sample.screen.BaseSampleActivity

class AdaptGridActivity : BaseSampleActivity() {

    override fun layoutResId() = R.layout.activity_recycler

    override fun title() = "Recycler Grid"

    override fun addMoreItems() {
        val items = adapt.currentItems.toMutableList()
                .apply {
                    addAll(generator.generate(3))
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

        val recycler = findViewById<RecyclerView>(R.id.recycler_view)
        recycler.layoutManager = GridLayoutManager(this, 3)
        recycler.setHasFixedSize(true)

        // by default simple notifyDataSetChanged is called
//        adapt = Adapt.create()

        // but it's possible to use DiffUtil
//        adapt = Adapt.create(DiffUtilDataSetChanged.create(true))

        // or async DiffUtil
        adapt = Adapt.create(AsyncDiffUtilDataSetChanged.create(DiffUtilDataSetChanged.create(true)))

        recycler.adapter = adapt

        addMoreItems()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (isFinishing) {
            overridePendingTransition(R.anim.out_appear, R.anim.out_disappear)
        }
    }
}