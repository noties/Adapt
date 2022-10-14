package io.noties.adapt.sample.samples.adaptui

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import io.noties.adapt.sample.App
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.PreviewLayout
import io.noties.adapt.sample.util.hex
import io.noties.adapt.sample.util.withAlphaComponent
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.createView
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.gradient.GradientEdge
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.gradient.RadialGradient
import io.noties.adapt.ui.gradient.SweepGradient
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWeight
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.onViewAttachedStateChanged
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Asset
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Corners
import io.noties.adapt.ui.shape.Oval
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.StatefulShape
import io.noties.adapt.ui.util.Gravity
import kotlin.math.roundToInt

@AdaptSample(
    id = "20220926220755",
    title = "AdaptUI, Shape usage",
    description = "Asset, Capsule, Circle, Corners, Oval, Rectangle, RoundedRectangle",
    tags = ["adapt-ui", "ui-shape"]
)
class AdaptUIShapeSample : SampleView() {

    private lateinit var context: Context

    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {
        this.context = view.context
        val child = createView(view.context)
        (view as ViewGroup).addView(child)
    }

    private fun createView(context: Context): View {
        return ViewFactory.createView(context) {
            VScroll {
                VStack {

                    // a list of basic shapes
                    basicShapes()

                    basicComposition()

                    relativeValues()

                    elevated()

                    gradients()

                    stateful()

                    animated()

                }.noClip()

            }.layoutFill()
        }
    }

    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.basicShapes() {
//            Corners(2, 4, 8, 16),
//            RoundedRectangle(8)

        fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.view(shape: Shape) = View()
            .background(shape.apply {
                fill(Colors.orange.withAlphaComponent(0.1F))
                stroke(Colors.orange)
                padding(1)
            })


        // first row with asset, circle, oval and rectangle
        HStack {
            // Asset
            listOf(
                Asset(drawableTinted(Colors.orange)) {
                    gravity(Gravity.center)
                },
                Circle(),
                Oval(),
                Rectangle()
            ).forEach {
                view(it)
                    .layout(0, FILL)
                    .layoutWeight(1F)
            }
        }.layout(FILL, 64)

        HStack {

            // capsule automatically take smallest dimension
            Capsule().also { view(it).layout(24, FILL) }
            Capsule().also { view(it).layout(56, 24) }

            RoundedRectangle(8).also { view(it).layout(56, FILL) }

            // special rounded rectangle with all corners customizable
            Corners(24, 8, 24, 4).also {
                view(it).layout(0, FILL)
                    .layoutWeight(1F)
            }

        }.layout(FILL, 56)
            .layoutMargin(top = 8)
    }

    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.basicComposition() {
        // each shape can contain other shapes
        View()
            .layout(FILL, 64)
            .background(Rectangle {

                stroke(Colors.black)
                padding(2)

                // add an asset

                add(Capsule()) {
                    fill(hex("#cccccc"))
                    size(height = 56, gravity = Gravity.center)
                    padding(4)

                    // align to start
                    add(Asset(drawableTinted(Colors.black))) {
                        size(24, 24, Gravity.leading.center)
                        translate(x = 8)
                    }

                    // align to end
                    add(Asset(drawableTinted(Colors.black))) {
                        size(24, 24, Gravity.trailing.center)
                        translate(x = -8)
                    }
                }
            })
    }

    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.relativeValues() {
        View()
            .layout(FILL, 128)
            .background(Rectangle {
                stroke(Colors.orange)
                padding(2)

                // relative to bounds size, half of width and 1/4 of height
                add(Rectangle()) {
                    sizeRelative(0.5F, 0.25F)
                    fill(0x20ff0000)
                }

                // relative padding
                add(Rectangle()) {
                    // half of available dimensions is padding -> rest is content
                    paddingRelative(0.25F)

                    fill(0x2000ff00)
                }

                // translate
                add(Rectangle()) {
                    size(48, 48, Gravity.trailing.bottom)
                    // negative values as we start at bottom right
                    translateRelative(x = -0.25F, y = -0.25F)
                    fill(0x200000ff)
                }
            })
    }

