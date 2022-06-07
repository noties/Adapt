package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.dip
import io.noties.adapt.ui.AnyViewElement
import io.noties.adapt.ui.AnyViewFactory
import io.noties.adapt.ui.FILL
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.WRAP
import io.noties.adapt.ui.addChildren
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipChildren
import io.noties.adapt.ui.clipToPadding
import io.noties.adapt.ui.element.HScroll
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Spacer
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.fillViewPort
import io.noties.adapt.ui.element.textAllCaps
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.gradient.GradientEdge
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.item.ElementItemNoRef
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.overScrollMode
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.scrollBarStyle
import io.noties.adapt.ui.shape.Asset
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Corners
import io.noties.adapt.ui.shape.Oval
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.StatefulShape
import io.noties.adapt.ui.util.ColorStateListBuilder
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.debug.Debug
import java.util.Date

@AdaptSample(
    id = "20220523200713",
    "Adapt-UI sample",
    "usage of <tt>adapt-ui</tt> module to build simple layouts in Kotlin",
    tags = ["adapt-ui"]
)
class AdaptUISample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_frame

    // as always adapt-ui items are also available in all contexts:
    //  - recycler-view
    //  - view-group
    //  - list-view
    //  - view
    override fun render(view: View) {
        val viewGroup = view.findViewById<ViewGroup>(R.id.frame_layout)

        // let's build main view with adapt-ui also

        // RecyclerView via CustomView
//        ViewFactory.addChildren(viewGroup) {
//            CustomView(::RecyclerView) {
//                layoutManager = LinearLayoutManager(context)
//                setHasFixedSize(true)
//            }
//            .layout(FILL, FILL)
//            .onView {
//                bindAdapt(AdaptRecyclerView.init(this))
//            }
//        }

        // ScrollView + LinearLayout (VScroll + VStack)
        // @formatter:off
        ViewFactory.addChildren(viewGroup) {
            VScroll {
                VStack { /*no op*/ }
                    .layout(FILL, WRAP)
                    .onView {
                        bindAdapt(AdaptViewGroup.init(this))
                    }
            }
            .layout(FILL, FILL)
        }

        // TODO: color overload - allow hex string?
    }

    // @formatter:off
