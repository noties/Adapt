package io.noties.adapt.sample.samples.adaptui

import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Lazy
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.lazyInject
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layoutWeight
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding

@AdaptSample(
    id = "20230116182927",
    title = "AdaptUI, Lazy",
    description = "ViewStub-like behaviour in android-ui",
    tags = ["adapt-ui"]
)
class AdaptUILazySample : SampleView() {
    override val layoutResId: Int = R.layout.view_sample_frame

    override fun render(view: View) {
        ViewFactory.addChildren(view as ViewGroup) {
            VStack {

                Text("Some text at top")
                    .textSize(24)
                    .textColor(Colors.black)
                    .padding(horizontal = 16)
                    .padding(top = 24, bottom = 8)

                val lazy = Lazy {

                    // layout params of parent can be used
                    Text("layout_weight:1")
                        .background(Colors.orange)
                        .padding(16)
                        .layoutWeight(1F)

                    // multiple views can be _lazy_ -> all would be added to parent
                    Text("another layout_weight:1")
                        .background(Colors.accent)
                        .padding(16)
                        .layoutWeight(1F)
                }

                Text("Some other text, [click me] to inject lazy views")
                    .padding(16)
                    .onClick {

                        // additionally, can trigger default transition
                        (lazy.view.parent as? ViewGroup)
                            .also { TransitionManager.beginDelayedTransition(it) }

                        lazy.lazyInject()
                        // or (the same effect):
//                        lazy.visible(true)
                    }
            }
        }
    }
}