package io.noties.adapt.sample.explore

import android.annotation.SuppressLint
import android.content.Context
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.explore.ExploreOverlayDrawBounds.overlayDrawBounds
import io.noties.adapt.sample.explore.ExploreScrollIfScrolls.VStackContentMeasuredLast
import io.noties.adapt.sample.explore.ExploreScrollIfScrolls.WrapContentOrScroll
import io.noties.adapt.sample.explore.ExploreScrollIfScrolls.stackContentMeasureLast
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.util.children
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.ElementGroup
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.ZStack
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
import kotlin.math.roundToInt

// actually, what we really want is to have a wrap-content view,
//  but which might scroll if content exceeds available height.
//  The question now is: how to determine available height?
//  1. what if we additionally measure parent to understand its maximum height? then how to fallback?
object ExploreScrollIfScrolls {

    @Suppress("FunctionName")
    fun <V : View, LP : LayoutParams> ViewFactory<LP>.WrapContentOrScroll(
        scrollStyle: (ViewElement<ScrollView, FrameLayout.LayoutParams>) -> Unit = {},
        child: ViewFactory<FrameLayout.LayoutParams>.() -> ViewElement<V, FrameLayout.LayoutParams>
    ) = Element(
        provider = { WrapHeightOrScroll(it, scrollStyle, child) }
    )

    @SuppressLint("ViewConstructor")
    class WrapHeightOrScroll(
        context: Context,
        private val scrollStyle: (ViewElement<ScrollView, LayoutParams>) -> Unit,
        child: ViewFactory<LayoutParams>.() -> ViewElement<out View, LayoutParams>
    ) : FrameLayout(context) {

        private val content: View

        private var callbacks: Runnable? = null

        init {
            content = ViewFactory<LayoutParams>(
                context,
                this
            ).let { child.invoke(it) }
                .also { it.init(context) }
                .also {
                    if (it.view.layoutParams == null) {
                        it.view.layoutParams =
                            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    }
                }
                .also { it.render() }
                .also {
                    addView(it.view)
                }
                .view
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = MeasureSpec.getSize(heightMeasureSpec)
            Debug.i("height:$height spec:${MeasureSpec.toString(heightMeasureSpec)}")

            val child = content

            // unspecified and without padding (yet)!
            val childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            child.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                childHeightSpec
            )

            val childMeasuredHeight = child.measuredHeight

            // TODO: do we need to adjust for padding in order to properly layout child?
            val isOverflow = childMeasuredHeight > (height - paddingTop - paddingBottom)
            Debug.i("child.measured:${childMeasuredHeight} isOverflow:$isOverflow")

            if (child.parent != this) {
                // additionally measure wrapper content
                getChildAt(0).measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                )
            }

            if (!isOverflow) {
                setMeasuredDimension(
                    width,
                    childMeasuredHeight + paddingTop + paddingBottom
                )

                unwrapContentFromScrollView()

            } else {
                setMeasuredDimension(
                    width,
                    height
                )

                wrapContentInScrollView(height)
            }
        }

        private fun wrapContentInScrollView(height: Int) {
            removeWrapUnwrapCallbacks()

            Debug.i("parent.willWrap:${content.parent == this}")

            // cannot do anything until parent is present
            val parent = content.parent as? ViewGroup ?: return
            if (parent != this) {
                // assume already wrapped
                return
            }

            postWrapUnwrapCallbacks {
                // remove from this layout
                removeAllViews()

                val view = ViewFactory.newView(this)
                    .layoutParams(LayoutParams(LayoutParams.MATCH_PARENT, height))
                    .create {
                        VScroll {
                            VStack {
                                Element { content }
                            }
                        }.indent()
                            .also(scrollStyle)
                    }

                Debug.i("view:$view")

                addView(view)
            }
        }

        private fun unwrapContentFromScrollView() {
            removeWrapUnwrapCallbacks()

            Debug.i("parent.willUnwrap:${content.parent != this}")

            val parent = content.parent as? ViewGroup ?: return
            if (parent == this) {
                // assume already unwrapped
                return
            }

            postWrapUnwrapCallbacks {
                parent.removeAllViews()

                removeAllViews()

                addView(content)
            }
        }

        private fun removeWrapUnwrapCallbacks() {
            callbacks?.also { removeCallbacks(it) }
        }

        private fun postWrapUnwrapCallbacks(callbacks: Runnable) {
            this.callbacks = callbacks
            post(callbacks)
        }

        override fun canScrollVertically(direction: Int): Boolean {
            // if contains scroll-view -> pass to it, else false
            val scrollView = getChildAt(0) as? ScrollView ?: return false
            return scrollView.canScrollVertically(direction)
        }
    }

    @Suppress("FunctionName")
    fun <LP : LayoutParams> ViewFactory<LP>.VStackContentMeasuredLast(
        children: ViewFactory<ContentHeightIsMeasuredLast.LayoutParams>.() -> Unit
    ) = ElementGroup(
        provider = { ContentHeightIsMeasuredLast(it) },
        children = children
    )

    fun <V : View, @Suppress("FINAL_UPPER_BOUND") LP : ContentHeightIsMeasuredLast.LayoutParams> ViewElement<V, LP>.stackContentMeasureLast() =
        onLayoutParams {
            it.isContent = true
        }

    // no padding?
    class ContentHeightIsMeasuredLast(context: Context) : LinearLayout(context) {

        init {
            orientation = VERTICAL
        }

        class LayoutParams : LinearLayout.LayoutParams {
            var isContent: Boolean = false

            constructor(c: Context, attrs: AttributeSet?) : super(c, attrs)
            constructor(width: Int, height: Int) : super(width, height)
            constructor(width: Int, height: Int, weight: Float) : super(width, height, weight)
            constructor(p: ViewGroup.LayoutParams) : super(p)
            constructor(source: MarginLayoutParams) : super(source)
            constructor(source: LinearLayout.LayoutParams) : super(source)
        }

        private companion object {
            val View.isContentView: Boolean get() {
                return true == (this.layoutParams as? LayoutParams)?.isContent
            }
        }

        override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
            return LayoutParams(context, attrs)
        }

        override fun generateDefaultLayoutParams(): LayoutParams {
            return LayoutParams(
                io.noties.adapt.ui.LayoutParams.MATCH_PARENT,
                io.noties.adapt.ui.LayoutParams.WRAP_CONTENT
            )
        }

        override fun generateLayoutParams(lp: ViewGroup.LayoutParams): LayoutParams {
            return LayoutParams(lp)
        }

        override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
            return p is LayoutParams
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            // if content is not found -> just super

            val (contentChildren, children) = children
                .partition { it.isContentView }

            if (contentChildren.isEmpty()) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }

