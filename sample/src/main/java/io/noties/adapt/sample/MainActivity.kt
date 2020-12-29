package io.noties.adapt.sample

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ViewAnimator
import io.noties.adapt.sample.samples.*

class MainActivity : Activity() {

    private val samples: List<Sample> = listOf(
        ViewPager2Sample().sample,
        AlertDialogSample().sample,
        ListViewSomeEnabledSample().sample,
        ListViewAllEnabledSample().sample,
        ViewGroupTransitionSample().sample,
        RecyclerViewNestedSample().sample,
        RecyclerViewGridSample().sample,
        RecyclerViewDiffSample().sample,
        RecyclerViewStickySample().sample,
        ViewGroupSample().sample,
        ListViewSample().sample,
        RecyclerViewSample().sample,
    )

    private lateinit var viewAnimator: ViewAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewAnimator = findViewById(R.id.view_animator)

        val sampleViewList = SampleViewList(samples) { sample ->
            val sampleView = sample.provider()
            viewAnimator.showNext(sampleView.view(viewAnimator))
        }

        viewAnimator.addView(sampleViewList.view(viewAnimator))
    }

    override fun onBackPressed() {
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
}