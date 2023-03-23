package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.PreviewLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.imageScaleType
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.foreground
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230220160509",
    title = "AdaptUI, clip views",
    description = "Create a circle avatar view with shape clipping",
    tags = ["shape", "clip"]
)
class AdaptUIClipSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {
        ViewFactory.addChildren(view as ViewGroup) {

            HStack {

                CircleAvatar()

                // CircleAvatar does not return ViewElement, but we can still inspect it
                inspectElements().last()
                    .let {
                        @Suppress("UNCHECKED_CAST")
                        it as ViewElement<*, LinearLayout.LayoutParams>
                    }
                    .layout(0, FILL, 1F)

                Image(R.drawable.ic_shuffle_24_white)
                    .imageScaleType(ImageView.ScaleType.FIT_CENTER)
                    .layout(0, FILL, 1F)

            }.layout(FILL, 256)
        }
    }

    @Suppress("FunctionName")
    private fun ViewFactory<LayoutParams>.CircleAvatar() {
        // base shape, we add a stroke on top, so use the same base shape
        fun circle() = Circle {
            size(128, 128, Gravity.center)
        }

        // the key is clipToOutline + proper background shape
        Image(R.drawable.ic_shuffle_24_white, ImageView.ScaleType.FIT_CENTER)
            .layout(256, 256)
            .background(circle().copy {
                fill(Colors.white)
            })
            // clip view to shape
            .clipToOutline()
            // elevate
            .elevation(8)
            // add stroke
            .ifAvailable(Build.VERSION_CODES.M) {
                it.foreground(circle().copy {
                    stroke(Colors.orange, 2, 8, 2)
                    // half the stroke width
                    padding(1)
                })
            }
    }
}

@Suppress("ClassName", "unused")
private class __AdaptUIClipSample(context: Context, attributeSet: AttributeSet?) :
    PreviewLayout(context, attributeSet) {
    init {
        AdaptUIClipSample().render(this)
    }
}