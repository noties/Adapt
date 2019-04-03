package ru.noties.adapt.sample.next

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.noties.adapt.next.*
import ru.noties.adapt.sample.R
import ru.noties.adapt.sample.core.ColorRandom
import ru.noties.adapt.sample.core.ShapeRandom
import ru.noties.debug.AndroidLogDebugOutput
import ru.noties.debug.Debug

class NextActivity : Activity() {

    private var index = 0
    private var update = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)

        Debug.init(AndroidLogDebugOutput(true))

        val recycler = findViewById<RecyclerView>(R.id.recycler_view)
        if (true) {

            val adapt = Adapt.create(AsyncDiffUtilDataSetChanged.create())

            recycler.layoutManager = LinearLayoutManager(this)
            recycler.setHasFixedSize(true)
            recycler.adapter = adapt

            val shapeRandom = ShapeRandom.create()
            val colorRandom = ColorRandom.create()

            val items = listOf<Item<*>>(
                    IntItem(10),
                    CharItem('z'),
                    MyItem(shapeRandom.next(), colorRandom.next(), "0", "Subtitle here")
            )
            adapt.setItems(items)

            val handler = Handler()
            handler.postDelayed({
                (0..10)
                        .map { MyItem(shapeRandom.next(), colorRandom.next(), "$it", "Just some text on top") }
                        .toList()
                        .apply {
                            adapt.setItems(this)
                            adapt.setItems(this)
                            adapt.setItems(this)
                        }

            }, 2000L)

            return
        }
        val parent = findViewById<ViewGroup>(/*R.id.group*/0)

        if (true) {

            val shapeRandom = ShapeRandom.create()
            val colorRandom = ColorRandom.create()
            val items = mutableListOf<MyItem>()
            val adaptViews = mutableListOf<AdaptView<MyItem>>()
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {

                    if (!update) {

                        TransitionManager.beginDelayedTransition(parent)

                        val item = MyItem(
                                shapeRandom.next(),
                                colorRandom.next(),
                                "Item #$index",
                                "Super duper huge big subtitle for item #$index")
                        val view = AdaptView.append(parent, item)
                        adaptViews.add(view)
                        items.add(item)
                    } else {

//                        TransitionManager.beginDelayedTransition(parent)

                        val item = items[index].run {
                            copy(
                                    shapeType = shapeRandom.next(),
                                    color = colorRandom.next(),
                                    subtitle = "${this.subtitle} + updated!")
                        }
                        val view = adaptViews[index]

//                        view.bind(MyItem(shapeRandom.next(), colorRandom.next(), item.title, "${item.subtitle} + updated!"))
                        view.bind(item)
//                        AdaptView.create(
//                                parent.getChildAt(index),
//                                MyItem(shapeRandom.next(), colorRandom.next(), item.title, "${item.subtitle} + updated!")) {
//                            MyItem.Holder(it)
//                        }
                    }

                    if (index == 4 && update) {

                        TransitionManager.beginDelayedTransition(parent)

                        val group = AdaptViewGroup.create(parent)
                        group.setItems(
                                items.map {
                                    it.copy(
                                            shapeRandom.next(),
                                            colorRandom.next(),
                                            it.title,
                                            "${it.subtitle} + uououououou!"
                                    )
                                }
                                        .reversed()
                        )
                        return
                    }

                    index += 1

                    if (index == 5) {
                        index = 0
                        update = true
                    }

                    handler.postDelayed(this, 1000L)
                }
            }, 1000L)

            return
        }

        val steps = arrayListOf<List<Item<*>>>(
                listOf(IntItem(10)),
                listOf(IntItem(10), IntItem(11)),
                listOf(IntItem(11), IntItem(10)),
                listOf(CharItem('a'), CharItem('b'), IntItem(11), IntItem(10)),
                listOf(IntItem(10), CharItem('a'), CharItem('b'), IntItem(11)),
                listOf(CharItem('a'), CharItem('b'), IntItem(11))
        )

        if (true) {

            val adaptViewGroup = AdaptViewGroup.create(parent)
            val handler = Handler()
            val runnable = object : Runnable {
                override fun run() {

                    TransitionManager.beginDelayedTransition(parent)

                    val start = System.currentTimeMillis()
//                    NextDiff.diff(parent, previous, steps[index])
                    adaptViewGroup.setItems(steps[index])
                    val end = System.currentTimeMillis()
                    Debug.i("took: %d ms", end - start);

                    index += 1
                    if (index < steps.size) {
//                        previous.clear()
//                        previous.addAll(steps[index - 1])
                        handler.postDelayed(this, 1000L)
                    }
                }

            }
            handler.postDelayed(runnable, 1000L)
            return
        }

        var previous = mutableListOf<Item<*>>()

        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {

                TransitionManager.beginDelayedTransition(parent)

                val start = System.currentTimeMillis()
                NextDiff.diff(parent, previous, steps[index])
                val end = System.currentTimeMillis()
                Debug.i("took: %d ms", end - start);

                index += 1
                if (index < steps.size) {
                    previous.clear()
                    previous.addAll(steps[index - 1])
                    handler.postDelayed(this, 1000L)
                }
            }
        }

        handler.postDelayed(runnable, 1000L)
    }

    class IntItem(private val value: Int) : Item<IntItem.Holder>(value.toLong()) {

        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
            return Holder(inflater.inflate(R.layout.adapt_int, parent, false))
        }

        override fun render(holder: Holder) {
            holder.textView.text = value.toString()
        }

        override fun recyclerDecoration(): RecyclerView.ItemDecoration? {
            return object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.set(48, 48, 48, 48)
//                    outRect.set(0, 0, 0, 0)
//                    val holder = parent.findContainingViewHolder(view) ?: return
//                    if (recyclerViewType() == holder.itemViewType) {
//                        outRect.set(48, 48, 48, 48)
//                    }
                }
            }
        }

        class Holder(itemView: View) : Item.Holder(itemView) {
            val textView = requireView<TextView>(R.id.text)
        }
    }

    class CharItem(private val value: Char) : Item<CharItem.Holder>(value.toLong()) {

        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
            return Holder(inflater.inflate(R.layout.adapt_char, parent, false))
        }

        override fun render(holder: Holder) {
            holder.textView.text = value.toString()
        }

        class Holder(itemView: View) : Item.Holder(itemView) {
            val textView = requireView<TextView>(R.id.text)
        }
    }
}