//            val content =
//                children.firstOrNull { true == (it.layoutParams as? LayoutParams)?.isContent }
//            if (content == null) {
//                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//                return
//            }

            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = MeasureSpec.getSize(heightMeasureSpec)

            var availableHeight = height - paddingTop - paddingBottom

            fun measureChild(child: View) {
                measureChild(
                    child,
                    childWidthMeasureSpec(width, child),
                    childHeightMeasureSpec(
                        availableHeight - child.heightLayoutMargins()
                    )
                )
                availableHeight -= (child.measuredHeight + child.heightLayoutMargins())
            }

            children.forEach { measureChild(it) }
            contentChildren.forEach { measureChild(it) }

//            children
//                .withIndex()
//                .sortedWith(ChildrenContentComparator)
//                .map { getChildAt(it.index) }
//                .forEach { child ->
//                    measureChild(
//                        child,
//                        childWidthMeasureSpec(width, child),
//                        childHeightMeasureSpec(
//                            availableHeight - child.heightLayoutMargins()
//                        )
//                    )
//                    availableHeight -= (child.measuredHeight + child.heightLayoutMargins())
//                }

//            for (child in children) {
//                if (true == (child.layoutParams as? LayoutParams)?.isContent) {
//                    continue
//                }
//                measureChild(
//                    child,
//                    childWidthMeasureSpec(width, child),
//                    childHeightMeasureSpec(
//                        availableHeight - child.heightLayoutMargins()
//                    )
////                    MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.AT_MOST)
//                )
////                child.measure(
////                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
////                    MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.AT_MOST)
////                )
//
//                availableHeight -= (child.measuredHeight + child.heightLayoutMargins())
//            }

            // maybe just sort them and process in a single loop?
//            content.measure(
//                childWidthMeasureSpec(width, content),
//                childHeightMeasureSpec(
//                    availableHeight - content.heightLayoutMargins()
//                )
//            )
//            availableHeight -= (content.measuredHeight + content.heightLayoutMargins())

            // okay here:
            //  do we just use maximum dimensions
            //  or do we check if wrap-content is requested and then use the height used by the children?

            setMeasuredDimension(
                width,
                height - availableHeight
            )
        }

        private fun childWidthMeasureSpec(parentWidth: Int, child: View): Int {
            return MeasureSpec.makeMeasureSpec(
                (parentWidth - paddingLeft - paddingRight - child.widthLayoutMargins()),
                MeasureSpec.EXACTLY
            )
        }

        private fun childHeightMeasureSpec(height: Int): Int {
            return MeasureSpec.makeMeasureSpec(
                height,
                MeasureSpec.EXACTLY
            )
        }

        private fun View.widthLayoutMargins(): Int {
            val lp = this.layoutParams as? LayoutParams
            return if (lp != null) {
                lp.leftMargin + lp.rightMargin
            } else 0
        }

        private fun View.heightLayoutMargins(): Int {
            val lp = this.layoutParams as? LayoutParams
            return if (lp != null) {
                lp.topMargin + lp.bottomMargin
            } else 0
        }
    }
}

private class PreviewExploreScrollIfScrolls(context: Context, attrs: AttributeSet?) :
    AdaptUIPreviewLayout(context, attrs) {

    lateinit var text: TextView

    override fun ViewFactory<LayoutParams>.body() {
        ZStack {
            VStackContentMeasuredLast {

                HStack {
                    Image(R.drawable.ic_arrow_back_ios_24_white)
                        .layout(56, 56)
                }.layout(FILL, 56)
                    .backgroundColor { accent }

                WrapContentOrScroll(
                    scrollStyle = {
                        it.scrollBarsEnabled(false)
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