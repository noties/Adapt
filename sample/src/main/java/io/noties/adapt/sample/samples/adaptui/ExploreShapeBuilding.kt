package io.noties.adapt.sample.samples.adaptui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import io.noties.adapt.ui.shape.Asset
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.shape.Corners
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.Shape

@Suppress("FunctionName")
private object ExploreShapeBuilding {

    class Rectangle2(block: Rectangle2.() -> Unit = {}) : Shape() {

        init {
            block(this)
        }

        override fun clone(): Shape {
            TODO("Not yet implemented")
        }

        override fun toStringDedicatedProperties(): String {
            TODO("Not yet implemented")
        }

        override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
            TODO("Not yet implemented")
        }
    }

    // we would need 2 variant? raw and not raw?
    // it is actually very confusing, as it is not clear what would be called
    //  constructor, which does not add shape to parent or extension function
    fun Shape.Rectangle2(
        add: Boolean = true,
        block: Rectangle2.() -> Unit
    ): Rectangle2 {
        val rectangle2 = Rectangle2(block)
        if (add) {
            add(rectangle2)
        }
        return rectangle2
    }

    // TODO: how to have a constructor with block and a function with block
//    fun hey(): Shape {
//        return Rectangle2 {
//            Rectangle2 {
//
//            }
//        }
//    }


    class ShapeBuilder internal constructor(
        internal val shapes: MutableList<Shape> = mutableListOf()
    ) {
        fun add(shape: Shape): Shape {
            shapes.add(shape)
            return shape
        }
    }

    // not the same... as asset becomes it argument
    fun ShapeBuilder.Asset(drawable: Drawable, block: ShapeBuilder.(Asset) -> Unit): Asset {
        val builder = ShapeBuilder()
        val asset = Asset(drawable)
        block(builder, asset)
        builder.shapes.forEach(asset::add)
        shapes.add(asset)
        return asset
    }

    fun ShapeBuilder.Capsule(block: Capsule.() -> Unit): Capsule {
        val asset = Capsule().apply(block)
        shapes.add(asset)
        return asset
    }

    fun ShapeBuilder.Corners(block: Corners.() -> Unit): Corners {
        val asset = Corners().apply(block)
        shapes.add(asset)
        return asset
    }

    fun ShapeBuilder.Rectangle(block: Rectangle.() -> Unit): Rectangle {
        val rectangle = Rectangle().apply(block)
        shapes.add(rectangle)
        return rectangle
    }

    // actually... this won't work because it accepts only the first level
//  so, all functions in nested objects would be added to the root...
    fun Shape.Companion.builder(block: ShapeBuilder.() -> Unit): Shape {
        // which shape to return...
        //  as we do in the view? just take first, or in case of multiple ->
        //  automatically wrap in Rectangle?
        val builder = ShapeBuilder()
        block(builder)

        val shapes = builder.shapes
        if (shapes.size == 1) {
            return shapes.first()
        }

        val rectangle = Rectangle()
        shapes.forEach { rectangle.add(it) }
        return rectangle
    }

    fun hey() {
        Shape.builder {

            val a = Asset(Rectangle().newDrawable())
            val c = Capsule()
            val r = Rectangle()

            Asset(r.newDrawable()) {
                it.tint(612)
            }

            Capsule {
                fill(761)
            }

            Rectangle {
                fill(1256)
                stroke(1)

                Rectangle {
                    padding(32)
                }
            }
        }
    }
}