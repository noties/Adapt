package ru.noties.adapt.sample.kotlin

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import ru.noties.adapt.Adapt
import ru.noties.adapt.OnClickViewProcessor
import ru.noties.adapt.sample.R
import ru.noties.adapt.sample.core.ItemGenerator
import ru.noties.adapt.sample.core.item.AppendItem
import ru.noties.adapt.sample.core.item.Item
import ru.noties.adapt.sample.core.item.SectionItem
import ru.noties.adapt.sample.core.item.ShapeItem
import ru.noties.adapt.sample.kotlin.view.AppendView
import ru.noties.adapt.sample.kotlin.view.SectionView
import ru.noties.adapt.sample.kotlin.view.ShapeView

class MainActivity : Activity() {

    private val itemGenerator = ItemGenerator.create()

    private lateinit var adapt: Adapt<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapt = Adapt.builder(Item::class.java)
                .include(SectionItem::class.java, SectionView())
                .include(ShapeItem::class.java, ShapeView())
                .include(
                        AppendItem::class.java,
                        AppendView(),
                        OnClickViewProcessor.create({ _, _ -> append() })
                )
                .build()

        initRecyclerView()

        append()
    }

    private fun initRecyclerView() {

        val shapeViewType = adapt.assignedViewType(ShapeItem::class.java)
        val spanCount = resources.getInteger(R.integer.span_count)
        val padding = resources.getDimensionPixelSize(R.dimen.item_padding)

        val manager = GridLayoutManager(this, spanCount)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (shapeViewType == adapt.itemViewType(position))
                    1
                else
                    spanCount
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = manager

        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                outRect.set(padding, padding, padding, padding)
            }
        })
        recyclerView.clipToPadding = false
        recyclerView.clipChildren = false
        recyclerView.setPadding(padding, padding, padding, padding)

        recyclerView.adapter = adapt.recyclerViewAdapter()
    }

    private fun append() {
        adapt.setItems(itemGenerator.generate(adapt.items))
    }
}