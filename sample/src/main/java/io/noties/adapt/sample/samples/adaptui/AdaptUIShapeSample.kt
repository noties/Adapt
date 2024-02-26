package io.noties.adapt.sample.samples.adaptui

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.explore.ExploreShapePath
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.gradient.GradientEdge
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.gradient.RadialGradient
import io.noties.adapt.ui.gradient.SweepGradient
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWeight
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.onViewAttachedStateChanged
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Arc
import io.noties.adapt.ui.shape.ArcShape
import io.noties.adapt.ui.shape.Asset
import io.noties.adapt.ui.shape.AssetShape
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.shape.CapsuleShape
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.CircleShape
import io.noties.adapt.ui.shape.CornersShape
import io.noties.adapt.ui.shape.Line
import io.noties.adapt.ui.shape.OvalShape
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.shape.RoundedRectangleShape
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.ShapeDrawable
import io.noties.adapt.ui.shape.StatefulShape
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.shape.reference
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.hex
import io.noties.adapt.ui.util.withAlphaComponent
import kotlin.math.roundToInt

@AdaptSample(
    id = "20220926220755",
    title = "AdaptUI, Shape usage",
    description = "Asset, Capsule, Circle, Corners, Oval, Rectangle, RoundedRectangle",
    tags = ["adapt-ui", "ui-shape"]
)
class AdaptUIShapeSample : SampleView() {

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

//                    path()

                    arc()

                    elevated()

                    gradients()

                    rotation()

                    stateful()

                    animated()

                    references()

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


        // first row with asset, arc, circle, oval and rectangle
        HStack {
            // Asset
            listOf(
                AssetShape(drawableTinted(Colors.orange)) {
                    gravity(Gravity.center)
                },
                ArcShape(240F, -300F),
                CircleShape(),
                OvalShape(),
                RectangleShape()
            ).forEach {
                view(it)
                    .layout(0, FILL)
                    .layoutWeight(1F)
            }
        }.layout(FILL, 64)

