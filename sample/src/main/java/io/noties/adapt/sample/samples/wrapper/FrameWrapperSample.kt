package io.noties.adapt.sample.samples.wrapper

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleViewLayout
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.util.dip
import io.noties.adapt.viewgroup.AdaptViewGroup

@AdaptSample(
    "20220404124224",
    "FrameWrapper",
    "Usage of <tt>FrameWrapper</tt> with different values for <tt>width</tt>, <tt>height</tt> and <tt>contentGravity</tt>",
    tags = ["wrapper"]
)
class FrameWrapperSample : SampleViewLayout() {

    override val layoutResId: Int
        get() = R.layout.view_sample_view_group

    override fun render(view: View) {
        // NB! `wrapper` should be used carefully (thus its generally discouraged)
        //  as it modifies Item, and a different one will be rendered by `adapt`,
        //  which might create confusion (as original will be missing, as it is _wrapped_)
        val viewGroup = view.findViewById<ViewGroup>(R.id.view_group)
        val adapt = AdaptViewGroup.init(viewGroup)

        val item = ItemGenerator.next(0).first()

        val items = listOf(
            item.id(1L)
                .frame(100.dip, 100.dip),
            item.id(2L)
                .frame(height = 150.dip)
                .background(0x40FF0000),
            item.id(3L)
                .frame(100.dip, 100.dip)
                .background(0x4000ff00)
                .frame(
                    height = 300.dip,
                    contentGravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                )
                .background(0x400000ff)
        )

        adapt.setItems(items)
    }
}

@Preview
private class PreviewFrameWrapperSample(context: Context, attrs: AttributeSet?) :
    PreviewSampleView(context, attrs) {
    override val sampleView
        get() = FrameWrapperSample()
}