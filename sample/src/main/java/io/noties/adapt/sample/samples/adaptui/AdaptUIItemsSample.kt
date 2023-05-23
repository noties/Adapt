package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import io.noties.adapt.Item
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.PreviewLayout
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.adaptViewGroup
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.scrollFillViewPort
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.item.ElementItemNoRef
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.setItems
import io.noties.adapt.ui.shape.CircleShape
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.viewgroup.TransitionChangeHandler
import io.noties.adapt.wrapper.OnBindWrapper

@AdaptSample(
    id = "20220926192547",
    title = "AdaptUI, adapt items",
    description = "ElementItem and ElementItemNoRef usage, single-file component",
    tags = ["adapt-ui"]
)
class AdaptUIItemsSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_frame

    override fun render(view: View) {
        // just create a view
        // pass additional argument which would be accessible in the building block
        val child = ViewFactory.createView(view.context) {
            VScroll {

                VStack {
                }.adaptViewGroup(TransitionChangeHandler.create())
                    .setItems(items)
                // or
                // .onAdapt { setItems(items) }

            }.layoutFill()
                .scrollFillViewPort()
        }

        (view as ViewGroup).addView(child)
    }

    private val items: List<Item<*>>
        get() = listOf(
            AdaptUIElementItem("This is element item"),
            AdaptUIElementItemNoRef(1L),
            AdaptUIElementItem("Could this be not a text?"),
            AdaptUIElementItemNoRef(2L),
            AdaptUIElementItem("Naah").wrap(OnBindWrapper.init { holder ->
                holder.itemView().setOnClickListener {
                    val items = holder.adapt().items().shuffled()
                    holder.adapt().setItems(items)
                }
            }),
        )

    // can be used for static layouts that do not change based on arguments passed
    private class AdaptUIElementItemNoRef(id: Long) : ElementItemNoRef(id) {
        override fun ViewFactory<ViewGroup.LayoutParams>.body() {
            View()
                .layout(FILL, 56)
                .background(RectangleShape {

                    val base = CircleShape {
                        fill(Colors.orange)
                        size(8, 8)
                    }

                    add(base.copy {
                        // in the middle of the view
                        translateRelative(0.5F, 0.5F)
                    })

                    add(base.copy {
                        // align to the right bottom corner
                        gravity(Gravity.bottom.trailing)
                        // move 8 dp from that corner
                        translate(-8, -8)
                    })

                    Rectangle {
                        size(24, 12)
                        fill(Colors.black)
                        translate(16, 8)
                    }

                    // this rectangle is container for other children
                    //  but it too can have values specified
                    fill(
                        LinearGradient.edges { top.leading to bottom.trailing }
                            .setColors(
                                ColorUtils.setAlphaComponent(Colors.orange, 80),
                                ColorUtils.setAlphaComponent(Colors.black, 80)
                            )
                    )
                })
        }
    }
}

@Suppress("ClassName", "unused")
class __AdaptUIItemsSample(context: Context, attrs: AttributeSet?) : PreviewLayout(context, attrs) {
    init {
        AdaptUIItemsSample().render(this)
    }
}