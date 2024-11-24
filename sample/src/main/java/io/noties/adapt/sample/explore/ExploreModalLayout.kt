package io.noties.adapt.sample.explore

import android.content.Context
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.explore.ExploreModalLayout.Modal
import io.noties.adapt.sample.explore.ExploreOverlayDrawBounds.overlayDrawBounds
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.ElementGroup
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStackContentMeasuredLast
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.ZStackWrapHeightOrScroll
import io.noties.adapt.ui.element.stackContentMeasureLast
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.AdaptUIPreviewLayout
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.scrollBarsEnabled
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.transitionGroup
import io.noties.adapt.ui.util.withAlphaComponent
import io.noties.debug.Debug
import kotlin.math.abs
import kotlin.math.roundToInt

object ExploreModalLayout {

    // first child is the scroll reference
    class ModalLayout(context: Context) : FrameLayout(context) {

        // minimum distance for event to be considered scrolling
        private var touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()

        private var touchStartX: Float? = null
        private var touchStartY: Float? = null

        private var isTrackingTouch = false
        private var isScrollEvent = false

//        private val scroller = Scroller(context)
//        private val detector = TouchDetector(this, scroller)

        override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
            // must be processed anyway?
            val result = super.dispatchTouchEvent(ev)

            // if view is disabled, then no tracking of dismiss
            if (!isEnabled) {
                return result
            }

            fun fallback(): Boolean {
                // clean up the state
                touchStartX = null
                touchStartY = null
                isTrackingTouch = false
                isScrollEvent = false
//                scroller.abortAnimation()
//                removeCallbacks(scrollRunnable)

                // TODO: encapsulate the change (translation, alpha, scale, modal-background)
                //  maybe receive a value and apply dedicated transformation
                translationY = 0F
                alpha = 1F

                // pass to original implementation
                return result
            }

//            Debug.i("touch{x:$touchStartX y:$touchStartY} is{tracking:$isTrackingTouch scroll:$isScrollEvent}")
//            Debug.i("event:${MotionEvent.actionToString(ev.action)}")

            val child = scrollChild() ?: return fallback()

            // we need to listen to vertical scroll events only (and only to dismiss from top to bottom)
            // when we know which kind of event it is -> we send cancel to children/self and continue
            //  with dismiss

            val action = ev.action
            val x = ev.rawX
            val y = ev.rawY

            // TODO: bounce back

            if (MotionEvent.ACTION_DOWN == action) {

                // if scroll-child can scroll up, then no matter what is going
                //  to be next -> we are not going to dismiss
                if (child.canScrollUp()) {
                    return fallback()
                }

                // start new
//                scroller.abortAnimation()

                touchStartX = x
                touchStartY = y

                isTrackingTouch = true
                isScrollEvent = false

                @Suppress("KotlinConstantConditions", "SimplifyBooleanWithConstants")
                return super.dispatchTouchEvent(ev) || true

            }

            // if we already do not track it -> fallback
            if (!isTrackingTouch) return fallback()

//            val detectorResult = detector.onTouchEvent(ev)

//            removeCallbacks(scrollRunnable)
//            post(scrollRunnable)

            // here we also need to know if we already process the touch in logic or still evaluating it
            if (action == MotionEvent.ACTION_MOVE) {

                // already detected
                if (isScrollEvent) {
                    val startY = touchStartY ?: return fallback()
                    val value = y - startY

                    // do not stop tracking touch scroll events, just limit the
                    //  travel of the component
                    process(if (value < 0F) 0F else value)

                    return true
                }

                // if we have no required data -> fallback
                val startX = touchStartX ?: return fallback()
                val startY = touchStartY ?: return fallback()

                val diffX = abs(x - startX)
                val diffY = y - startY

                // if horizontal scroll has greater value -> ignore next events
                if (diffX > touchSlop) return fallback()
                // scrolling to bottom
                if (diffY < 0) return fallback()

                isScrollEvent = diffY >= touchSlop

                // after we have started tracking scroll event we also need to track
                //  fling gesture
                if (isScrollEvent) {
                    // okay, if it is scroll event, then cancel to children event
                    //  (they should not longer receive it)
                    val event = MotionEvent.obtain(ev)
                    try {
                        event.action = MotionEvent.ACTION_CANCEL
                        super.dispatchTouchEvent(event)
                    } finally {
                        event.recycle()
                    }

                    return true
                }

            } else if (action == MotionEvent.ACTION_CANCEL) {
                return fallback()
            } else if (action == MotionEvent.ACTION_UP) {
                // eval current state
                val isDismiss = translationY > (height / 2)
                val target = if (isDismiss) height.toFloat() else 0F
                clearAnimation()
                animate()
                    .translationY(target)
                    .alpha(if (isDismiss) 0F else 1F)
                    .setDuration(250L)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }

