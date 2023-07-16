package io.noties.adapt.sample

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ViewAnimator
import io.noties.adapt.sample.ui.OnBackPressedListenerFrameLayout
import io.noties.adapt.sample.util.SampleUtil

class MainActivity : Activity() {

    private lateinit var onBackPressedListenerFrameLayout: OnBackPressedListenerFrameLayout
    private lateinit var viewAnimator: ViewAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onBackPressedListenerFrameLayout = findViewById(R.id.on_back_pressed_listener)
        viewAnimator = findViewById(R.id.view_animator)

        val samples = SampleUtil.readSamples(this)
        val sampleViewList = SampleViewList(samples, ::showSample)

        viewAnimator.addView(sampleViewList.view(viewAnimator))

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
        val sampleView = SampleUtil.createView(sample)
        viewAnimator.showNext(sampleView.view(sample, viewAnimator))
    }

    override fun onBackPressed() {
        if (onBackPressedListenerFrameLayout.onSoftBackPressed()) {
            return
        }
        if (!viewAnimator.goBack()) {
            super.onBackPressed()
        }
    }

    private fun ViewAnimator.showNext(view: View) {
        inAnimation = AnimationUtils.loadAnimation(context, R.anim.forward_in)
        outAnimation = AnimationUtils.loadAnimation(context, R.anim.forward_out)
        addView(view)
        displayedChild = 1
    }

    private fun ViewAnimator.goBack(): Boolean {
        val child = displayedChild
        if (child > 0) {
            inAnimation = AnimationUtils.loadAnimation(context, R.anim.back_in)
            outAnimation = AnimationUtils.loadAnimation(context, R.anim.back_out)
            showPrevious()
            postDelayed({ removeViewAt(child) }, outAnimation.duration)
        }
        return child >= 1
    }

    companion object {
        // can be used to directly launch sample from IDE,
        //  provide `--es "aid" "${ID_OF_SAMPLE}"` as `Launch Flags` for Activity
        const val LAUNCH_SAMPLE_ID_EXTRA_KEY = "aid"
    }
}