    // each shape contributes to outline, so elevation should happen out of box
    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.elevated() {
        HStack {

            View()
                .layout(64, 64)
                .layoutMargin(leading = 8)
                .elevation(8)
                .background(Circle {
                    fill(Colors.orange)
                })

            View()
                .layout(64, 64)
                .layoutMargin(leading = 8)
                .elevation(8)
                .background(RoundedRectangle(8) {
                    fill(Colors.orange)
                })

            View()
                .layout(FILL, 24)
                .layoutMargin(leading = 8, trailing = 8)
                .elevation(8)
                .background(Corners(bottomLeading = 8) {
                    fill(Colors.orange)
                })

        }.noClip()
            .padding(bottom = 12)
    }

    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.gradients() {
        HStack {

            View()
                .layout(0, FILL)
                .layoutWeight(1F)
                .background(Rectangle {

                    padding(4)

                    fill(
                        LinearGradient(
                            GradientEdge.LeadingTop to GradientEdge.BottomTrailing,
                            Colors.orange.withAlphaComponent(0.75F),
                            Colors.black.withAlphaComponent(0.75F)
                        )
                    )

                    stroke(
                        LinearGradient(
                            GradientEdge.TopTrailing to GradientEdge.LeadingBottom,
                            Colors.black,
                            Colors.orange
                        ), 4
                    )
                })

            View()
                .layout(0, FILL)
                .layoutWeight(1F)
                .background(Circle {
                    fill(
                        RadialGradient(
                            Colors.orange.withAlphaComponent(0.75F),
                            Colors.black,
                            // allows specifying starting edge
//                        GradientEdge.LeadingBottom,
//                        0.75F
                        )
                    )
                })

            View()
                .layout(0, FILL)
                .layoutWeight(1F)
                .background(RoundedRectangle(8) {
                    padding(4)
                    fill(
                        SweepGradient(
                            Colors.orange,
                            Colors.black
                        )
                    )
                })

        }.layout(FILL, 100)
    }

    // allows creation of statful shapes
    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.stateful() {
        View()
            .layout(FILL, 56)
            .background(StatefulShape.drawable {

                val base = RoundedRectangle(8) {
                    fill(Colors.orange)
                    stroke(Colors.black, 4)
                }

                setDefault(Rectangle {
                    add(base.copy()) {
                        strokeColor = null
                        fill(Colors.black.withAlphaComponent(0.4F))
                        gravity(Gravity.bottom)
                        padding(horizontal = -2)
                    }
                    add(base.copy()) {
                        padding(top = 2, bottom = 8)
                    }
                })

                setPressed(base.copy {
                    padding(top = 8, bottom = 2)
                })
            })
            .onClick { }
            .layoutMargin(top = 8)
            .layoutMargin(horizontal = 16)
    }

    private fun ViewFactory<ViewGroup.MarginLayoutParams>.animated() {

        val animator = ValueAnimator.ofFloat(0F, 1F)
        animator.duration = 450L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.setEvaluator(FloatEvaluator())

        View()
            .layout(FILL, 128)
            .background(Shape.drawable(Rectangle()) {
                stroke(Colors.black)

                val corner = Circle {
                    size(12, 12)
                }
                add(corner.copy()) {
                    fill(Color.RED)
                    gravity(Gravity.top.leading)
                }

                add(corner.copy()) {
                    fill(Color.GREEN)
                    gravity(Gravity.bottom.trailing)
                }

                add(Rectangle()) {
                    sizeRelative(0.5F)
                    size(height = 24)
                    fill(Color.BLUE)
                    gravity(Gravity.center)
                }

            }.also { shapeDrawable ->
                val target = 48
                animator.addUpdateListener {
                    val fraction = it.animatedFraction
                    val value = (target * fraction).roundToInt()
                    shapeDrawable.invalidate {
                        padding(value)
                    }
                }
            })
            .onViewAttachedStateChanged { _, attached ->
                if (attached) {
                    animator.start()
                } else {
                    animator.cancel()
                }
            }
    }

    private fun drawableTinted(tintColor: Int): Drawable =
        context.getDrawable(R.drawable.ic_search_24)!!.also {
            it.setTint(tintColor)
        }
}

@Suppress("ClassName", "unused")
class __AdaptUIShapeSample(context: Context, attrs: AttributeSet?) : PreviewLayout(context, attrs) {
    init {
        AdaptUIShapeSample().render(this)
    }
}