package io.noties.adapt.sample.samples.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import io.noties.adapt.Item
import io.noties.adapt.ItemWrapper
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.ControlItem

// TODO: fix this, currently fails as items are not using `match_parent`
@AdaptSample(
    id = "20210122143214",
    title = "ViewPager2",
    description = "AdaptRecyclerView adapter used in ViewPager2\n<b>NB!</b> fails due to " +
            "missing <tt>match_parent</tt> see <tt>MirroredSample</tt> instead",
    tags = ["recyclerview", "viewpager2"]
)
class ViewPager2Sample : SampleView() {

    override val layoutResId: Int
        get() = R.layout.view_sample_viewpager2

    override fun render(view: View) {
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager2)
        val adapt = AdaptRecyclerView.create()
        viewPager.adapter = adapt.adapter()

        fun onAdd() = ControlItem.addedItems(adapt.items())
            .map { MatchParentWrapper(it) }
            .run { adapt.setItems(this) }

        fun onShuffle() = ControlItem.shuffledItems(adapt.items())
            .map { MatchParentWrapper(it) }
            .run { adapt.setItems(this) }

        adapt.setItems(initialItems(::onAdd, ::onShuffle).map { MatchParentWrapper(it) })
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