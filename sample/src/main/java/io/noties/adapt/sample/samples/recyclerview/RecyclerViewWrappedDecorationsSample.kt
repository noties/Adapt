package io.noties.adapt.sample.samples.recyclerview

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Item
import io.noties.adapt.ItemWrapper
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.PlainItem
import io.noties.debug.Debug

@AdaptSample(
    id = "20210326211532",
    title = "Decoration on ItemWrapper",
    description = "Add distinct <tt>ItemDecoration</tt>s to the same " +
            "<tt>Item</tt> type wrapped in different <tt>ItemWrapper</tt>s in <tt>RecyclerView</tt>",
    tags = ["recyclerview", "wrapper", "key"]
)
class RecyclerViewWrappedDecorationsSample : SampleView() {

    override val layoutResId = R.layout.view_sample_recycler_view

    override fun render(view: View) {
        val recyclerView =
            view.findViewById<RecyclerView>(R.id.recycler_view).also { recyclerView ->
                recyclerView.layoutManager = LinearLayoutManager(view.context)

                // register even decoration
                Item.Key.builder().wrapped(EvenItem::class.java).build(PlainItem::class.java)
                    .also { key ->
                        Debug.i("event, short: ${key.toShortString()}, regular: $key")
                        recyclerView.addItemDecoration(
                            DrawColorOverItemDecoration(
                                key.viewType(),
                                0x22FF0000
                            )
                        )
                    }

                // register odd decoration
                Item.Key.builder().wrapped(OddItem::class.java).build(PlainItem::class.java)
                    .also { key ->
                        Debug.i("odd, short: ${key.toShortString()}, regular: $key")
                        recyclerView.addItemDecoration(
                            DrawColorOverItemDecoration(
                                key.viewType(),
                                0x2200FF00
                            )
                        )
                    }
            }

        val adapt = AdaptRecyclerView.init(recyclerView)

        initSampleItems(
            adapt,
            onAddingNewItems = { items ->
                items
                    .withIndex()
                    .map {
                        if (it.value is PlainItem) {
                            if (it.index % 2 == 0) {
                                EvenItem(it.value)
                            } else {
                                OddItem(it.value)
                            }
                        } else {
                            it.value
                        }
                    }
            }
        )
    }

    class EvenItem(item: Item<*>) : ItemWrapper(item)
    class OddItem(item: Item<*>) : ItemWrapper(item)

    class DrawColorOverItemDecoration(
        private val itemViewType: Int,
        @ColorInt color: Int
    ) : RecyclerView.ItemDecoration() {

        val paint = Paint().also {
            it.isAntiAlias = true
            it.style = Paint.Style.FILL
            it.color = color
        }

        val rect = Rect()

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            (0 until parent.childCount)
                .map { parent.getChildAt(it) }
                .mapNotNull { parent.findContainingViewHolder(it) }
                .filter { it.itemViewType == itemViewType }
                .map { it.itemView }
                .forEach { view ->

                    // to be animated by default ItemAnimator during diffing
                    val x = view.translationX.toInt()
                    val y = view.translationY.toInt()

                    rect.set(
                        view.left + x,
                        view.top + y,
                        view.right + x,
                        view.bottom + y
                    )

                    c.drawRect(rect, paint)
                }
        }
    }
}