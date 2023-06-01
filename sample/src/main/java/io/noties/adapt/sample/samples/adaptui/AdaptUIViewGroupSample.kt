package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.enabled
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.shape.StatefulShape
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.util.ColorStateListBuilder
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20221009161232",
    title = "AdaptUI - ViewGroup",
    tags = ["adapt-ui", "ui-viewgroup"]
)
class AdaptUIViewGroupSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {
        ViewFactory.addChildren(view as ViewGroup) {
            VStack {

                // ViewGroup can additionally send enabled/activated states to its children
                Enabled()
            }
        }
    }

    @Suppress("FunctionName")
    private fun ViewFactory<LayoutParams>.Enabled() {
        VStack {

            val group = ZStack {
                Text("This is text")
                    .padding(16)
                    .textColor(ColorStateListBuilder.create {
                        setEnabled(Colors.orange)
                        setDefault(Colors.black)
                    })
                    .background(StatefulShape.drawable {
                        setEnabled(RectangleShape {
                            stroke(Colors.orange)
                            padding(1)
                        })
                        setDefault(RectangleShape {
                            stroke(Colors.black)
                            padding(1)
                        })
                    }).layoutMargin(4)
            }.background(StatefulShape.drawable {
                setEnabled(RectangleShape {
                    stroke(Colors.orange)
                    padding(1)
                })
                setDefault(RectangleShape {
                    stroke(Colors.black)
                    padding(1)
                })
            }).enabled(false) // this call does not change state of children

            Text("CLICK ME")
                .padding(horizontal = 16, vertical = 8)
                .textColor(Colors.white)
                .background(StatefulShape.drawable {
                    val base = RectangleShape {
                        fill(Colors.orange)
                    }
                    setPressed(base.copy {
                        alpha(0.45F)
                    })
                    setDefault(base)
                })
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
    override val sampleView: SampleView
        get() = AdaptUIViewGroupSample()
}