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
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.JustifyContent
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
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.gradient.GradientEdge
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.onCheckedChanged
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.onElementView
import io.noties.adapt.ui.onViewPreDraw
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.shape.Asset
import io.noties.adapt.ui.shape.Corners
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.ShapeDrawable
import io.noties.adapt.ui.shape.StatefulShape
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.shape.invalidate
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
                        FlexShrinkInteractive()
                    ).onEach {
                        it.padding(top = 32)
                            .background(Rectangle {
                                add(Rectangle {
                                    size(height = 16)
                                    fill(Colors.accent)
                                })
                            })
                    }

                }.layout(FILL, WRAP)
                    .padding(vertical = 16)


            }.layoutFill()
                .background(Colors.primary)
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
                stroke(Colors.white, 2)
                padding(1)
                fill(
                    LinearGradient(
                        GradientEdge.Top to GradientEdge.Bottom,
                        hex("#20ffffff"),
                        hex("#00ffffff")
                    )
                )
            })

    @Suppress("FunctionName")
    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.InteractiveFlexWithFirstFlexItemActive(): Pair<ViewElement<FlexboxLayout, *>, ViewElement<*, FlexboxLayout.LayoutParams>> {
        lateinit var ffi: ViewElement<*, FlexboxLayout.LayoutParams>
        val flex = InteractiveFlex {
            ffi = it
            it.onView {
                (background as ShapeDrawable<*>).also { drawable ->
                    drawable.shape.fill(Colors.yellow)
                    drawable.invalidateSelf()
                }
            }
        }
        return flex to ffi
    }

    private fun <V : FlexboxLayout, LP : LayoutParams> ViewElement<V, LP>.defaultFlexContainerStyle() =
        this
            .flexAlignItems(AlignItems.STRETCH)
            .padding(horizontal = 12, vertical = 4)
            .background(RoundedRectangle(2) {
                stroke(hex("#40ffffff"))
                padding(horizontal = 8)
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
                primaryAxisShape.hidden(!show)
            }
        }

        ZStack {

            Element {
                Switch(it).also { s -> s.switchPadding = 8.dip }
            }.text("Show Primary Axis")
                .padding(bottom = 8)
                .textColor(Colors.white)
                .ifAvailable(Build.VERSION_CODES.M) {
                    it.onView {
                        thumbTintList = ColorStateListBuilder.create {
                            set(android.R.attr.state_checked, Colors.white)
                            setDefault(hex("#505a64"))
                        }
                    }
                }
                .onCheckedChanged {
                    showPrimaryAxis(it)
                }
                .layout(WRAP, WRAP)
                .layoutGravity(Gravity.center.trailing)
        }

        // create element now in a mock factory, so we can return reference right away
        //  then, we manually add this element to proper factory
        val firstItem = ViewFactory<FlexboxLayout.LayoutParams>(context).FlexItem("Hello")
            .also { firstFlexItem?.invoke(it) }

        e = Flex {
            elements.add(firstItem)
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
                        stroke(Colors.primary.withAlphaComponent(0.65F), 2, 4, 1)
                        gravity(Gravity.center)
                    }.reference(ref::primaryAxisShape)
                }.also { primaryAxis = it }

                overlay.add(drawable)

                // overlay does not update bounds
                addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
                    drawable.setBounds(0, 0, v.width, v.height)
                }
            }
            .onView {
                onGlobalLayout {
//                    Debug.e("it.isHorizontal=${it.isHorizontal}")

                    primaryAxis.invalidate {
                        val shape = primaryAxisShape
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
        .padding(vertical = 8)

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexDirection() = FlexContainer {

        val flex = InteractiveFlex()

        FlexDirectionDropDown(flex)
            .layout(FILL, WRAP)
            .padding(4)
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexAlignment() = FlexContainer {
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
                    onGlobalLayout {
                        update(seek.view.progress)
                    }
                }

            }.layout(0, WRAP, 1F)
                .padding(2)
        }
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexGrow() = FlexContainer {
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
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexGrowInteractive() = Flex {

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
                    .textColor(Colors.white)
                    .textSize(20)
                    .background(Corners {
                        fill(Colors.white.withAlphaComponent(0.4F))
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

            Text("flex-grow:").layoutWrap().textColor(Colors.white)
            Text("? / ?")
                .layoutWrap()
                .textColor(Colors.white)
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
                    fill(Colors.white.withAlphaComponent(0.25F))
                    add(RoundedRectangle(4) {
                        padding(1)
                        stroke(Colors.white.withAlphaComponent(0.5F))
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

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexShrinkInteractive() = VStack {

        // NB! difference from js impl - shrink is not distributed equally, so children are
        //  not shrunk proportionally. They receive the same amount in pixels, but these
        //  values are not distributed according to the flex-basis
        fun <LP : FlexboxLayout.LayoutParams> ViewFactory<LP>.Entry(basis: Int) = VStack {
            Text("flex-basis:$basis")
                .textSize(12)
                .textGravity(Gravity.center.horizontal)
                .textColor(Colors.white)
            Text("Actual size:???")
                .textSize(12)
                .textGravity(Gravity.center.horizontal)
                .textColor(Colors.white)
                .onView {
                    val textView = this
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
                fill(Colors.white.withAlphaComponent(0.25F))
                add(RoundedRectangle(2) {
                    padding(1)
                    stroke(Colors.white.withAlphaComponent(0.5F))
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

        DropDownLabel("Container width", false)

//        Element { ctx ->
//            SeekBar(ctx).also {
//                it.max = 100
//                it.progress = 100
//                it.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
//                    override fun onProgressChanged(
//                        seekBar: SeekBar?,
//                        progress: Int,
//                        fromUser: Boolean
//                    ) {
//                        val width = (flex.view.parent as ViewGroup).width
//                        flex.layout(
//                            (width * (progress / 100F)).roundToInt().fromPxToDp,
//                            128
//                        ).render()
//                    }
//
//                    override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
//                    override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
//                })
//            }
//
//        }.layout(FILL, WRAP)
//            .padding(8)

        SeekBar()
            .seekBarOnChanged {
                val width = (flex.view.parent as ViewGroup).width
                val value = (width * it).roundToInt().fromPxToDp
                Debug.e("progress:$it width:$width value:$value")
                flex.layout(
                    value,
                    128
                ).render()
            }
            .seekBarTint(Colors.yellow)
            .onElementView {
                val el = this
                flex.onViewPreDraw {
                    el.seekBarValue(1F).render()
                }.render()
            }
            .layout(FILL, WRAP)
            .padding(8)
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexShrink() = VStack {
        val flex = Flex {

        }.defaultFlexContainerStyle()
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.FlexDirectionDropDown(
        flex: ViewElement<FlexboxLayout, *>
    ) = DropDown(
        "flex-direction",
        listOf("row" to FlexDirection.ROW, "column" to FlexDirection.COLUMN)
    ) { selected -> flex.renderInTransition { it.flexDirection(selected.second) } }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.JustifyContentDropDown(
        flex: ViewElement<FlexboxLayout, *>
    ) = DropDown(
        "justify-content",
        listOf(
            "flex-start" to JustifyContent.FLEX_START,
            "center" to JustifyContent.CENTER,
            "flex-end" to JustifyContent.FLEX_END,
            "space-between" to JustifyContent.SPACE_BETWEEN,
            "space-around" to JustifyContent.SPACE_AROUND,
            "space-evenly" to JustifyContent.SPACE_EVENLY
        )
    ) { selected -> flex.renderInTransition { it.flexJustifyContent(selected.second) } }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.AlignItemsDropDown(
        flex: ViewElement<FlexboxLayout, *>
    ) = DropDown(
        "align-items",
        listOf(
            "stretch" to AlignItems.STRETCH,
            "flex-start" to AlignItems.FLEX_START,
            "center" to AlignItems.CENTER,
            "flex-end" to AlignItems.FLEX_END,
            "baseline" to AlignItems.BASELINE
        )
    ) { selected -> flex.renderInTransition { it.flexAlignItems(selected.second) } }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.AlignSelfDropDown(
        flex: ViewElement<FlexboxLayout, *>,
        element: ViewElement<*, FlexboxLayout.LayoutParams>
    ) = DropDown(
        "align-self",
        listOf(
            "stretch" to AlignItems.STRETCH,
            "flex-start" to AlignItems.FLEX_START,
            "center" to AlignItems.CENTER,
            "flex-end" to AlignItems.FLEX_END,
            "baseline" to AlignItems.BASELINE
        ),
        true
    ) { selected ->
        flex.renderInTransition {
            val lp = element.view.layoutParams as FlexboxLayout.LayoutParams
            lp.alignSelf = selected.second
            element.view.requestLayout()
        }
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.DropDownLabel(
        name: String,
        isActive: Boolean
    ) = Text("$name:")
        .textSize(16)
        .textColor(if (isActive) Colors.yellow else Colors.white)
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
            .textColor(if (isActive) Colors.black else Colors.white)
            .layoutMargin(top = 4)
            .padding(16)
            .padding(trailing = 16 + 16)
            .background(RoundedRectangle(8) {
                val base = if (isActive) Colors.yellow else Colors.white.withAlphaComponent(0.1F)
                fill(base)
                add(Asset(App.shared.getDrawable(R.drawable.ic_arrow_drop_down_24)!!) {
                    size(16, 16)
                    gravity(Gravity.center.trailing)
                    tint(Colors.white)
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
        val FlexboxLayout.isHorizontal: Boolean get() = flexDirection == FlexDirection.ROW

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