package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout
import io.noties.adapt.sample.R
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.samples.adaptui.Colors
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.foreground
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.CircleShape

@AdaptSample(
    id = "20230531162848",
    title = "[Showcase] Circle Avatar",
    description = "",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseCircleAvatar : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            HStack {
                Cell {
                    // unmodified avatar
                    Image(R.drawable.sample_avatar_1)
                        .layoutFill()
                }
                Cell {
                    // Custom view to encapsulate view
                    AvatarView()
                        .elevation(12)
                }
            }

            HStack {
                Cell {
                    // a special view that would cast shadow, as after `clipToOutline`
                    //  shadow would be clipped on original ImageView
                    View()
                        .layoutFill()
                        .background {
                            Circle {
                                shadow(8)
                            }
                        }
                    AvatarView()
                }
            }
        }
    }

    @Suppress("FunctionName")
    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.Cell(
        block: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
    ) = ZStack(block)
        .layout(0, 128, 1F)
        .padding(8)
        .noClip()

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.AvatarView() =
        Image(R.drawable.sample_avatar_1)
            .layoutFill()
            // stroke is in foreground to draw on top of content
            .ifAvailable(Build.VERSION_CODES.M) {
                it.foreground {
                    Circle {
                        stroke(Colors.black, 2)
                        // half the stroke width as padding
                        padding(1)
                    }
                }
            }
            .background(CircleShape())
            .clipToOutline()
}

private class PreviewAdaptUIShowcaseCircleAvatar(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseCircleAvatar()
}
