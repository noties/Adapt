package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.DrawableRes
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.background
import io.noties.adapt.sample.ui.color.backgroundSecondary
import io.noties.adapt.sample.ui.color.emeraldGreen
import io.noties.adapt.sample.ui.color.naplesYellow
import io.noties.adapt.sample.ui.color.salmonRed
import io.noties.adapt.sample.ui.color.steelBlue
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.color.textSecondary
import io.noties.adapt.sample.ui.text.footnote
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.VStackReverseDrawingOrder
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.imageTint
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.shape.Corners
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.sticky.stickyVerticalScrollContainer
import io.noties.adapt.ui.sticky.stickyView
import kotlin.random.Random

@AdaptSample(
    id = "20241211001529",
    title = "Sticky header card",
    tags = [Tags.adaptUi, Tags.sticky]
)
class AdaptUIStickyCardHeaderSample : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        VScroll {
            VStack {
                repeat(5) {
                    Card(R.drawable.ic_code_24, "Hello $it")
                    Card(R.drawable.ic_code_24, "Okay now $it")
                    Card(R.drawable.ic_code_24, "Get busy $it")
                    Card(R.drawable.ic_code_24, "Man $it")
                }
            }.indent()
                .noClip()
        }.indent()
            .layoutFill()
            .stickyVerticalScrollContainer()
            .noClip()
    }

    @Suppress("FunctionName")
    private fun <LP : MarginLayoutParams> ViewFactory<LP>.Card(
        @DrawableRes icon: Int,
        title: String
    ) = VStackReverseDrawingOrder {

        HStack {
            Image(icon)
                .layout(24, 24)
                .imageTint { text }
            Text(title)
                .textSize { footnote }
                .textColor { text }
                .layoutMargin(leading = 8)
        }.indent()
            .padding(8)
            .stickyView()
            .background {
                RoundedRectangle(8) {
                    fill { backgroundSecondary }
                }
            }

        View()
            .layout(fill, 96)
            .layoutMargin(16)
            .preview(true) { it.previewBounds() }
            .background {
                Corners {
                    bottomLeading = 8
                    trailingBottom = 8
//
//                    val random = Random(title.hashCode())
//                    val gradient = Gradient.linear {
//                        angle(random.nextInt(0, 359).toFloat())
//                            .setColors(
//                                listOf(steelBlue, naplesYellow, salmonRed, emeraldGreen).shuffled(random)
//                            )
//                    }
//                    fill(gradient)
                }
            }
            .clipToOutline()

    }.indent()
        .layoutMargin(16)
        .background {
            RoundedRectangle(8) {
                fill { backgroundSecondary }
            }
        }
}

@Preview
private class PreviewAdaptUIStickyCardHeaderSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIStickyCardHeaderSample()
}