        HStack {

            // capsule automatically take smallest dimension
            CapsuleShape().also { view(it).layout(24, FILL) }
            CapsuleShape().also { view(it).layout(56, 24) }

            RoundedRectangleShape(8).also { view(it).layout(56, FILL) }

            // special rounded rectangle with all corners customizable
            CornersShape(24, 8, 24, 4).also {
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
            .background(RectangleShape {

                stroke(Colors.black)
                padding(2)

                // add an asset

                Capsule {
                    fill(hex("#ccc"))
                    size(height = 56, gravity = Gravity.center)
                    padding(4)

                    // align to start
                    Asset(drawableTinted(Colors.black)) {
                        size(36, 36, Gravity.leading.center)
                        translate(x = 8)
                    }

                    // align to end
                    Asset(drawableTinted(Colors.orange)) {
                        size(24, 24, Gravity.trailing.center)
                        translate(x = -8)
                    }
                }
            })
    }

    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.relativeValues() {
        View()
            .layout(FILL, 128)
            .background(RectangleShape {
                stroke(Colors.orange)
                padding(12)

                Line {
                    fromRelative(0F, 0F)
                    toRelative(1F, 1F)
                    stroke(
                        LinearGradient.edges { top.leading to bottom.trailing }
                            .setColors(Colors.accent, Colors.primary),
//                        Colors.black,
                        4,
                        16,
                        2
                    )
                }

                // relative to bounds size, half of width and 1/4 of height
                Rectangle {
                    sizeRelative(0.5F, 0.25F)
                    fill(0x20ff0000)
                }

                // relative padding
                Rectangle {
                    // half of available dimensions is padding -> rest is content
                    paddingRelative(0.25F)

                    fill(0x2000ff00)
                }

                // translate
                Rectangle {
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
                .background(CircleShape {
                    fill(Colors.orange)
                })

            View()
                .layout(64, 64)
                .layoutMargin(leading = 8)
                .elevation(8)
                .background(RoundedRectangleShape(8) {
                    fill(Colors.orange)
                })

            View()
                .layout(FILL, 24)
                .layoutMargin(leading = 8, trailing = 8)
                .elevation(8)
                .background(CornersShape(bottomLeading = 8) {
                    fill(Colors.orange)
                })

        }.noClip()
            .padding(bottom = 12)
    }

    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.gradients() {
        val linearGradients: List<Shape> = listOf(
            RectangleShape {
                alpha(0.5F)
                fill(
                    LinearGradient.edges { top.leading to bottom.trailing }
                        .setColors(
                            Colors.orange.withAlphaComponent(0.75F),
                            Colors.black.withAlphaComponent(0.75F)
                        )
                )
                stroke(
                    LinearGradient.edges { top.trailing to bottom.leading }
                        .setColors(Colors.black, Colors.orange),
                    4
                )
            },
            ArcShape(225F, -270F) {
                fill(
                    LinearGradient.edges { top to bottom.trailing }
                        .setColors(Colors.black, Colors.primary, Colors.accent, Colors.orange)
                )
            },
            RoundedRectangleShape(8) {
                fill(
                    LinearGradient.edges { leading to trailing }
                        .setColors(
                            Colors.black to 0.1F,
                            Colors.primary to 0.5F,
                            Colors.accent to 0.6F,
                            Colors.orange to 1F
                        )
                )
            },
            CircleShape {
                fill(
                    LinearGradient.edges { top to bottom }
                        .setColors(
                            Colors.orange,
                            Colors.black,
                            Colors.primary,
                            Colors.orange
                        )
                )
            }
        )

        val radialGradients = listOf(
            RectangleShape {
                fill(
                    RadialGradient.center().setColors(
                        Colors.orange,
                        Colors.black
                    )
                )
            },
            CircleShape {
                fill(
                    RadialGradient.center().setColors(
                        Colors.orange, Colors.accent, Colors.primary, Colors.black
                    )
                )
            },
            CornersShape(leadingTop = 48) {
                fill(
                    RadialGradient.edge(GradientEdge.top)
                        .setColors(
                            Colors.black to 0.1F,
                            Colors.primary to 0.5F,
                            Colors.accent to 0.6F,
                            Colors.orange to 1F
                        )
                )
            },
            CapsuleShape {
                sizeRelative(height = 0.5F, gravity = Gravity.center)
                fill(
                    RadialGradient.edge(GradientEdge.leading)
                        .setColors(
                            Colors.orange to 0.1F,
                            Colors.accent to 0.4F,
                            Colors.primary to 0.75F,
                            Colors.orange to 1F
                        )
                )
            }
        )

        val sweepGradients = listOf(
            CircleShape {
                fill(
                    SweepGradient.center().setColors(
                        Colors.orange,
                        Colors.primary
                    )
                )
            },
            RoundedRectangleShape(24) {
                padding(8)
                stroke(
                    SweepGradient.center().setColors(
                        Colors.orange, Colors.primary, Colors.accent, Colors.black
                    ),
                    16
                )
            },
            RectangleShape {
                fill(
                    SweepGradient.center().setColors(
                        Colors.orange to 0.1F,
                        Colors.accent to 0.2F,
                        Colors.primary to 0.7F,
                        Colors.black to 1F
                    )
                )
            }
        )

        VStack {

            listOf(linearGradients, radialGradients, sweepGradients)
                .forEach { shapes ->
                    HStack {
                        shapes.forEach {
                            View()
                                .layout(0, FILL, 1F)
                                .layoutMargin(2)
                                .background(it)
                        }
                    }.layout(FILL, 100)
                }
        }

    }

    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.rotation() {
        View()
            .layout(FILL, 128)
            .background(RectangleShape {
                val base = RectangleShape {
                    sizeRelative(0.5F, 0.5F)
                    gravity(Gravity.bottom.trailing)
                    padding(8)
                }
                val colors = listOf(Colors.orange, Colors.black, Colors.accent, Colors.primary)
                listOf(0F, 25F, 85F, 110F, 150F, 189F, 250F)
                    .withIndex()
                    .forEach { (i, degree) ->
                        add(base.copy {
                            rotate(degree)
                            stroke(colors[i % colors.size])
                        })
                        add(base.copy {
                            rotate(degree)
                            gravity(Gravity.leading.top)
                            stroke(colors[i % colors.size])
                        })
                    }
            })
    }

    // allows creation of stateful shapes
    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.stateful() {
        View()
            .layout(FILL, 56)
            .background(StatefulShape.drawable {

                val base = RoundedRectangleShape(8) {
                    fill(Colors.orange)
                    stroke(Colors.black, 4)
                }

                setDefault(RectangleShape {
                    add(base.copy {
                        stroke = null
                        fill(Colors.black.withAlphaComponent(0.4F))
                        gravity(Gravity.bottom)
                        padding(horizontal = -2)
                    })
                    add(base.copy {
                        padding(top = 2, bottom = 8)
                    })
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
            .background(RoundedRectangleShape(12) {
                stroke(
                    LinearGradient.edges { leading to trailing }
                        .setColors(
                            Colors.orange,
                            Colors.primary
                        ),
                    width = 2,
//                    dashWidth = 12
                )

                val corner = CircleShape {
                    size(12, 12)
                }
                add(corner.copy {
                    fill(Color.RED)
                    gravity(Gravity.top.leading)
                })

                add(corner.copy {
                    fill(Color.GREEN)
                    gravity(Gravity.bottom.trailing)
                })

                Rectangle {
                    sizeRelative(0.5F)
                    size(height = 24)
                    fill(Color.BLUE)
                    gravity(Gravity.center)
                }

            }.newDrawable()
                .also { shapeDrawable ->
                    val target = 48
                    animator.addUpdateListener {
                        val fraction = it.animatedFraction
                        val value = (target * fraction).roundToInt()
                        shapeDrawable.shape.padding(value)
                        shapeDrawable.invalidateSelf()
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

    private fun ViewFactory<LayoutParams>.references() {
        // a special Shape.drawable function to accept `references`
        //  which could be retrieved later

        class Ref {
            lateinit var gradient: Shape
            var added: Shape? = null
        }

        // in order to invalidate a shape we would need to reference ShapeDrawable
        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
        lateinit var drawable: ShapeDrawable<Ref>

        View()
            .layout(FILL, 128)
            .background(ShapeDrawable(Ref()) { ref ->
                Rectangle {
                    add(Circle {
                        fill(Colors.orange)
                        gravity(Gravity.center)
                    })

                    // or just save normally
                    ref.gradient = Rectangle {
                        // also possible to call reference here
                        reference(ref::gradient)

                        fill(
                            LinearGradient.edges { top to bottom }
                                .setColors(Colors.accent, Colors.primary)
                        )
                        size(height = 16, gravity = Gravity.bottom)

                    }.reference(ref::gradient)
                }
            }.also { drawable = it })
//            .background(Shape.drawable(Rectangle(), Ref()) { ref ->
//
//                add(Circle {
//                    fill(Colors.orange)
//                    gravity(Gravity.center)
//                })
//
//                add(Rectangle {
//                    // also possible to call reference here
//                    reference(ref::gradient)
//
//                    fill(
//                        LinearGradient(
//                            GradientEdge.Top to GradientEdge.Bottom,
//                            Colors.accent, Colors.primary
//                        )
//                    )
//                    size(height = 16, gravity = Gravity.bottom)
//
//                }.reference(ref::gradient))
//
//            }.also { drawable = it })
            .ifAvailable(Build.VERSION_CODES.M) {
                it.foregroundDefaultSelectable()
            }
//            .onClick {
//                // NB! experiment only to see if we can make automatic transitions
//                drawable.animate {
//                    Debug.i(shape)
//                    if (flag) {
//                        ref.gradient
//                            .alpha(0.5F)
//                            .translate(x = 128, y = -48)
//                            .apply {
//                                add(Circle {
//                                    fill(Colors.orange)
//                                    padding(2)
//                                    reference(ref::added)
//                                })
//                            }
//                    } else {
//                        ref.gradient
//                            .alpha(1F)
//                            .translateRelative(x = 0.1F, y = 0F).apply {
//                                ref.added?.also { remove(it) }
//                            }
//                    }
//                    flag = !flag
//                }
//            }
    }

    private var flag = true

    private fun ViewFactory<LayoutParams>.arc() {
        View()
            .layout(FILL, 150)
            .background(RectangleShape {
                Rectangle {
                    size(128, 128, Gravity.leading.center)
                    padding(8)
                    stroke(Colors.black.withAlphaComponent(0.2F), 2, 2, 2)

                    Arc(0F, 90F) {
                        fill(Colors.orange)
                    }
                    Arc(90F, 90F) {
                        fill(Colors.primary)
                    }
                    Arc(180F, 90F) {
                        fill(
                            LinearGradient.edges { top to bottom }
                                .setColors(Colors.accent, Colors.black)
                        )
                        translate(-5, -4)
                    }
                    Arc(270F, 90F) {
                        stroke(Colors.black, 2)
                        padding(1)
                    }
                }

                Rectangle {
                    padding(leading = 132, trailing = 1)
                    stroke(Colors.black, 1)

                    val colors = listOf(Colors.orange, Colors.black, Colors.accent, Colors.primary)

                    listOf(
                        0F to 120F,
                        120F to 132F,
                        132F to 180F,
                        180F to 190F,
                        190F to 220F,
                        220F to 300F,
//                        300F to 320F, // missing
                        320F to 360F
                    ).withIndex()
                        .forEach { (i, v) ->
                            Arc(v.first, v.second - v.first) {
                                fill(colors[i % colors.size])
                                padding(2)
                            }
                        }
                }
            })
    }

    private fun ViewFactory<LayoutParams>.path() {
        View()
            .layout(FILL, 128)
            .background(ExploreShapePath.Path {
                stroke(Colors.black, 2)

                move(0, 0)
                line(24, 24)
                quad(12 to 48, 48 to 48)
                line(96, 48)
            })
    }

    private fun drawableTinted(tintColor: Int): Drawable =
        context.getDrawable(R.drawable.ic_search_24)!!.also {
            it.setTint(tintColor)
        }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIShapeSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIShapeSample()
}