package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.enabled
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.state.backgroundWithState
import io.noties.adapt.ui.state.textColorWithState
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20221009161232",
    title = "AdaptUI - ViewGroup",
    tags = [Tags.adaptUi]
)
class AdaptUIViewGroupSample : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            // ViewGroup can additionally send enabled/activated states to its children
            Enabled()
        }
    }

    @Suppress("FunctionName")
    private fun ViewFactory<LayoutParams>.Enabled() {
        VStack {

            val group = ZStack {
                Text("This is text")
                    .padding(16)
                    .textColorWithState {
                        enabled = orange
                        default = black
                    }
                    .backgroundWithState {
                        enabled = Rectangle {
                            stroke(color = { orange })
                            padding(1)
                        }
                        default = Rectangle {
                            stroke(color = { black })
                            padding(1)
                        }
                    }
                    .layoutMargin(4)
            }.indent()
                .backgroundWithState {
                    enabled = Rectangle {
                        stroke(color = { orange })
                        padding(1)
                    }
                    default = Rectangle {
                        stroke(color = { black })
                        padding(1)
                    }
                }
                .enabled(false) // this call does not change state of children

            Text("CLICK ME")
                .padding(horizontal = 16, vertical = 8)
                .textColor { white }
                .backgroundWithState {
                    // important to call real constructor, as `Rectangle` is a factory method
                    //  that does return Shape instance, but also by default adds this shape to the
                    val base = Rectangle {
                        fill { orange }
                    }
                    pressed = base.copy { alpha(0.45F) }
                    default = base
                }
                .textGravity(Gravity.center)
                .layoutMargin(top = 8)
                .also {
                    var enabled = false
                    it.onClick {
                        enabled = !enabled
                        // supply the second argument to also apply that state to children
                        group.enabled(enabled, true).render()
                    }
                }
        }.padding(16)
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIViewGroupSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUIViewGroupSample()
}