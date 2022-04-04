package io.noties.adapt.sample.samples.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import io.noties.adapt.Item
import io.noties.adapt.wrapper.ItemWrapper
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample

@AdaptSample(
    id = "20210122143214",
    title = "ViewPager2",
    description = "AdaptRecyclerView adapter used in ViewPager2 with special " +
            "<tt>ItemWrapper</tt> that modifies existing items to <tt>match_parent</tt> them",
    tags = ["recyclerview", "viewpager2"]
)
class ViewPager2Sample : SampleView() {

    override val layoutResId: Int
        get() = R.layout.view_sample_viewpager2

    override fun render(view: View) {
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager2)
        val adapt = AdaptRecyclerView.create()
        viewPager.adapter = adapt.adapter()

        initSampleItems(
            adapt,
            processItem = {
                if (it !is MatchParentWrapper) {
                    MatchParentWrapper(it)
                } else {
                    it
                }
            }
        )
    }

    class MatchParentWrapper(item: Item<*>) : ItemWrapper(item) {
        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
            return super.createHolder(inflater, parent).also {
                val view = it.itemView()
                view.layoutParams = view.layoutParams.apply {
                    width = MATCH_PARENT
                    height = MATCH_PARENT
                }
            }
        }

        companion object {
            const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }
}