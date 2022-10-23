package io.noties.adapt.sample.samples.adaptui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import io.noties.adapt.ui.shape.Dimension
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.ShapeDrawable
import io.noties.adapt.ui.shape.copy
import java.util.IdentityHashMap
import kotlin.math.roundToInt


object ExploreAdaptUIShapeTransition {

    // TODO: stateful change that trigger animation on state changed

    // TODO: if drawable has references, it would still reference old (before copying)
    //  shapes... so our copy shape won't record any changes.. but what if, we let it
    //  be and then use our copy as initial reference point from which we determine the values
    //  changed?
    fun <S : ShapeDrawable> S.animate(block: S.() -> Unit) {

        // record initial state -> key is original shape, so we
        //  can reference it after changes had happened
        val start = shape.record()

        println("start:$start")

        // let changes be filled
        block(this)

        // record final state (with all changes applied)
        val end = shape.shapes()

        println("end:$end")

        val properties = diff(start, end)

        println("properties:$properties")

        if (properties.isEmpty()) {
            return
        }

        val drawable = this

        val animator = ValueAnimator.ofFloat(0F, 1F)
        animator.duration = 1000L
        animator.setEvaluator(FloatEvaluator())
        animator.addUpdateListener {
            if (drawable.callback == null) {
                animator.cancel()
            } else {
                val fraction = it.animatedFraction
                properties.forEach { p -> p.update(fraction) }
                drawable.invalidateSelf()
            }
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                // TODO: as most of the properties would need to have bounds,
                //  we need to trigger first draw
                properties.forEach { it.onBeforeStarted() }

                // TODO: here we must trigger filling the bounds.. can we expose it?
            }

            override fun onAnimationEnd(animation: Animator?) = onFinished()
            override fun onAnimationCancel(animation: Animator?) = onFinished()
            private fun onFinished() = properties.forEach { p -> p.onFinished() }
        })

        animator.start()

        // diff changes
        // TODO: we can do it np for common properties defined in shape itself
        //  but what to do with custom properties? For example, in Line? should we expose
        //  a way to fill the differences?

        // TODO: how to use the copied version to start the animation?

        // TODO: if we copy, a new instance is created, so we won't be able to
        //  find changed shapes (new instance)
        //  It seems we need to create a map of all shapes before copying, then

        // TODO: how to remove a shape? from the set, for example add it -> animate alpha
        //  and then finished remove?

        // TODO: animate add -> animate alpha, start with 0 and finish and value in shape?

        // TODO: what if a child changes parent? this is weird...
        //  but we could deal with it

        // TODO: how can we associate an animator with a shape or a drawable, so it has only one instance?
        //  should we add it to the ShapeDrawable?
    }

    private abstract class Property(val shape: Shape) {

        // must apply initial values before animation had started
        abstract fun onBeforeStarted()

        abstract fun update(fraction: Float)

        open fun onFinished() = Unit
    }

    private class AlphaProperty(
        val parent: Shape?,
        shape: Shape,
        val startAlpha: Float,
        val endAlpha: Float
    ) : Property(shape) {

        private val delta = endAlpha - startAlpha

        override fun onBeforeStarted() {
            shape.alpha(startAlpha)

            parent?.also {
                // if parent is not null -> we were removed, add temporary
                //  and remove in onFinished
                parent.add(shape)
            }
        }

        override fun update(fraction: Float) {
            shape.alpha(startAlpha + (delta * fraction))
            println("fraction:$fraction alpha:${shape.alpha}")
        }

        override fun onFinished() {
            parent?.also {
                parent.remove(shape)
            }
        }

        override fun toString(): String {
            return "AlphaProperty(parent=$parent, startAlpha=$startAlpha, endAlpha=$endAlpha, delta=$delta)"
        }

        companion object {
            fun added(shape: Shape) =
                AlphaProperty(null, shape, 0F, shape.alpha ?: 1F)

            // we need parent only if we were removed (first, we add it when animation starts
            //  then after it is finished we remove self from parent)
            fun removed(parent: Shape?, shape: Shape) =
                AlphaProperty(parent, shape, shape.alpha ?: 1F, 0F)

            fun changed(start: Shape, shape: Shape): AlphaProperty? {
                val startAlpha = start.alpha ?: 1F
                val endAlpha = shape.alpha ?: 1F
                if (startAlpha == endAlpha) return null
                return AlphaProperty(
                    null,
                    shape,
                    startAlpha,
                    endAlpha
                )
            }
        }
    }

    private fun diff(map: IdentityHashMap<Shape, ShapeInfo>, end: List<Shape>): List<Property> {

        val properties = mutableListOf<Property>()

        // added, removed, changed, not-changed
        for (shape in end) {
            val info = map.remove(shape)
            if (info == null) {
                // newly added shape
                properties.add(AlphaProperty.added(shape))
            } else {
                // it is present in both start AND end sets
                // call `equals`
                if (shape != info.reference) {
                    // changed -> extract changes
                    properties.addAll(changes(shape, info.reference))
                }
                // else -> not changed
            }
        }

        // now, iterate over the rest -> they are not present in new set
        for (entry in map.entries) {
            properties.add(
                AlphaProperty.removed(
                    entry.value.parent,
                    entry.key
                )
            )
        }

        return properties
    }

    private fun changes(shape: Shape, references: Shape): List<Property> {
        val properties = mutableListOf<Property?>()
        properties.add(AlphaProperty.changed(references, shape))
        properties.add(TranslationXProperty.changed(references, shape))
        return properties.filterNotNull()
    }

    private class ShapeInfo(val parent: Shape?, val reference: Shape) {
        override fun toString(): String {
            return "ShapeInfo(parent=$parent, reference=$reference)"
        }
    }

    // TODO: it seems we must keep track of parent (in order to add/remove on animation)
    private fun Shape.shapes(): List<Shape> {
        val set = mutableListOf<Shape>()

        // it seems here we must keep track of parent
        fun add(shape: Shape) {
            set.add(shape)
            shape.children.forEach(::add)
        }

        add(this)

        return set
    }

    private fun Shape.record(): IdentityHashMap<Shape, ShapeInfo> {
        // extract all shapes from main shape and create a copy of it (to keep track of changes)
        //  value would be a reference point to understand what had changed
        val map = IdentityHashMap<Shape, ShapeInfo>()

        fun add(parent: Shape?, shape: Shape) {
            map[shape] = ShapeInfo(parent, shape.copy())
            for (child in shape.children) {
                add(shape, child)
            }
        }

        add(null, this)

        return map
    }

    private class TranslationXProperty(
        shape: Shape,
        val start: Dimension?,
        val target: Dimension?
    ) : Property(shape) {

        // okay, we could use here any dimension (both exact and relative), but we need
        //  to reference fillRect of the shape...

        private var startX: Int = 0

        override fun onBeforeStarted() {
            // and what if it is relative? can we use it here?
            // mixing px and dp...
            startX = start?.resolve(shape.drawRect().width()) ?: 0
            shape.translate(x = startX)
        }

        override fun update(fraction: Float) {
            // as we cannot know for sure that end is final, we need to evaluate it each time
            // TODO: we are mixing actual data between pixels and points
            // TODO: we need to do checks for types and Exact vs Exact,
            //  Relative vs Relative and only if types are different reduce to actual
            //  px values (which we would need to convert to dp)
            val targetX = target?.resolve(shape.drawRect().width()) ?: 0
            val delta = targetX - startX
            // TODO: do we need to check anything here to return?
            shape.translate(x = startX + (delta * fraction).roundToInt())
        }

        companion object {
            fun changed(start: Shape, shape: Shape): TranslationXProperty? {
                val startX = start.translation?.x
                val targetX = shape.translation?.x
                // if they are equal -> do no thing
                if (start == targetX) {
                    return null
                }
                return TranslationXProperty(shape, startX, targetX)
            }
        }
    }
}