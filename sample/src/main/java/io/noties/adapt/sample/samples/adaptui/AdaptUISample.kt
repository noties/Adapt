@file:Suppress("unused")

package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.cyan
import io.noties.adapt.sample.ui.color.emeraldGreen
import io.noties.adapt.sample.ui.color.gray
import io.noties.adapt.sample.ui.color.green
import io.noties.adapt.sample.ui.color.red
import io.noties.adapt.sample.ui.color.salmonRed
import io.noties.adapt.sample.ui.color.steelBlue
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.sample.ui.color.yellow
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.clipChildren
import io.noties.adapt.ui.clipToPadding
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.HScroll
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Spacer
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.scrollFillViewPort
import io.noties.adapt.ui.element.textAllCaps
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textHideIfEmpty
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.item.ElementItemNoRef
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWeight
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.onViewScrollChanged
import io.noties.adapt.ui.overScrollMode
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.scrollBarStyle
import io.noties.adapt.ui.shape.Asset
import io.noties.adapt.ui.shape.CapsuleShape
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.CircleShape
import io.noties.adapt.ui.shape.CornersShape
import io.noties.adapt.ui.shape.OvalShape
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.state.ShapeStateListFactory
import io.noties.adapt.ui.state.backgroundWithState
import io.noties.adapt.ui.state.textColorWithState
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.debug.Debug
import java.util.Arrays
import java.util.Date

@AdaptSample(
    id = "20220523200713",
    "Adapt-UI sample",
    "usage of <tt>adapt-ui</tt> module to build simple layouts in Kotlin",
    tags = [Tags.adaptUi]
)
class AdaptUISample : SampleViewUI() {
    // as always adapt-ui items are also available in all contexts:
    //  - recycler-view
    //  - view-group
    //  - list-view
    //  - view
    override fun ViewFactory<LayoutParams>.body() {
        VScroll {
            VStack { /*no op*/ }
                .layout(fill, wrap)
                .onView {
                    bindAdapt(AdaptViewGroup.init(it))
                }
//                    .myCustomStyle()
        }.layoutFill()
            .onViewScrollChanged { scrollView, deltaX, deltaY ->
                Debug.i(
                    "onViewScrollChanged deltaX:$deltaX deltaY:$deltaY " +
                            "scrollX:${scrollView.scrollX} scrollY:${scrollView.scrollY}"
                )
            }
    }


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

