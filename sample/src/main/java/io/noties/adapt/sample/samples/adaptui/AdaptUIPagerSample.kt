package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import io.noties.adapt.sample.App
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.dip
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.addChildren
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Pager
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.ViewPagerLayoutParams
import io.noties.adapt.ui.element.ViewPagerOnPageChangeListener
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.imageScaleType
import io.noties.adapt.ui.element.imageTint
import io.noties.adapt.ui.element.pagerCurrentItem
import io.noties.adapt.ui.element.pagerDecor
import io.noties.adapt.ui.element.pagerOffscreenPageLimit
import io.noties.adapt.ui.element.pagerOnPageChangedListener
import io.noties.adapt.ui.element.pagerOnPageSelectedListener
import io.noties.adapt.ui.element.pagerPageMargin
import io.noties.adapt.ui.element.pagerPageTransformer
import io.noties.adapt.ui.element.pagerPageWidthRatio
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.element.viewPager
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.enabled
import io.noties.adapt.ui.gradient.GradientEdge
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutWeight
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.StatefulShape
import io.noties.adapt.ui.util.Gravity
import io.noties.debug.Debug
import kotlin.math.abs

@AdaptSample(
    id = "20221006102210",
    title = "AdaptUI: Pager",
    description = "Usage of <def>Pager</def> element (ViewPager)",
    tags = ["adapt-ui", "view-pager"]
)
class AdaptUIPagerSample : SampleView() {
    override val layoutResId: Int = R.layout.view_sample_frame

    private val colors = listOf(
        Colors.orange,
        Colors.black
    )

    override fun render(view: View) {
        val container = view.findViewById<ViewGroup>(R.id.frame_layout)

        ViewFactory.addChildren(container) {

            Pager {

                // Decor view
                PagerDecor()

                MyPage("First", colors[0], 0.75F)
                MyPage("Second", colors[1], 0.5F)
                MyPage("Third", colors[0], 0.25F)
                MyPage("Forth", colors[1])

                // each page can additionally register on-page-selected-listener
                //  callbacks would be triggered when page is selected/unselected
                //  there could be multiple deselect callbacks
                //  NB! this listener is registered when view is attached to a viewpager
                Text("PAGE!!!")
                    .textGravity(Gravity.center)
                    .textSize(48)
                    .textColor(Colors.black)
                    .also { element ->
                        element.pagerOnPageSelectedListener {
                            // this page is selected/deselected
                            Debug.i("selected:$it")

                            element.view.clearAnimation()
                            element.view.animate()
                                .translationY(if (it) 0F else -128.dip.toFloat())
                                .setDuration(250L)
                                .start()
                        }
                    }

                // doesn't need to be the `MyPage` (it is just an utility)
                // can add any arbitrary views
                VStack {
                    Image(R.drawable.ic_search_24)
                        .imageTint(Colors.orange)
                        .layout(FILL, 0)
                        .imageScaleType(ImageView.ScaleType.CENTER_CROP)
                        .layoutWeight(3F)
                    Text("This is text")
                        .textGravity(Gravity.center)
                        .textColor(Colors.black)
                        .textSize(24) // already 24sp
                        .layout(FILL, 0)
                        .layoutWeight(1F)
                        .background(RoundedRectangle(9) {
                            fill(Colors.white) // for the shape to cast proper elevation shadow
                            stroke(Colors.black)
                            padding(16)
                        })
                        .elevation(4)
                }

                // as the really important part is actual call inside ViewFactory context
                //  a for-loop, if block all could be used
                (5 until 7)
                    .mapIndexed { index, i -> "list-map Item $i" to colors[index % 2] }
                    .forEach {
                        // here is the call to add an element
                        MyPage(it.first, it.second)
                    }

                // for loop
                for (i in (7 until 9)) {
                    MyPage("for-loop Item $i", colors[i % 2])
                }

                // evaluated at runtime, if false - the block is not triggered
                if (true /*some condition*/) {
                    MyPage("from-if Item", colors[0])
                }

            }.layoutFill()
                .noClip()
                .pagerPageTransformer(transformer = { page, position ->
                    val value = 1F - abs(position)
                    page.alpha = value
                    page.scaleX = value
                    page.pivotX = if (position < 0) {
                        page.width.toFloat()
                    } else {
                        0F
                    }
                })
                .pagerCurrentItem(1)
                .pagerOffscreenPageLimit(3)
                .pagerPageMargin(16, Capsule {
                    fill(
                        LinearGradient(
                            GradientEdge.Top to GradientEdge.Bottom,
                            Colors.orange,
                            Colors.black
                        )
                    )
                    padding(4)
                })
                .pagerOnPageChangedListener(object : ViewPagerOnPageChangeListener() {
                    override fun onPageSelected(position: Int) {
                        // NB! position is 0-based
                        Debug.i("view-pager, page changed (with adapter): ${position + 1} / $pagesCount")
                    }
                })

        }
    }

    // If view element is not returned, no customization could happen
    //  in the calling block. But this is still valid and views would be added
    @Suppress("FunctionName")
    private fun ViewFactory<ViewPagerLayoutParams>.PagerDecor() {

        // we can lateinit all the variables.. it's not pretty, but it is possible
        // persist all elements we want to change when page selection changes
        lateinit var previous: ViewElement<out View, *>
        lateinit var text: ViewElement<out TextView, *>
        lateinit var next: ViewElement<out View, *>

        // persist decor element so we can obtain ViewPager - `decor.viewPager`
        lateinit var decor: ViewElement<out View, ViewPagerLayoutParams>

        decor = HStack(Gravity.center) {

            previous = Text("<<")
                .padding(16)
                .background(decorViewButtonBackground(Color.MAGENTA))
                .layout(WRAP, FILL)
                .onClick {
                    val vp = decor.viewPager
                    vp.currentItem = vp.currentItem - 1
                }

            text = Text()
                .layout(0, WRAP)
                .layoutWeight(1F)
                .textGravity(Gravity.center)

            next = Text(">>")
                .padding(16)
                .background(decorViewButtonBackground(Color.GREEN))
                .layout(WRAP, FILL)
                .onClick {
                    val vp = decor.viewPager
                    vp.currentItem = vp.currentItem + 1
                }

        }.layout(FILL, 56)
            .pagerDecor(Gravity.bottom)
            .pagerOnPageChangedListener(object : ViewPagerOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    val count = pagesCount
                    previous.enabled(position > 0).render()
                    next.enabled((position + 1) < count).render()
                    text.text("${position + 1} / $count").render()
                }
            })
    }

    private fun decorViewButtonBackground(baseColor: Int): Drawable = StatefulShape.drawable {
        val base = RoundedRectangle(9) {
            fill(baseColor)
            padding(4)
            alpha(0.2F) // disabled alpha
        }

        setPressed(base.copy {
            alpha(0.5F)
        })

        setEnabled(base.copy {
            alpha(1F)
        })

        setDefault(base)
    }

    @Suppress("FunctionName")
    private fun ViewFactory<ViewPagerLayoutParams>.MyPage(
        title: String,
        color: Int,
        pageWidthRatio: Float? = null
    ): ViewElement<out View, ViewPagerLayoutParams> = ZStack {
        Text(title)
            .textColor(Color.RED)
            .textGravity(Gravity.center)
            .layoutFill()
            .background(color)
    }.pagerPageWidthRatio(pageWidthRatio)
}

// Preview for the sample
@Suppress("ClassName", "unused")
class __AdaptUIPagerSample(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {
    init {
        App.mock(context)
        id = R.id.frame_layout
        AdaptUIPagerSample().render(this)
    }
}