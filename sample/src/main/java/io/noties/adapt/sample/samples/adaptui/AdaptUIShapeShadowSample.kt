package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.PreviewUILayout
import io.noties.adapt.sample.util.withAlphaComponent
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.adaptRecyclerView
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.Recycler
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.recyclerGridLayoutManager
import io.noties.adapt.ui.element.textAutoSize
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.item.ElementItemNoRef
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.setItems
import io.noties.adapt.ui.shape.ArcShape
import io.noties.adapt.ui.shape.AssetShape
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.CircleShape
import io.noties.adapt.ui.shape.CornersShape
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.shape.RoundedRectangleShape
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.translation
import io.noties.adapt.ui.util.Gravity
import io.noties.debug.Debug

@AdaptSample(
    id = "20230512142056",
    title = "AdaptUI, shape shadow",
    tags = ["adapt-ui", "shape", "shadow"]
)
class AdaptUIShapeShadowSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        ZStack {

            Recycler()
                .recyclerGridLayoutManager(3) { rv, spanCount, position ->
                    val adapt = AdaptRecyclerView.find(rv) ?: return@recyclerGridLayoutManager 1
                    val item = adapt.items()[position]
                    if (item is CardItem) {
                        spanCount
                    } else {
                        1
                    }
                }
                .noClip()
                .layoutFill()
                .padding(8)
                .adaptRecyclerView()
                .setItems(items())

        }.layoutFill()
            .noClip()
    }

    private fun items(): List<Item<*>> {
        fun config(radius: Int = 12): Shape.() -> Unit {
            return {
                fill(Colors.white)
                shadow(Colors.black, radius)
            }
        }
        return listOf(
            ShapeItem("Rectangle", RectangleShape(), config()),
            ShapeItem("RoundedRectangle", RoundedRectangleShape(8), config()),
            ShapeItem("Corners", CornersShape(4, 12, 24, 48), config()),
            ShapeItem("Circle", CircleShape(), config()),
            ShapeItem("Arc", ArcShape(90F, 180F), config()),
//            ShapeItem("Oval", Oval(), config()),
            ShapeItem("Nested shadows", CircleShape()) {
                fill(Colors.white)
                shadow(Colors.black.withAlphaComponent(0.5F), 12)

                add(Circle {
                    padding(12)
                    fill(Colors.primary)
                    shadow(Colors.primary, 12)

                    add(Circle {
                        padding(12)
                        fill(Colors.orange)
                        shadow(Colors.orange, 12)

                        add(Circle {
                            padding(12)
                            fill(0xFFff0000.toInt())
                            shadow(Colors.white, 12)
                        })
                    })
                })
            },
            ShapeItem("Follows content", RectangleShape()) {
                fill(Colors.white)
                size(24, 24, Gravity.bottom.trailing)
                shadow(Colors.accent, 4)
            },
            // Asset is not supported (does not use Paint to draw drawable resource)
            ShapeItem("Asset (N/A)", AssetShape(context.getDrawable(R.drawable.logo)!!)) {
                size(56)
                gravity(Gravity.center)
                shadow(Colors.black, 8)
            },
            ShapeItem("Rotated", RectangleShape()) {
                fill(Colors.white)
                sizeRelative(0.75F, 0.25F, Gravity.center)
                rotate(45F)
                shadow(Colors.black, 8)
            },
            ShapeItem("Rotated 2X", RectangleShape()) {
                add(Rectangle {
                    fill(Colors.white)
                    sizeRelative(0.75F, 0.25F, Gravity.center)
                    rotate(45F)
                    shadow(Colors.orange, 8)
                })
                add(Rectangle {
                    fill(Colors.white)
                    sizeRelative(0.75F, 0.25F, Gravity.center)
                    rotate(-45F)
                    shadow(Colors.primary, 16)
                })
            },
            ShapeItem("Offset", RectangleShape()) {
                fill(Colors.white)
                shadow(Colors.black, 12, 4, -8)
            },
            ShapeItem("Relative", RectangleShape()) {
                size(48, 48)
                fill(Colors.white)
                shadowRelative(
                    Colors.black,
                    0.25F,
                    0.1F,
                    -0.5F
                )
            },
            CardItem()
        )
    }

    private class ShapeItem(
        val name: String,
        val shape: Shape,
        val config: Shape.() -> Unit
    ) : ElementItem<ShapeItem.Ref>(hash(name), { Ref() }) {
        class Ref {
            lateinit var nameView: TextView
            lateinit var view: View
        }

        override fun bind(holder: Holder<Ref>) {
            with(holder.ref) {
                nameView.text = name
                config(shape)
                view.background = shape.newDrawable()
            }
        }

        override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
            ZStack {
                View()
                    .layoutFill()
                    .reference(ref::view)
                Text()
                    .reference(ref::nameView)
                    .layoutWrap()
                    .layoutGravity(Gravity.center)
                    .textSize(14)
                    .textColor(Colors.black)
                    .padding(horizontal = 8)
                    .ifAvailable(Build.VERSION_CODES.O) {
                        it.textAutoSize(8, 14)
                    }
            }.layout(FILL, 120)
                .noClip()
                .padding(8)
        }
    }

    // creates a card, drops shadow, clips content, adds click and foreground
    private class CardItem : ElementItemNoRef(0L) {
        override fun ViewFactory<ViewGroup.LayoutParams>.body() {
            ZStack {

                // this is the base shape
                //  we will use it to draw background and shadow and clip content
                val base = RoundedRectangleShape(12) {
                    // it does not have any info except its outline
//                    fill(Colors.primary)
                }

                // container that will drop shadow
                ZStack {

                    // card content
                    VStack {

                        Text("The title of the card!")
                            .textSize(20)
                            .textColor(Colors.orange)
                            .textFont(fontStyle = Typeface.BOLD)
                            .translation(x = -16, y = -16)

                        Text("The text on top is clipped, so no content would exit the card area")
                            .textSize(16)
                            .textColor(Colors.white)
                            .padding(8)
                    }
                        // just set, it won't be drawn, but will clip content
                        .background(base)
                        .clipToOutline()
                        .ifAvailable(Build.VERSION_CODES.M) {
                            it.foregroundDefaultSelectable()
                        }
                        .onClick {
                            Debug.e("card clicked!")
                        }

                }.background(base.copy {
                    fill(Colors.primary)
                    shadow(Colors.accent, 12)
                })

            }.layout(FILL, 128)
                .padding(16)
                .noClip()
        }
    }
}

private class PreviewAdaptUIShapeShadowSample(context: Context, attrs: AttributeSet?) :
    PreviewUILayout(context, attrs) {
    override fun ViewFactory<LayoutParams>.body() {

        with(AdaptUIShapeShadowSample()) {
            context = this@PreviewAdaptUIShapeShadowSample.context
            body()
        }
    }
}