    private fun ViewElement<*, *>.myCustomStyle() = this
        .overScrollMode { never }
        .scrollBarStyle { outsideOverlay }
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
                .textGravity { center }
                .textColor { black }
                // 24 is SP, not pixels
                .textSize(24)
                // 48 is Dp here
                .padding(48)
                .layout(fill, 256)
                // Note that shape has distinct padding from the view
                .background(CornersShape(leadingTop = 32) {
                    padding(16)

                    fill(
                        LinearGradient.edges { top.leading to bottom.trailing }
                            .setColors(Color.YELLOW, Color.RED)
                    )

                    // add copy of self to stroke with padding
                    add(copy {
                        fill(null)
                        stroke(
                            LinearGradient.edges { top to bottom }
                                .setColors(Color.MAGENTA, Color.BLUE),
                            4,
                            16
                        )
                        padding(2)
                    }.also {
                        Debug.i(it)
                    })

                    Asset(context.getDrawable(R.drawable.ic_search_24)!!) {
                        gravity(Gravity.bottom.trailing)
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
            lateinit var textViewElement: ViewElement<out TextView, *>
        }

        private class LabelView(context: Context) : TextView(context)

        @Suppress("FunctionName")
        private fun <LP : LayoutParams> ViewFactory<LP>.Label(): ViewElement<LabelView, LayoutParams> =
            ViewElement<LabelView, LayoutParams> { LabelView(it) }.also { add(it) }

        override fun ViewFactory<LayoutParams>.body(ref: References) {
            VStack {
                View()
                    // 128 is already dp
                    .layout(fill, 128)
                    .background(RectangleShape {

                        val base = RectangleShape {
                            fill(Color.MAGENTA)
                            sizeRelative(.75F, .75F, gravity = Gravity.trailing.bottom)
                            alpha(0.5F)
                        }

                        val circle = CircleShape {
                            fill(0xFF000000.toInt())
                            sizeRelative(0.25F, 0.25F)
                            gravity(Gravity.leading.top)
                        }

                        add(base.copy {
                            fill(Color.GREEN)
                            add(circle.copy())

                            add(base.copy {
                                fill(Color.BLUE)
                                add(circle.copy {
                                    gravity(Gravity.center)
                                })

                                add(base.copy {
                                    fill(Color.RED)
                                    add(circle.copy {
                                        gravity(Gravity.trailing.bottom)
                                    })
                                })
                            })
                        })
                    })
                Element(::Button)
                    .textSize(17)
                    .textBold()
                    .reference(ref::textView)
                    .reference(ref::textViewNullable)
                    .also { ref.textViewElement = it }
                    .reference(ref::textViewElement)
                    .textHideIfEmpty()
                    .padding(8)
            }.noClip()
        }

        override fun bind(holder: Holder<References>) {
            with(holder.ref) {
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

        override fun ViewFactory<LayoutParams>.body(ref: References) {
            VStack {
                test()
                test2()

                paragraph()
                    .layout(fill, wrap)
                    .backgroundColor(Color.RED)
                    .layoutMargin(8)

                HStack {
                    Text()
                        .reference(ref::titleView)
                    Spacer()
                    View()
                        .layout(1, 16)
                        .backgroundColor(Color.GREEN)
                    Spacer()
                    Text()
                        .reference(ref::valueView)
                }

                HScroll {

                    HStack {
                        square(Color.GRAY)
                        square(Color.BLACK)
                        square(Color.YELLOW)
                        square(Color.MAGENTA)
                    }.layout(wrap, wrap)

                }.scrollFillViewPort()
                    .overScrollMode { always }

                paragraph()
            }
        }

        // it does not return anything, so further modification won't be possible,
        //  but view will be added to layout
        private fun ViewFactory<LinearLayout.LayoutParams>.square(color: Int) {
            View()
                .layout(128, 128)
                .backgroundColor(color)
                .layoutMargin(horizontal = 2)
        }

        // this would reduce layoutParams to ViewGroups, so only basic layout(width, height)
        //  would be configurable
        private fun ViewFactory<*>.test() =
            Text("TEST")
                .textSize(12)
                .textColor(Color.WHITE)
                .padding(16)
                .backgroundColor(Color.RED)

        // this does not return anything, it is valid, but no further customization
        //  would be available
        private fun ViewFactory<*>.test2() {
            Text("WHATEVER")
                .textSize(12)
                .textColor(Color.RED)
                .textGravity(Gravity.trailing)
                .padding(12)
                .backgroundColor(Color.BLACK)
        }

        // this would allow configuring layout params of ViewGroup,
        //  for example, layoutWeight if used inside LinearLayout
        private fun <LP : LayoutParams> ViewFactory<LP>.paragraph() =
            Text("***")
                .textGravity(Gravity.center)
                .textSize(48)
                .padding(24)
                .textColor(Color.GRAY)
                .backgroundColor(Color.CYAN)

        override fun bind(holder: Holder<References>) {
            with(holder.ref) {
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
                    .textGravity(Gravity.center)
                    .textBold()
                    .textSize(16)
                    .padding(horizontal = 16, vertical = 8)
                    .backgroundWithState {
                        val base = CapsuleShape { fill { steelBlue } }
                        pressed = base.copy {
                            alpha(0.45F)
                            stroke(color = { yellow }, width = 4)
                        }
                        default = base
                    }
                    .layoutMargin(16)
                    .onClick {
                        Debug.i("Clicked!")
                    }
            }
        }
    }

    private class ShapeItem : ElementItemNoRef(0L) {
        override fun ViewFactory<LayoutParams>.body() {
            View()
                .background(shape)
                .layout(fill, 128)
        }

        private val shape: Shape
            get() = OvalShape {
                fill { cyan }
                padding(8)

                RoundedRectangle(8) {
                    fill { black }
                    stroke(
                        color = { yellow },
                        width = 8,
                        dashWidth = 8,
                        dashGap = 2
                    )
                    size(100, 48, Gravity.trailing.bottom)
                }

                Rectangle {
                    fill { white }
                    stroke(
                        color = { gray },
                        width = 1,
                        dashWidth = 8,
                        dashGap = 2
                    )
                    size(64, 64, Gravity.leading.center)

                    Circle {
                        fill { salmonRed }
                        // would still be circle -> additionally moved to be centered (inside own bounds!)
                        // if gravity is specified with `size`, then gravity is applied inside parent bounds
                        size(32, 32, Gravity.bottom.trailing)
                        padding(2)

                        Circle {
                            fill { emeraldGreen }
                            size(16, 16, Gravity.trailing.top)
                            padding(4)
                        }
                    }
                }
            }
    }

    // root shape of drawable automatically reports to outline provider,
    //  making it possible to add shadows around defined shape
    private class ElevatedShapeItem : ElementItemNoRef(0L) {
        override fun ViewFactory<LayoutParams>.body() {
            ZStack {

                VStack {
                    Text("First line")
                        .textSize(24)
                        .textBold()
                    Text("Second line")
                        .textSize(16)
                }.indent()
                    .backgroundWithState {
                        val base = CapsuleShape {
                            fill { white }
                            stroke(0xFFeeeeee.toInt(), 1)
                            size(128, 48)
                            translate(48, 48)
                        }
                        pressed = base.copy {
                            alpha(0.82F)
                            stroke(
                                color = { gray },
                                width = 1,
                                dashWidth = 8,
                                dashGap = 2
                            )
                        }
                        default = base
                    }
                    .elevation(4)
                    .padding(16)
                    .onClick { }

            }.clipToPadding(false)
                .clipChildren(false)
                .padding(16)
                .backgroundColor(0x10000000)
        }
    }

    private class CardItem : ElementItemNoRef(0L) {
        override fun ViewFactory<LayoutParams>.body() {
            ZStack {
                HStack {

                    View()
                        .background(iconShape)
                        .layout(64, 64)

                    VStack {
                        Text("The title")
                            .textSize(21)
                            .textBold()
                            .textColor(Color.BLACK)
                        Text("350")
                            .textSize(16)
                            .textColor(Color.GRAY)
                            .layoutMargin(top = 8)
                    }.layout(0, wrap)
                        .layoutWeight(1F)
                        .layoutMargin(leading = 8)

                    Text("Start")
                        .textAllCaps()
                        .textColorWithState {
                            activated = steelBlue
                            default = white
                        }
                        .textBold()
                        .padding(horizontal = 24, vertical = 8)
                        .onView {
                            it.background = toggleDrawable
                            it.setOnClickListener {
                                it.isActivated = !it.isActivated
                            }
                        }
                }.padding(8)
                    .padding(bottom = 12)
                    .pressable()
                    .onClick {
                        Debug.i("pressable clicked!")
                    }
            }.padding(16, 8)
        }

        private val iconShape: Shape
            get() = RectangleShape {
                padding(4)

                RoundedRectangle(8) {
                    fill(Color.RED)
                    size(48, 48, Gravity.trailing.bottom)
                }

                Circle {
                    fill(Color.BLUE)
                    size(48, 48, Gravity.leading.top)
                    alpha(0.82F)
                }
            }

        private fun <V : View, LP : LayoutParams> ViewElement<V, LP>.pressable(
        ): ViewElement<V, LP> = onView { view ->

            val distance = 6

//            view.background = StatefulShape.drawable {
//                val base = shape.copy {
//                    padding(bottom = distance + (padding?.bottom?.resolve(0) ?: 0))
//                }
//                setPressed(base)
//                setDefault(Rectangle {
//                    add(shape.copy {
//                        size(null, 32, Gravity.bottom)
//                        fill(Color.GREEN)
//                    })
//                    add(base)
//                })
//            }
//            view.foreground = object: Drawable() {
//                override fun draw(canvas: Canvas) = Unit
//                override fun setAlpha(alpha: Int) = Unit
//                override fun setColorFilter(colorFilter: ColorFilter?) = Unit
//                override fun getOpacity(): Int = PixelFormat.OPAQUE
//                override fun onStateChange(state: IntArray?): Boolean {
//                    return true
//                }
//                override fun isStateful(): Boolean {
//                    return true
//                }
//            }

            view.isFocusable = true
            view.isFocusableInTouchMode = true

            var previousState = view.drawableState

            view.viewTreeObserver.addOnDrawListener {
                val state = view.drawableState
                if (!Arrays.equals(previousState, state)) {
                    previousState = state
                    val text = state
                        .map { id ->
                            try {
                                view.resources.getResourceName(id)
                            } catch (t: Throwable) {
                                Debug.e(t)
                                id.toString()
                            }
                        }
                        .joinToString(", ")
                    Debug.e("NEW-STATE:$text")
                }
                view.translationY =
                    if (view.drawableState.contains(android.R.attr.state_pressed)) {
                        distance.dip.toFloat()
                    } else {
                        0F
                    }
            }
        }

        private val toggleDrawable: Drawable
            get() = ShapeStateListFactory.build {

                val control = Rectangle {
                    size(width = 12)
                    fill { gray }
                    stroke(color = { black }, width = 1)
                }

                activated = Rectangle {
                    Rectangle {
                        padding(1)
                        padding(trailing = 12)
                        fill { green }
                    }

                    add(control.copy { gravity { trailing } })
                    stroke(color = { black }, width = 2)
                }
                default = Rectangle {
                    Rectangle {
                        padding(1)
                        padding(leading = 12)
                        fill { red }
                    }

                    add(control.copy { gravity { leading } })
                    stroke(color = { black }, width = 2)
                }

            }.stateListDrawable
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUISample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUISample()
}