//    private fun fillTest(viewGroup: ViewGroup) {
//
//        ViewFactory.addChildren(viewGroup) {
//            VScroll {
//                VStack {
//
//                    Spacer()
//                        .layout(0, 0)
//                        .minimumSize(height = 24)
//
//                    repeat(1) {
//                        Text("In the middle")
//                            .padding(24)
//                            .margin(16)
//                            .background(RoundedRectangle(8) {
//                                fill(Color.GREEN)
//                            })
//                    }
//
//                    Spacer()
//                        .layout(0, 0)
//                        .minimumSize(height = 24)
//
//                    Text("Button, okay?")
//                        .textSize(16)
//                        .textColor(Color.WHITE)
//                        .padding(16, 8)
//                        .textGravity(Gravity.CENTER)
//                        .margin(16)
//                        .background(RoundedRectangle(8) {
//                            fill(Color.RED)
//                        })
//                        .onClick {  }
//                        .myCustomStyle()
//                }
//            }
//            .fillViewPort(true)
//        }
//    }
    // @formatter:on

    private fun AnyViewElement.myCustomStyle() = this
        .scrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY)
        .overScrollMode(View.OVER_SCROLL_NEVER)
        .padding(101)

    private fun bindAdapt(adapt: Adapt) {
        // add different item, not ui one
        val items = listOf<Item<*>>(
            StaticTextItem(),
            TextItem("Created: ${Date()}"),
            LineItem("title1", "value1"),
            ButtonItem(),
            ShapeItem(),
            ElevatedShapeItem(),
            CardItem()
        )

        adapt.setItems(items)
    }

    private class StaticTextItem : ElementItemNoRef(0L) {
        // as `text` is static we use it directly here
        override fun ViewFactory<LayoutParams>.body() {
            Text("This is static text\nthat never changes")
                .textGravity(Gravity.CENTER)
                .textColor(Color.BLACK)
                // 24 is SP, not pixels
                .textSize(24)
                // 48 is Dp here
                .padding(48)
                .layout(FILL, 256)
                // Note that shape has distinct padding from the view
                .background(Corners(leadingTop = 32) {
                    padding(16)

                    fill(
                        LinearGradient(
                            GradientEdge.LeadingTop to GradientEdge.BottomTrailing,
                            Color.YELLOW,
                            Color.RED
                        )
                    )

                    // add copy of self to stroke with padding
                    add(copy()) {
                        fill(null)
                        stroke(
                            LinearGradient(
                                GradientEdge.Top to GradientEdge.Bottom,
                                Color.MAGENTA,
                                Color.BLUE
                            ),
                            4,
                            16
                        )
                        padding(2)
                    }

                    add(Asset(context.getDrawable(R.drawable.ic_search_24)!!)) {
                        gravity(Gravity.BOTTOM or Gravity.END)
                        translate(-8, -8)
                        alpha(0.5F)
                        size(48, 48)
                    }
                })
                .elevation(12)
        }
    }

    private class TextItem(
        val text: String
    ) : ElementItem<TextItem.References>(hash(text), ::References) {

        class References {
            lateinit var textView: TextView
            var textViewNullable: TextView? = null
        }

        private class LabelView(context: Context): TextView(context)
        private fun <LP: LayoutParams> ViewFactory<LP>.Label(): ViewElement<LabelView, LayoutParams> = ViewElement<LabelView, LayoutParams> { LabelView(it) }.also(elements::add)

        override fun ViewFactory<LayoutParams>.body(references: References) {
            VStack {
                View()
                    // 48 is already dp
                    .layout(FILL, 48)
                    .background(Color.MAGENTA)
                Label()
                    .textSize(17)
                    .textFont(null, Typeface.BOLD)
                    .reference(references::textView)
                    .reference(references::textViewNullable)
                    .padding(8)
            }
        }

        override fun bind(holder: Holder<References>) {
            with(holder.references) {
                textView.text = text
            }
        }
    }

    private class LineItem(val title: String, val value: String) :
        ElementItem<LineItem.References>(hash(title, value), ::References) {
        class References {
            lateinit var titleView: TextView
            lateinit var valueView: TextView
        }

        // @formatter:off
        override fun ViewFactory<LayoutParams>.body(references: References) {
            VStack {
                test()

                test2()

                paragraph()
                    .layout(FILL, WRAP)
                    .background(Color.RED)
                    .margin(8)

                HStack {
                    Text()
                        .reference(references::titleView)
                    Spacer()
                    View()
                        .layout(1, 16)
                        .background(Color.GREEN)
                    Spacer()
                    Text()
                        .reference(references::valueView)
                }

                HScroll {
                    HStack {
                        square(Color.GRAY)
                        square(Color.BLACK)
                        square(Color.YELLOW)
                        square(Color.MAGENTA)
                    }.layout(WRAP, WRAP)
                }
                .fillViewPort(true)
                .overScrollMode(View.OVER_SCROLL_ALWAYS)

                paragraph()
            }
        }
        // @formatter:on

        private fun ViewFactory<LinearLayout.LayoutParams>.square(color: Int) {
            View()
                .layout(128, 128)
                .background(color)
                .margin(horizontal = 2)
        }

        // this would reduce layoutParams to ViewGroups, so only basic layout(width, height)
        //  would be configurable
        private fun AnyViewFactory.test() =
            Text("TEST")
                .textSize(12)
                .textColor(Color.WHITE)
                .padding(16)
                .background(Color.RED)

        // this does not return anything, it is valid, but no further customization
        //  would be available
        private fun AnyViewFactory.test2() {
            Text("WHATEVER")
                .textSize(12)
                .textColor(Color.RED)
                .textGravity(Gravity.END)
                .padding(12)
                .background(Color.BLACK)
        }

        // this would allow configuring layout params of ViewGroup,
        //  for example, layoutWeight if used inside LinearLayout
        private fun <LP : LayoutParams> ViewFactory<LP>.paragraph() =
            Text("***")
                .textGravity(Gravity.CENTER)
                .textSize(48)
                .padding(24)
                .textColor(Color.GRAY)
                .background(Color.CYAN)

        override fun bind(holder: Holder<References>) {
            with(holder.references) {
                titleView.text = title
                valueView.text = value
            }
        }
    }

    private class ButtonItem : ElementItemNoRef(0L) {
        override fun ViewFactory<LayoutParams>.body() {
            ZStack {
                Text("This is button")
                    .textColor(Color.WHITE)
                    .textGravity(Gravity.CENTER)
                    .textFont(fontStyle = Typeface.BOLD)
                    .textSize(16)
                    .padding(horizontal = 16, vertical = 8)
                    .background(background)
                    .margin(16)
                    .onClick {
                        Debug.i("Clicked!")
                    }
            }
        }

        private val background = StatefulShape.drawable {
            val base = Capsule {
                fill(Color.BLUE)
            }
            setPressed(base.copy {
                alpha(0.45F)
                stroke(Color.YELLOW, 4)
            })
            setDefault(base)
        }
    }

    private class ShapeItem : ElementItemNoRef(0L) {
        override fun ViewFactory<LayoutParams>.body() {
            View()
                .background(shape)
                .layout(FILL, 128)
        }

        private val shape: Shape
            get() = Oval {

                fill(Color.CYAN)
                padding(8)

                add(RoundedRectangle(8)) {
                    fill(Color.BLACK)
                    stroke(Color.YELLOW, 8, 8, 2)
                    size(100, 48, Gravity.END or Gravity.BOTTOM)
                }

                add(Rectangle()) {
                    fill(Color.WHITE)
                    stroke(Color.GRAY, 1, 8, 2)
                    size(64, 64, Gravity.START or Gravity.CENTER_VERTICAL)

                    add(Circle()) {
                        fill(Color.RED)
                        // would still be circle -> additionally moved to be centered (inside own bounds!)
                        // if gravity is specified with `size`, then gravity is applied inside parent bounds
                        size(32, 32, Gravity.BOTTOM or Gravity.END)
                        padding(2)

                        add(Circle()) {
                            fill(Color.GREEN)
                            size(16, 16, Gravity.START or Gravity.TOP)
                            padding(4)
                        }
                    }
                }
            }
    }

    // root shape of drawable automatically reports to outline provider,
    //  making it possible to add shadows around defined shape
    private class ElevatedShapeItem : ElementItemNoRef(0L) {
        // @formatter:off
        override fun ViewFactory<LayoutParams>.body() {
            ZStack {
                VStack {
                    Text("First line")
                        .textSize(24)
                        .textFont(fontStyle = Typeface.BOLD)
                    Text("Second line")
                        .textSize(16)
                }
                .background(background)
                .elevation(4)
                .padding(16)
                .onClick { }
            }
            .clipToPadding(false)
            .clipChildren(false)
            .padding(16)
            .background(0x10000000)
        }
        // @formatter:on

        private val background = StatefulShape.drawable {
            val base = Capsule {
                fill(Color.WHITE)
                stroke(0xFFeeeeee.toInt(), 1)
                size(128, 48)
                translate(48, 48)
            }

            setPressed(base.copy {
                alpha(0.82F)
                stroke(Color.LTGRAY, 1, 8, 2)
            })

            setDefault(base)
        }
    }

    private class CardItem : ElementItemNoRef(0L) {
        // @formatter:off
        override fun ViewFactory<LayoutParams>.body() {
            ZStack {
                HStack {

                    View()
                        .background(iconShape)
                        .layout(64, 64)

                    VStack {
                        Text("The title")
                            .textSize(21)
                            .textFont(fontStyle = Typeface.BOLD)
                            .textColor(Color.BLACK)
                        Text("350")
                            .textSize(16)
                            .textColor(Color.GRAY)
                            .margin(top = 8)
                    }
                    .layout(0, WRAP)
                    .layoutWeight(1F)
                    .margin(leading = 8)

                    Text("Start")
                        .textAllCaps()
                        .textColor(ColorStateListBuilder.create {
                            setActivated(Color.BLUE)
                            setDefault(Color.WHITE)
                        })
                        .textFont(fontStyle = Typeface.BOLD)
                        .padding(horizontal = 24, vertical = 8)
                        .onView {
                            background = toggleDrawable
                            setOnClickListener {
                                it.isActivated = !it.isActivated
                            }
                        }
                }
                .pressable(RoundedRectangle(12) {
                    stroke(Color.BLACK, 2)
                    fill(Color.WHITE)
                    padding(1)
                })
                .padding(8)
                .padding(bottom = 12)
                .onClick {

                }
            }
            .padding(16, 8)
        }
        // @formatter:on

        private val iconShape: Shape
            get() = Rectangle {

                add(RoundedRectangle(8)) {
                    fill(Color.RED)
                    size(48, 48, Gravity.BOTTOM or Gravity.END)
                }

                add(Circle()) {
                    fill(Color.BLUE)
                    size(48, 48, Gravity.START or Gravity.TOP)
                    alpha(0.82F)
                }

                padding(4)
            }

        private fun <V : View, LP : LayoutParams> ViewElement<V, LP>.pressable(
            shape: Shape
        ): ViewElement<V, LP> = onView {

            val distance = 6

            background = StatefulShape.drawable {
                val base = shape.copy {
                    padding(bottom = distance + (paddingBottom ?: 0))
                }
                setPressed(base)
                setDefault(Rectangle {
                    add(shape.copy()) {
                        size(null, 32, Gravity.BOTTOM)
                        fill(Color.GREEN)
                    }
                    add(base)
                })
            }

            viewTreeObserver.addOnDrawListener {
                translationY = if (background.state.contains(android.R.attr.state_pressed)) {
                    distance.dip.toFloat()
                } else {
                    0F
                }
            }
        }

        private val toggleDrawable
            get() = StatefulShape.drawable {

                val control = Rectangle {
                    size(width = 12)
                    fill(Color.GRAY)
                    stroke(Color.BLACK, 1)
                }

                setActivated(Rectangle {

                    add(Rectangle {
                        padding(1)
                        padding(trailing = 12)
                        fill(Color.GREEN)
                    })
                    add(control.copy {
                        gravity(Gravity.END)
                    })

                    stroke(Color.BLACK, 2)
                })

                setDefault(Rectangle {
                    add(Rectangle()) {
                        padding(1)
                        padding(leading = 12)
                        fill(Color.RED)
                    }
                    add(control.copy())
                    stroke(Color.BLACK, 2)
                })
            }
    }
}