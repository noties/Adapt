package io.noties.adapt.sample.samples.recyclerview

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample

// TODO: fix this, currently fails as items are not using `match_parent`
@AdaptSample(
    id = "20210122143214",
    title = "ViewPager2",
    description = "AdaptRecyclerView adapter used in ViewPager2\n<b>NB!</b> fails due to missing <tt>match_parent</tt>",
    tags = ["recyclerview", "viewpager2"]
)
class ViewPager2Sample : SampleView() {

    override val layoutResId: Int
        get() = R.layout.view_sample_viewpager2

    override fun render(view: View) {
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager2)
        val adapt = AdaptRecyclerView.create()
        viewPager.adapter = adapt.adapter()

        adapt.setItems(initialItems(adapt))
    }
}