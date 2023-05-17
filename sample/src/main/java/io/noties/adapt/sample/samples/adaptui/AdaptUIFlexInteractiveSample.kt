package io.noties.adapt.sample.samples.adaptui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Switch
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import io.noties.adapt.sample.App
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.children
import io.noties.adapt.sample.util.dip
import io.noties.adapt.sample.util.fromPxToDp
import io.noties.adapt.sample.util.hex
import io.noties.adapt.sample.util.withAlphaComponent
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.activated
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundDefaultSelectable
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.flex.AlignContent
import io.noties.adapt.ui.flex.AlignItems
import io.noties.adapt.ui.flex.AlignSelf
import io.noties.adapt.ui.flex.Flex
import io.noties.adapt.ui.flex.FlexDirection
import io.noties.adapt.ui.flex.FlexWrap
import io.noties.adapt.ui.flex.JustifyContent
import io.noties.adapt.ui.flex.flexAlignContent
import io.noties.adapt.ui.flex.flexAlignItems
import io.noties.adapt.ui.flex.flexDirection
import io.noties.adapt.ui.flex.flexJustifyContent
import io.noties.adapt.ui.flex.flexWrap
import io.noties.adapt.ui.flex.layoutFlexGrow
import io.noties.adapt.ui.flex.layoutFlexShrink
import io.noties.adapt.ui.flex.layoutFlexWrapBefore
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.onViewCheckedChanged
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.shape.Asset
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.shape.Corners
import io.noties.adapt.ui.shape.Oval
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.ShapeDrawable
import io.noties.adapt.ui.shape.StatefulShape
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.shape.reference
import io.noties.adapt.ui.util.ColorStateListBuilder
import io.noties.adapt.ui.util.Gravity
import io.noties.debug.Debug
import kotlin.math.roundToInt

@AdaptSample(
    id = "20221126000447",
    title = "AdaptUI - Flex interactive guide",
    description = "https://www.joshwcomeau.com/css/interactive-guide-to-flexbox/"
)
class AdaptUIFlexInteractiveSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {

