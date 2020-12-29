package io.noties.adapt.sample.samples

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.Sample

// TODO: fix this, currently fails as items are not using `match_parent`
class ViewPager2Sample : AbsSampleView() {

    override val sample: Sample
        get() = Sample(
            "ViewPager2",
            "AdaptRecyclerView adapter used in ViewPager2",
            ::ViewPager2Sample
        )

    override val layoutResId: Int
        get() = R.layout.view_sample_viewpager2

    override fun render(view: View) {
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager2)
        val adapt = AdaptRecyclerView.create()
        viewPager.adapter = adapt.adapter()

        adapt.setItems(initialItems(adapt))
    }
}