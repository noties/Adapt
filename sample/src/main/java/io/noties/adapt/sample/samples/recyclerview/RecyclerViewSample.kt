package io.noties.adapt.sample.samples.recyclerview

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.ifCastLayout
import io.noties.adapt.ui.item.ElementItemNoRef
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.util.Gravity
import io.noties.debug.Debug

@AdaptSample(
    id = "20210122143200",
    title = "RecyclerView",
    description = "Usage of multiple items inside <tt><b>RecyclerView</b></tt>",
    tags = ["recyclerview"]
)
class RecyclerViewSample : SampleView() {

    override val layoutResId = R.layout.view_sample_recycler_view

    override fun render(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val adapt = AdaptRecyclerView.init(recyclerView)

        // STOPSHIP:
        if (true) {
            adapt.setItems(listOf(MyItem("HEY!")))
            return
        }

        initSampleItems(adapt)
    }

    private class MyItem(val text: String) : ElementItemNoRef(0L) {
        override fun ViewFactory<ViewGroup.LayoutParams>.body() {
            Text(text)
                .textSize(24)
                .textColor { black }
                .textGravity(Gravity.center)
                .onLayoutParams {
                    Debug.e("lp:$it")
                }
                .ifCastLayout(MarginLayoutParams::class) {
                    Debug.e("MARGINS!")
                    it.layoutMargin(16)
                }
        }
    }
}