        ViewFactory.addChildren(view as ViewGroup) {
            VScroll {
                VStack {

                    listOf(
                        FlexDirection(),
                        FlexAlignment(),
                        FlexCrossAlignment(),
                        FlexAlignSelf(),
                        FlexBasis(),
                        FlexGrow(),
                        FlexGrowInteractive(),
                        FlexShrinkInteractive(),
                        FlexShrink(),
                        FlexWrap(),
                        FlexAlignItemsMultiline(),
                        FlexAlignContentMultiline()
                    )

                }.layout(FILL, WRAP)
                    // more at the bottom, so view can scroll more and dropdown is properly displayed
                    .padding(bottom = 128)


            }.layoutFill()
        }
    }

    private fun <V : View, LP : LayoutParams> ViewElement<V, LP>.renderInTransition(block: (ViewElement<V, LP>) -> Unit) {
        val group = (view as? ViewGroup) ?: (view.parent as ViewGroup)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TransitionManager.endTransitions(group)
        }
        block(this)
        TransitionManager.beginDelayedTransition(group)
        render()
    }

    @Suppress("FunctionName")
    private fun <LP : FlexboxLayout.LayoutParams> ViewFactory<LP>.FlexItem(text: String): ViewElement<TextView, LP> =
        Text(text)
            .textSize(16)
            .textColor(Colors.white)
            .textFont(Typeface.DEFAULT_BOLD)
            .padding(horizontal = 16, vertical = 8)
            .background(RoundedRectangle(8) {
                stroke(Colors.primary, 2)
                padding(1)
                fill(
                    LinearGradient.edges { top to bottom }
                        .setColors(
                            Colors.primary.withAlphaComponent(0.42F),
                            Colors.primary.withAlphaComponent(0F)
                        )
                )
            })

    @Suppress("FunctionName")
    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.InteractiveFlexWithFirstFlexItemActive(): Pair<ViewElement<FlexboxLayout, *>, ViewElement<*, FlexboxLayout.LayoutParams>> {
        lateinit var ffi: ViewElement<*, FlexboxLayout.LayoutParams>
        val flex = InteractiveFlex {
            ffi = it
            it.onView { view ->
                (view.background as ShapeDrawable<*>).also { drawable ->
                    drawable.shape.fill(Colors.yellow)
                    drawable.invalidateSelf()
                }
            }
        }
        return flex to ffi
    }

    private fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.defaultFlexContainerStyle() =
        this
            .flexAlignItems(AlignItems.stretch)
            .padding(horizontal = 12, vertical = 4)
            .background(RoundedRectangle(2) {
                stroke(Colors.black.withAlphaComponent(0.42F), 1, 8)
                padding(horizontal = 8, vertical = 1)
            })

    @Suppress("FunctionName")
    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.InteractiveFlex(
        firstFlexItem: ((ViewElement<*, FlexboxLayout.LayoutParams>) -> Unit)? = null
    ): ViewElement<FlexboxLayout, LP> {
        class Ref {
            lateinit var primaryAxisShape: Shape
        }
        // NB! we can process the returned element, but not parent
        //  which would be more reasonable... can we return proper parent,
        //  but give access to the flexbox layout?o
        lateinit var e: ViewElement<FlexboxLayout, *>
        lateinit var primaryAxis: ShapeDrawable<Ref>

        fun showPrimaryAxis(show: Boolean) {
            Debug.e("show:$show")
            primaryAxis.invalidate {
                it.primaryAxisShape.hidden(!show)
            }
        }

        ZStack {

            Element {
                Switch(it).also { s -> s.switchPadding = 8.dip }
            }.text("Show Primary Axis")
                .padding(bottom = 8)
                .textColor(Colors.black)
                .ifAvailable(Build.VERSION_CODES.M) {
                    it.onView {
                        it.thumbTintList = ColorStateListBuilder.create {
                            set(android.R.attr.state_checked, Colors.white)
                            setDefault(hex("#505a64"))
                        }
                    }
                }
                .onViewCheckedChanged { _, checked ->
                    showPrimaryAxis(checked)
                }
                .layout(WRAP, WRAP)
                .layoutGravity(Gravity.center.trailing)
        }

        // create element now in a mock factory, so we can return reference right away
        //  then, we manually add this element to proper factory
        val firstItem = ViewFactory<FlexboxLayout.LayoutParams>(context).FlexItem("Hello")
            .also { firstFlexItem?.invoke(it) }

        e = Flex {
            add(firstItem)
            FlexItem("the")
            FlexItem("world")
        }.layout(FILL, 128 + 24)
            .defaultFlexContainerStyle()
            // we should have used normal foreground, but it is available from API 23
            .onView {

                val drawable = ShapeDrawable(Ref()) { ref ->
                    Rectangle {
                        hidden(true)
                        fill(Colors.white.withAlphaComponent(0.65F))
                        stroke(Colors.accent.withAlphaComponent(0.65F), 2, 4, 1)
                        gravity(Gravity.center)
                    }.reference(ref::primaryAxisShape)
                }.also { primaryAxis = it }

                it.overlay.add(drawable)

                // overlay does not update bounds
                it.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
                    drawable.setBounds(0, 0, v.width, v.height)
                }
            }
            .onView {
                it.onGlobalLayout {
                    primaryAxis.invalidate {
                        val shape = it.primaryAxisShape
                        if (e.view.isHorizontal) {
                            shape.width = null
                            shape.size(height = 4)
                        } else {
                            shape.height = null
                            shape.size(width = 4)
                        }
                    }
                }
            }

        return e
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexContainer(
        children: ViewFactory<LinearLayout.LayoutParams>.() -> Unit
    ) = VStack(children = children)
        .padding(top = 8, bottom = 32)
        .background(sampleSeparator())

    private fun sampleSeparator() = Rectangle {
        val s = 16
        val drawable = App.shared.getDrawable(R.drawable.ic_asterisk)!!
        val base = Asset(drawable) {
            size(s, s, Gravity.center.bottom)
            tint(Colors.black.withAlphaComponent(0.2F))
        }
        add(base)
        add(base.copy {
            translate(x = -(s + s / 2 + 4))
        })
        add(base.copy {
            translate(x = s + s / 2 + 4)
        })
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexDirection() = FlexContainer {

        SampleTitle("Flex direction")

        val flex = InteractiveFlex()

        FlexDirectionDropDown(flex)
            .layout(FILL, WRAP)
            .padding(4)
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexAlignment() = FlexContainer {

        SampleTitle("Flex alignment")

        val flex = InteractiveFlex()

        HStack {

            FlexDirectionDropDown(flex)
                .layout(0, WRAP, 1F)

            JustifyContentDropDown(flex)
                .layout(0, WRAP, 1F)

        }.padding(horizontal = 2)
            .layoutMargin(top = 4)
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexCrossAlignment() = FlexContainer {
        SampleTitle("Flex cross alignment")
        val flex = InteractiveFlex()
        HStack {
            FlexDirectionDropDown(flex)
                .layout(0, WRAP, 1F)
            JustifyContentDropDown(flex)
                .layout(0, WRAP, 1F)
            AlignItemsDropDown(flex)
                .layout(0, WRAP, 1F)
        }
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexAlignSelf() = FlexContainer {
        SampleTitle("Flex align self")
        val (flex, firstItem) = InteractiveFlexWithFirstFlexItemActive()
        HStack {
            FlexDirectionDropDown(flex)
                .layout(0, WRAP, 1F)
            AlignSelfDropDown(flex, firstItem)
                .layout(0, WRAP, 1F)
        }
    }

    // different impl, width or height must be used instead of flex-basis, depending on primary axis
    //  in original flex flex-basis is used for that (no need to check axis and use width/height depending on it)
    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexBasis() = FlexContainer {
        SampleTitle("Flex basis")
        val (flex, firstItem) = InteractiveFlexWithFirstFlexItemActive()
        HStack {

            FlexDirectionDropDown(flex)
                .layout(0, WRAP, 1F)

            VStack {

                lateinit var valueView: ViewElement<TextView, *>

                HStack {
                    DropDownLabel("flex-basis", true)
                        .layout(0, WRAP, 1F)
                    valueView = Text()
                        .textColor(Colors.yellow)
                        .textSize(16)
                }

                fun update(progress: Int) {
                    valueView.text(progress.toString()).render()
                    if (flex.view.isHorizontal) {
                        firstItem.layout(progress.dip, WRAP).render()
                    } else {
                        firstItem.layout(WRAP, progress.dip).render()
                    }
                }

                // seekbar
                val seek = Element {
                    SeekBar(it).also { sb ->
                        sb.max = 150
                        sb.progressTintList = ColorStateList.valueOf(Colors.yellow)
                        sb.thumbTintList = ColorStateList.valueOf(Colors.yellow)
                        sb.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar?,
                                progress: Int,
                                fromUser: Boolean
                            ) {
                                update(progress)
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
                        })
                    }
                }.layout(FILL, WRAP)
                    .padding(vertical = 12)

                flex.onView {
                    it.onGlobalLayout {
                        update(seek.view.progress)
                    }
                }

            }.layout(0, WRAP, 1F)
                .padding(2)
        }
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexGrow() = FlexContainer {
        SampleTitle("Flex grow")
        val (flex, firstItem) = InteractiveFlexWithFirstFlexItemActive()
        HStack {

            FlexDirectionDropDown(flex)
                .layout(0, WRAP, 1F)

            DropDown(
                "flex-grow",
                listOf("0 (default)" to 0, "1" to 1),
                true
            ) { selected ->
                flex.renderInTransition {
                    firstItem.layoutFlexGrow(selected.second.toFloat()).render()
                }
            }
        }
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexGrowInteractive() = FlexContainer {
        SampleTitle("Flex grow interactive")

        Flex {

            class Ref {
                lateinit var view: View
                lateinit var growIndicator: TextView
                lateinit var minus: View
                lateinit var plus: View
            }

            fun ViewFactory<FlexboxLayout.LayoutParams>.Entry(ref: Ref) = VStack(Gravity.center) {

                fun <LP : LayoutParams> ViewFactory<LP>.TextEntry(isMinus: Boolean) =
                    Text(if (isMinus) "-" else "+")
                        .padding(horizontal = 8)
                        .textFont(Typeface.MONOSPACE)
                        .textColor(Colors.black)
                        .textSize(20)
                        .background(Corners {
                            fill(Colors.primary.withAlphaComponent(0.4F))
                            if (isMinus) {
                                leadingTop = 4
                                bottomLeading = 4
                                padding(trailing = 1)
                            } else {
                                topTrailing = 4
                                trailingBottom = 4
                                padding(leading = 1)
                            }
                        })
                        .ifAvailable(Build.VERSION_CODES.M) {
                            it.foregroundDefaultSelectable()
                        }
                        .clipToOutline()

                Text("flex-grow:").layoutWrap().textColor(Colors.black)
                Text("? / ?")
                    .layoutWrap()
                    .textColor(Colors.black)
                    .reference(ref::growIndicator)
                HStack {
                    TextEntry(true)
                        .reference(ref::minus)
                    TextEntry(false)
                        .reference(ref::plus)
                }.layoutWrap()

            }.layoutFlexGrow(1F) // by default start with one
                .layoutWrap()
                .background(StatefulShape.drawable {
                    val base = RoundedRectangle(4) {
                        padding(2)
                        fill(Colors.primary.withAlphaComponent(0.25F))
                        add(RoundedRectangle(4) {
                            padding(1)
                            stroke(Colors.primary)
                        })
                    }
                    setActivated(base)
                    setDefault(base.copy {
                        alpha(0.5F)
                    })
                })
                .padding(horizontal = 4)
                .reference(ref::view)

            val left = Ref()
            val right = Ref()

            fun update() {
                fun View.flexLP(): FlexboxLayout.LayoutParams =
                    layoutParams as FlexboxLayout.LayoutParams

                @SuppressLint("SetTextI18n")
                fun Ref.updateGrow(own: Float, total: Float) {
                    val ownValue = own.roundToInt()
                    if (ownValue == 0) {
                        view.isActivated = false
                        growIndicator.text = "0"
                    } else {
                        view.isActivated = true
                        growIndicator.text = "$ownValue / ${total.roundToInt()}"
                    }
                }

                fun Ref.setupClicks() {
                    fun inTransition(view: View, value: Float) {
                        if (value >= 0F) {
                            val lp = view.flexLP()
                            val flex = (view.parent as FlexboxLayout)
                            TransitionManager.beginDelayedTransition(flex)
                            lp.flexGrow = value
                            view.requestLayout()
                        }
                    }

                    val lp = view.flexLP()
                    val value = lp.flexGrow.roundToInt()
                    minus.setOnClickListener {
                        inTransition(view, value - 1F)
                        update()
                    }
                    plus.setOnClickListener {
                        inTransition(view, value + 1F)
                        update()
                    }
                }

                val leftGrow = left.view.flexLP().flexGrow
                val rightGrow = right.view.flexLP().flexGrow

                left.updateGrow(leftGrow, leftGrow + rightGrow)
                right.updateGrow(rightGrow, leftGrow + rightGrow)

                left.setupClicks()
                right.setupClicks()
            }

            Entry(left)
            Entry(right)
                .onView { update() }

        }.layout(FILL, 128)
            .defaultFlexContainerStyle()
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexShrinkInteractive() = FlexContainer {

        SampleTitle("Flex shrink interactive")

        // NB! difference from js impl - shrink is not distributed equally, so children are
        //  not shrunk proportionally. They receive the same amount in pixels, but these
        //  values are not distributed according to the flex-basis
        fun <LP : FlexboxLayout.LayoutParams> ViewFactory<LP>.Entry(basis: Int) = VStack {
            Text("flex-basis:$basis")
                .textSize(12)
                .textGravity(Gravity.center.horizontal)
                .textColor(Colors.black)
            Text("Actual size:???")
                .textSize(12)
                .textGravity(Gravity.center.horizontal)
                .textColor(Colors.black)
                .onView { textView ->
                    (textView.parent as View).onGlobalLayout {
                        val dp = it.width.fromPxToDp
                        val text = StringBuilder().apply {
                            append("Actual size:$dp")
                            if (dp != basis) {
                                val reducedBy = ((dp.toFloat() / basis) * 100F).roundToInt()
                                append("\nReduced by:$reducedBy%")
                            }
                        }.toString()
                        textView.text = text
                    }
                }
        }.layout(basis, FILL)
            .background(RoundedRectangle(2) {
                padding(1)
                fill(Colors.primary.withAlphaComponent(0.25F))
                add(RoundedRectangle(2) {
                    padding(1)
                    stroke(Colors.primary)
                })
            })

        val flex = Flex {
            Entry(200)
            // manually specifying shrink value would result in behaviour that seems
            //  must be there by default
//                .layoutFlexShrink(2F)
            Entry(100)
        }.defaultFlexContainerStyle()
            .layout(FILL, 128)

        FlexContainerWidthOption(flex)
            .layout(FILL, WRAP)
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexShrink() = FlexContainer {

        SampleTitle("Flex shrink")

        val height = 32

        val circles = mutableListOf<ViewElement<*, FlexboxLayout.LayoutParams>>()

        val flex = Flex {

            View()
                .layout(height, FILL)
                .background(Oval().fill(Colors.yellow))
                .also { circles.add(it) }
            View()
                .layout(128, FILL)
                .layoutFlexGrow(1F)
                .layoutMargin(horizontal = 4)
                .background(Capsule().stroke(Colors.primary).padding(1))
            View()
                .layout(height, FILL)
                .background(Oval().fill(Colors.yellow))
                .also { circles.add(it) }

        }.flexAlignItems(AlignItems.stretch)
            .background(RoundedRectangle(2) {
                stroke(hex("#40ff0000"))
//                padding(horizontal = 8)
            })
            .layout(FILL, height)

        HStack(Gravity.top) {

            DropDown(
                "flex-shrink",
                listOf("1 (default)" to 1F, "0" to 0F),
                false
            ) { selected ->
                circles.forEach { circle ->
                    circle.layoutFlexShrink(selected.second)
                }
            }.layout(0, WRAP, 1F)

            FlexContainerWidthOption(flex)
                .layout(0, WRAP, 1F)
        }
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexContainerWidthOption(
        flex: ViewElement<FlexboxLayout, *>
    ) = VStack {
        DropDownLabel("Container width", false)
        SeekBar()
            .seekBarTint(Colors.yellow)
            .seekBarValue(1F)
            .seekBarOnChanged {
                val width = ((flex.view.parent as ViewGroup).width * it).roundToInt()
                val lp = flex.view.layoutParams
                lp.width = width
                flex.view.requestLayout()
            }
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexWrap() = FlexContainer {

        SampleTitle("Flex wrap")

        fun <LP : FlexboxLayout.LayoutParams> ViewFactory<LP>.Entry(basis: Int = 100) = VStack {

            Text("flex-basis: $basis")
                .textColor(Colors.black)
            Text("Actual size: ???")
                .textColor(Colors.black)
                .layoutMargin(top = 2)
                .onView { tv ->
                    (tv.parent as View).onGlobalLayout {
                        tv.text = "Actual size: ${it.width.fromPxToDp}"
                    }
                }

        }.layout(basis, WRAP)
            .padding(4)
            .background(RoundedRectangle(2) {
                stroke(Colors.accent.withAlphaComponent(0.42F))
                padding(2)
            })
//            .layoutFlexAlignSelf(AlignItems.STRETCH)
//            .layoutFlexShrink(0F)
//            .layoutFlexGrow(1F)

        val flex = Flex {
            Entry()
            Entry()
            Entry()
        }.defaultFlexContainerStyle()

        HStack(Gravity.top) {

            DropDown(
                "flex-wrap",
                listOf("nowrap (default)" to FlexWrap.nowrap, "wrap" to FlexWrap.wrap)
            ) { selected ->
                flex.renderInTransition {
                    flex.flexWrap(selected.second)
                }
            }
                .layout(0, WRAP, 1F)

            FlexContainerWidthOption(flex)
                .layout(0, WRAP, 1F)
        }
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexAlignItemsMultiline() = FlexContainer {

        SampleTitle("Flex align items multiline")

        val flex = Flex {
            FlexItem("Hello")
                .layout(0, WRAP)
                .layoutFlexGrow(1F)
            FlexItem("the")
                .layout(0, 96)
                .layoutFlexGrow(1F)
            FlexItem("world")
                .layout(0, 96)
                .layoutFlexWrapBefore()
                .layoutFlexGrow(1F)
            FlexItem("!!!")
                .layout(0, WRAP)
                .layoutFlexGrow(1F)
        }.defaultFlexContainerStyle()
            .flexWrap(FlexWrap.wrap)

        AlignItemsDropDown(flex)
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexAlignContentMultiline() = FlexContainer {

        SampleTitle("Flex align content multiline")

        val shape = RoundedRectangle(2) {
            stroke(Colors.primary)
            padding(1)
        }

        val flex = Flex {
            FlexItem("Hello")
                .layout(0, WRAP)
                .layoutFlexGrow(1F)
            FlexItem("the")
                .layout(0, 96)
                .layoutFlexGrow(1F)
            FlexItem("world")
                .layout(0, 96)
                .layoutFlexWrapBefore()
                .layoutFlexGrow(1F)
            FlexItem("!!!")
                .layout(0, WRAP)
                .layoutFlexGrow(1F)
        }.defaultFlexContainerStyle()
            .flexWrap(FlexWrap.wrap)
            .layout(FILL, 96 * 2 + 32)

        HStack {
            AlignItemsDropDown(flex)
                .layout(0, WRAP, 1F)
            DropDown(
                "align-content",
                listOf(
                    "flex-start" to AlignContent.flexStart,
                    "flex-end" to AlignContent.flexEnd,
                    "center" to AlignContent.center,
                    "space-between" to AlignContent.spaceBetween,
                    "space-around" to AlignContent.spaceAround,
                    "stretch" to AlignContent.stretch,
                )
            ) { selected ->
                flex.renderInTransition {
                    flex.flexAlignContent(selected.second)
                }
            }.layout(0, WRAP, 1F)
        }
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.SampleTitle(name: String) =
        Text(name)
            .textSize(24)
            .textColor(Colors.primary)
            .textFont(Typeface.DEFAULT_BOLD)
            .padding(horizontal = 16)
            .padding(top = 24, bottom = 8)

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexDirectionDropDown(
        flex: ViewElement<FlexboxLayout, *>
    ) = DropDown(
        "flex-direction",
        listOf(
            "row" to FlexDirection.row,
            "column" to FlexDirection.column
        )
    ) { selected -> flex.renderInTransition { it.flexDirection(selected.second) } }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.JustifyContentDropDown(
        flex: ViewElement<FlexboxLayout, *>
    ) = DropDown(
        "justify-content",
        listOf(
            "flex-start" to JustifyContent.flexStart,
            "center" to JustifyContent.center,
            "flex-end" to JustifyContent.flexEnd,
            "space-between" to JustifyContent.spaceBetween,
            "space-around" to JustifyContent.spaceAround,
            "space-evenly" to JustifyContent.spaceEvenly
        )
    ) { selected -> flex.renderInTransition { it.flexJustifyContent(selected.second) } }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.AlignItemsDropDown(
        flex: ViewElement<FlexboxLayout, *>
    ) = DropDown(
        "align-items",
        listOf(
            "stretch" to AlignItems.stretch,
            "flex-start" to AlignItems.flexStart,
            "center" to AlignItems.center,
            "flex-end" to AlignItems.flexEnd,
            "baseline" to AlignItems.baseline
        )
    ) { selected -> flex.renderInTransition { it.flexAlignItems(selected.second) } }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.AlignSelfDropDown(
        flex: ViewElement<FlexboxLayout, *>,
        element: ViewElement<*, FlexboxLayout.LayoutParams>
    ) = DropDown(
        "align-self",
        listOf(
            "auto" to AlignSelf.auto,
            "stretch" to AlignSelf.stretch,
            "flex-start" to AlignSelf.flexStart,
            "center" to AlignSelf.center,
            "flex-end" to AlignSelf.flexEnd,
            "baseline" to AlignSelf.baseline
        ),
        true
    ) { selected ->
        flex.renderInTransition {
            val lp = element.view.layoutParams as FlexboxLayout.LayoutParams
            lp.alignSelf = selected.second.value
            element.view.requestLayout()
        }
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.DropDownLabel(
        name: String,
        isActive: Boolean
    ) = Text("$name:")
        .textSize(16)
        .textColor(if (isActive) Colors.yellow else Colors.black)
        .textFont(Typeface.DEFAULT_BOLD)

    @Suppress("FunctionName")
    private fun <T, LP : LayoutParams> ViewFactory<LP>.DropDown(
        name: String,
        values: List<Pair<String, T>>,
        isActive: Boolean = false,
        callbacks: (selected: Pair<String, T>) -> Unit
    ) = VStack {

        DropDownLabel(name, isActive)

        Text()
            .text(values[0].first)
            .textSize(16)
            .textColor(if (isActive) Colors.black else Colors.primary)
            .layoutMargin(top = 4)
            .padding(16)
            .padding(trailing = 16 + 16)
            .background(RoundedRectangle(8) {
                val base = if (isActive) Colors.yellow else Colors.black.withAlphaComponent(0.1F)
                fill(base)
                add(Asset(App.shared.getDrawable(R.drawable.ic_arrow_drop_down_24)!!) {
                    size(16, 16)
                    gravity(Gravity.center.trailing)
                    tint(Colors.accent)
                    translate(x = -8)
                })
            })
            .ifAvailable(Build.VERSION_CODES.M) {
                it.foregroundDefaultSelectable()
            }
            .clipToOutline()
            .also { element ->
                element.onClick {
                    // we need to keep track of the window, and dismiss on touch outside
                    val view = createPopupWindowView(
                        element.view.context,
                        element.view.text.toString(),
                        values
                    ) { selected ->
                        // update selected value
                        element.text(selected.first).render()
                        callbacks(selected)
                    }
                    val window = PopupWindow(
                        view,
                        element.view.width,
                        WRAP
                    )
                    window.isFocusable = true
                    window.elevation = 10.dip.toFloat()
                    window.setBackgroundDrawable(RoundedRectangle(8).fill(Colors.white).newDrawable())
                    window.showAsDropDown(element.view, 0, -element.view.height)
                }
            }
    }.padding(2)

    private fun <T> createPopupWindowView(
        context: Context,
        selected: String,
        values: List<Pair<String, T>>,
        callbacks: (selected: Pair<String, T>) -> Unit
    ): View {
        return ViewFactory.createView(context) {

            lateinit var parent: ViewElement<out ViewGroup, *>
            fun activate(view: View) {
                parent.view.children.forEach {
                    it.isActivated = it == view
                }
            }
            VScroll {
                parent = VStack {
                    for (value in values) {
                        Text(value.first)
                            .padding(16)
                            .textSize(16)
                            .textColor(ColorStateListBuilder.create {
                                setActivated(Colors.orange)
                                setDefault(Colors.black)
                            })
                            .activated(selected == value.first)
                            .backgroundDefaultSelectable()
                            .also { element ->
                                element.onClick {
                                    activate(element.view)
                                    callbacks(value)
                                }
                            }
                    }
                }.layout(FILL, WRAP)
            }
        }
    }

    private companion object {
        val FlexboxLayout.isHorizontal: Boolean get() = flexDirection == com.google.android.flexbox.FlexDirection.ROW

        fun <V : View> V.onGlobalLayout(block: (V) -> Unit) {
            val view = this
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (view.isAttachedToWindow) {
                        block(view)
                    } else {
                        view.viewTreeObserver
                            .takeIf { it.isAlive }
                            ?.removeOnGlobalLayoutListener(this)
                    }
                }
            })
        }
    }
}