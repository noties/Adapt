package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.imageScaleType
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.foreground
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.ArcShape
import io.noties.adapt.ui.shape.CapsuleShape
import io.noties.adapt.ui.shape.CircleShape
import io.noties.adapt.ui.shape.CornersShape
import io.noties.adapt.ui.shape.LabelShape
import io.noties.adapt.ui.shape.OvalShape
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangleShape
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230220160509",
    title = "AdaptUI, clip views",
    description = "Create a circle avatar view with shape clipping",
    tags = ["shape", "clip"]
)
class AdaptUIClipSample : AdaptUISampleView() {

    override fun ViewFactory<LayoutParams>.body() {
        VStack {

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

            // Arc is not supported, Line, Text, Label
            listOf(
                RoundedRectangleShape(24).also {
                    it.padding(bottom = -24)
                },
                ArcShape(0F, 220F),
                CapsuleShape {
                    sizeRelative(1F, 0.35F, Gravity.center)
                },
                CircleShape(),
                CornersShape(24, 24),
                OvalShape(),
                RoundedRectangleShape(12)
            ).windowed(3, 3, true)
                .forEach { shapes ->
                    HStack {
                        for (shape in shapes) {
//                            shape.fill(Colors.black)
                            View()
                                .layout(0, 96, 1F)
                                .layoutMargin(4)
                                .ifAvailable(Build.VERSION_CODES.M) {
                                    it.foreground {
                                        Rectangle {
                                            fill(Colors.orange)

                                            LabelShape(shape::class.java.simpleName)
                                                .also { add(it) }
                                                .textGravity(Gravity.center)
                                                .textSize(16)
                                                .textColor(Colors.white)
                                        }
                                    }
                                }
                                .background(shape)
//                                .onView { it.outlineProvider = ViewOutlineProvider.BACKGROUND }
                                .clipToOutline()
                        }
                    }.padding(8)
                        .background(Colors.black)
                        .layout(FILL, WRAP)
                }

        }.layoutFill()
    }

    @Suppress("FunctionName")
    private fun ViewFactory<LayoutParams>.CircleAvatar() {
        // base shape, we add a stroke on top, so use the same base shape
        fun circle() = CircleShape {
            size(128, 128, Gravity.center)
        }

        // the key is clipToOutline + proper background shape
        Image(R.drawable.ic_shuffle_24_white)
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

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIClipSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIClipSample()
}