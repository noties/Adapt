package io.noties.adapt.sample

import android.app.Activity
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.noties.adapt.sample.ui.color.background
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.sample.ui.dimen.appBarHeight
import io.noties.adapt.sample.util.KeyboardUtil
import io.noties.adapt.sample.util.SampleUtil
import io.noties.adapt.ui.app.dimen.Dimens
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundGradient
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.focusable
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.requestFocus
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.ShapeDrawable
import io.noties.adapt.ui.util.onFocusChanged
import io.noties.adapt.ui.util.onPreDrawOnce
import io.noties.adapt.ui.util.setContentUI
import io.noties.debug.Debug

class MainActivity : Activity() {

    val samples = SampleUtil.samples
    val sampleViewList = SampleViewList(samples, ::showSample)

    lateinit var rootContent: ViewGroup
    lateinit var contentDivider: View
    lateinit var childContent: ViewGroup

    // when needed takes focus to ensure no keyboard is shown
    lateinit var focusView: View

    private val childContentAnimation = ChildContentAnimation()
    private lateinit var windowsInsetsControllerCompat: WindowInsetsControllerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        windowsInsetsControllerCompat = WindowInsetsControllerCompat(window, window.decorView)
        windowsInsetsControllerCompat.isAppearanceLightStatusBars = true

        setContentUI {

            ZStack {

                View()
                    .reference(::focusView)
                    .layout(1, 1)
                    .focusable(true)
                    .requestFocus()
                    .onFocusChanged { view, hasFocus ->
                        if (hasFocus) KeyboardUtil.hide(view)
                    }

                ZStack { }
                    .layoutFill()
                    .reference(::rootContent)
                    .onView {
                        val view = sampleViewList.view(it)
                        it.addView(view)
                    }

                ZStack {
                    SampleAppBar { white }
                }.indent()
                    .layoutFill()
                    .reference(::contentDivider)
                    .backgroundGradient {
                        linear {
                            edges { top.leading to bottom.trailing }
                                .setColors(
                                    hex("#D8D8D8"),
                                    hex("#E5E5EA"),
                                    hex("#F2F2F7")
                                )
                        }
                    }

                ZStack { }
                    .layoutFill()
                    .reference(::childContent)
                    .padding(top = Dimens.appBarHeight)
                    .background {
                        Rectangle {
                            RoundedRectangle(12) {
                                fill { background }
                                padding(top = Dimens.appBarHeight)
                            }
                        }
                    }
                    .onView {
                        childContentAnimation.applyInitial(it)
                    }
                    .onView {
                        // consume all touch events by setting click listener (when needed)
                        it.isSoundEffectsEnabled = false
                    }

            }.layoutFill()
        }

        if (savedInstanceState == null) {
            val aid = intent.getStringExtra(LAUNCH_SAMPLE_ID_EXTRA_KEY)
                .takeIf { !it.isNullOrBlank() }
            if (aid != null) {
                val sample = samples.firstOrNull { aid == it.id }
                    ?: error("Sample with id `$aid` is not found")
                showSample(sample)
            }
        }
    }

    private fun showSample(sample: Sample) {
        Debug.i(sample)

        focusView.requestFocus()

        windowsInsetsControllerCompat.isAppearanceLightStatusBars = false

        childContent.isClickable = true
        childContent.setOnClickListener { }

        childContentAnimation.show(
            sample = sample,
            contentDivider = contentDivider,
            childContainer = childContent
        )
    }

    private fun hideSample(): Boolean {

        windowsInsetsControllerCompat.isAppearanceLightStatusBars = true

        childContent.setOnClickListener(null)
        childContent.isClickable = false

        childContent.childCount
            .takeIf { it > 0 }
            .also { Debug.i("count:$it") }
            ?: return false

        childContentAnimation.hide(
            contentDivider = contentDivider,
            childContainer = childContent
        )

        return true
    }

    override fun onBackPressed() {
        if (currentFocus != focusView) {
            focusView.requestFocus()
            return
        }

        // NB! the return
        if (hideSample()) {
            return
        }

        super.onBackPressed()
    }

    companion object {
        // can be used to directly launch sample from IDE,
        //  provide `--es "aid" "${ID_OF_SAMPLE}"` as `Launch Flags` for Activity
        const val LAUNCH_SAMPLE_ID_EXTRA_KEY = "aid"
    }

    private class ChildContentAnimation {
        companion object {
            const val DURATION = 250L

            const val START_SCALE = 0.5F
        }

        private var delayAction: Runnable? = null

        fun applyInitial(childContainer: ViewGroup) {
            childContainer.alpha = 0F
            childContainer.scaleX = START_SCALE
            childContainer.scaleY = START_SCALE
            childContainer.onPreDrawOnce {
                it.translationY = it.height.toFloat()
            }
        }

        fun show(
            sample: Sample,
            contentDivider: View,
            childContainer: ViewGroup
        ) {

            val sampleView = SampleUtil.createSampleView(sample)
            val childView = sampleView.createView(childContainer.context)

            cancel(
                contentDivider = contentDivider,
                childContainer = childContainer
            )

            contentDivider.background = ShapeDrawable.invoke {
                Rectangle {
                    fill(Gradient.linear {
                        edges { top.leading to bottom.trailing }
                            // .setColors(salmon, steelBlue, naplesYellow, emerald) - interesting
                            .setColors(*Sample.gradientColors(sample))
                    })
                }
            }

            contentDivider
                .animate()
                .alpha(1F)
                .setDuration(DURATION)
                .start()

            childContainer.pivotX = childContainer.width / 2F
            childContainer.pivotY = 0F

            childContainer.animate()
                .alpha(1F)
                .scaleX(1F)
                .scaleY(1F)
                .translationY(0F)
                .setDuration(DURATION)
                .start()

            delayAction = Runnable {
                TransitionManager.beginDelayedTransition(childContainer)
                childContainer.addView(childView)
            }.also { childContainer.postDelayed(it, DURATION / 2) }
        }


        fun hide(
            contentDivider: View,
            childContainer: ViewGroup
        ) {

            cancel(
                contentDivider = contentDivider,
                childContainer = childContainer
            )

            TransitionManager.beginDelayedTransition(childContainer)
            childContainer.removeAllViews()

            delayAction = Runnable {
                contentDivider.animate()
                    .alpha(0F)
                    .setDuration(DURATION)
                    .start()

                childContainer.pivotX = childContainer.width / 2F
                childContainer.pivotY = 0F

                childContainer.animate()
                    .alpha(0F)
                    .scaleX(START_SCALE)
                    .scaleY(START_SCALE)
                    .translationY(childContainer.height / 2F)
                    .setDuration(DURATION)
                    .start()
            }.also { childContainer.postDelayed(it, DURATION / 2) }
        }

        private fun cancel(
            contentDivider: View,
            childContainer: ViewGroup
        ) {
            // stop any previous
            TransitionManager.endTransitions(childContainer)
            contentDivider.clearAnimation()
            childContainer.clearAnimation()
            delayAction?.also { childContainer.removeCallbacks(it) }
        }
    }
}