            // return super OR true if tracking touch here
            // in case of `isScrollEvent` always return true (continue receiving it)
            return result
        }

        private fun scrollChild(): View? {
            // take first child if present
            return childCount
                .takeIf { it > 0 }
                ?.let { getChildAt(0) }
        }

        private fun View.canScrollUp(): Boolean = canScrollVertically(-1)

        private fun process(scrollY: Float) {
            Debug.i("scrollY:$scrollY")
            translationY = scrollY
            alpha = 1F - (scrollY / height)
        }

//        private val scrollRunnable: Runnable = object: Runnable {
//            override fun run() {
//                if (scroller.computeScrollOffset()) {
//                    Debug.i("scroll currY:${scroller.currY}")
//                    process(scroller.currY.toFloat())
//                    post(this)
//                }
//            }
//        }

//        private class TouchDetector(
//            view: ModalLayout,
//            private val scroller: Scroller
//        ) {
//            fun onTouchEvent(ev: MotionEvent) = detector.onTouchEvent(ev)
//
//            private val minFlingVelocity: Int
//            private val touchSlop: Int
//
//            init {
//                val configuration = ViewConfiguration.get(view.context)
//                minFlingVelocity = configuration.scaledMinimumFlingVelocity
//                touchSlop = configuration.scaledTouchSlop
//            }
//
//            private val detector = GestureDetector(view.context, object: ExploreScrollSimpleListener() {
//
////                override fun onScroll(
////                    e1: MotionEvent?,
////                    e2: MotionEvent?,
////                    distanceX: Float,
////                    distanceY: Float
////                ): Boolean {
////                    return false
////                }
//
//                override fun onFling(
////                    e1: MotionEvent,
////                    e2: MotionEvent,
//                    velocityX: Float,
//                    velocityY: Float
//                ): Boolean {
//
//                    // if small velocity or velocity x is greater than y
//                    if (abs(velocityY) < minFlingVelocity || abs(velocityX) > abs(velocityY)) {
//                        return false
//                    }
//
////                    val height = view.height.takeIf { it > 0 } ?: return false
//
//                    val velocity = -velocityY.roundToInt()
//
//                    scroller.fling(
//                        0,
//                        view.scrollY,
//                        0,
//                        velocity,
//                        0,
//                        0,
//                        0,
//                        Int.MAX_VALUE
//                    )
//
//                    return scroller.computeScrollOffset().also {
//                        Debug.i("compute:$it final-y:${scroller.finalY}")
//                    }
//                }
//            })
//        }
    }

    @Suppress("FunctionName")
    fun <LP : LayoutParams> ViewFactory<LP>.Modal(
        children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
    ) = ElementGroup(
        provider = { ModalLayout(it) },
        children = children
    )
}

private class PreviewExploreScrollIfScrolls(context: Context, attrs: AttributeSet?) :
    AdaptUIPreviewLayout(context, attrs) {

    lateinit var text: TextView

    override fun ViewFactory<LayoutParams>.body() {
        Modal {

            VStackContentMeasuredLast {

                HStack {
                    Image(R.drawable.ic_arrow_back_ios_24_white)
                        .layout(56, 56)
                }.layout(FILL, 56)
                    .backgroundColor { accent }

                ZStackWrapHeightOrScroll(
                    scrollStyle = {
                        it.scrollBarsEnabled()
                    }
                ) {
                    Text("Content, click me to generate new")
                        .layout(FILL, WRAP)
                        // this one is going to be erased after wrap/unwrap
                        .layoutGravity { center.vertical }
                        .textSize { 17 }
                        .padding(top = 24, bottom = 36)
                        .padding(horizontal = 16)
                        .backgroundColor { accent.withAlphaComponent(0.2F) }
                        .reference(::text)
                }.stackContentMeasureLast()

                Text("Click me!")
                    .layout(FILL, WRAP)
                    .textSize(17)
                    .textGravity { center.horizontal }
                    .padding(12)
                    .background {
                        RoundedRectangle(12) {
                            fill { accent }
                        }
                    }
                    .foregroundDefaultSelectable()
                    .clipToOutline()
                    .layoutMargin(16)
                    .onClick {
                        // from 0 to 100
                        val count = (20 * Math.random()).roundToInt()
                        val base =
                            "This is the text to replicate, let it take some place, some amount of space we need to cover."
                        val result = (0 until count)
                            .joinToString("\n\n") { "[$it/$count] $base" }
                        TransitionManager.beginDelayedTransition(text.parent.parent as ViewGroup)
                        text.text = result
                    }
            }.indent()
                .layoutGravity { center.bottom }
                .backgroundColor { accent.withAlphaComponent(0.3F) }
                .layoutMargin(top = 128)
                .layoutMargin(horizontal = 16)
                .transitionGroup()

        }.layoutFill()
            .overlayDrawBounds()
    }
}