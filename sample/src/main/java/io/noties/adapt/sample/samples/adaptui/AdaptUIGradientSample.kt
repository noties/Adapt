package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import io.noties.adapt.Item
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.withAlphaComponent
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.adaptView
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSingleLine
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.gradient.GradientEdge
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.gradient.RadialGradient
import io.noties.adapt.ui.gradient.SweepGradient
import io.noties.adapt.ui.item.ElementItemNoRef
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Arc
import io.noties.adapt.ui.shape.Corners
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.ZStackSquare

@AdaptSample(
    id = "20230517155636",
    title = "UI, shape gradients",
    description = "LinearGradient, RadialGradient, SweepGradient",
    tags = ["adapt-ui", "shape", "gradient", "graphics"]
)
class AdaptUIGradientSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            items().windowed(3, 3, true)
                .forEach {
                    HStack {
                        for (item in it) {
                            ZStackSquare { View().adaptView(item) }
                                .layout(0, 0, 1F)
                        }

                        for (i in it.size until 3) {
                            View().layout(0, 0, 1F)
                        }
                    }
                }

        }.layoutFill()
    }

    private fun items(): List<Item<*>> = listOf(
        "LG.edges(t-b)" to Rectangle {
            fill(LinearGradient.edges { top to bottom }.setColors(Colors.orange, Colors.primary))
        },
        "LG.angle(229)" to RoundedRectangle(8) {
            padding(8)
            fill(LinearGradient.angle(229F).setColors(Colors.orange, Colors.primary))
        },
        "LG.edges+positions" to Corners {
            corners(4, 8, 16, 32)
            padding(2)
            val gradient = LinearGradient.edges { leading to trailing }
                .setColors(
                    Colors.black to 0F,
                    Colors.accent to 0.25F,
                    Colors.accent to 0.75F,
                    Colors.orange to 1F
                )
            fill(gradient)
        },
        "LG.stroke" to Rectangle {
            padding(4)
            add(RoundedRectangle(8) {
                padding(4)
                stroke(
                    LinearGradient.edges { top.leading to bottom.trailing }
                        .setColors(Colors.accent, Colors.orange),
                    8
                )

                add(Arc(45F, 270F) {
                    size(48, 48, Gravity.center)
                    fill(LinearGradient.angle(7F).setColors(Colors.accent, Colors.primary))
                })
            })
        },
        "LG.stroke+dash" to Rectangle {
            padding(4)
            add(RoundedRectangle(12) {
                padding(4)
                stroke(
                    LinearGradient.angle(270F).setColors(Colors.black, Colors.orange),
                    8,
                    16,
                    4
                )
            })
        },
        "RG.center" to Rectangle {
            fill(RadialGradient.center()
                .setColors(Colors.accent, Colors.orange))
        },
        "RG.edge" to Rectangle {
            fill(RadialGradient.edge(GradientEdge.top)
                .setColors(Colors.orange, Colors.black))
        },
        "RG.angle" to Rectangle {
            fill(RadialGradient.angle(250F)
                .setColors(Colors.primary, Colors.yellow)
                .setRadiusRelative(0.75F))
        },
        "SG" to Rectangle {
            fill(SweepGradient.center().setColors(Colors.orange, Colors.accent))
        },
        "SG-edge" to Rectangle {
            fill(SweepGradient.edge(GradientEdge.top).setColors(Colors.yellow, Colors.primary))
        },
        "SG-angle" to Rectangle {
            fill(SweepGradient.angle(25F)
                .setColors(Colors.orange, Colors.accent))
        }
    ).map { GradientItem(it.first, it.second) }

    private class GradientItem(
        val name: String,
        val shape: Shape
    ) : ElementItemNoRef(hash(name)) {
        override fun ViewFactory<ViewGroup.LayoutParams>.body() {
            VStack {

                Text(name)
                    .textSize(14)
                    .textColor(Colors.black)
                    .textSingleLine(true)
                    .padding(8)

                View()
                    .layoutFill()
                    .background(shape)

            }.layoutFill()
        }
    }
}

private class PreviewAdaptUIGradientSample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIGradientSample()

}