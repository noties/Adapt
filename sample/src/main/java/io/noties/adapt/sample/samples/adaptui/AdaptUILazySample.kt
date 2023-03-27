package io.noties.adapt.sample.samples.adaptui

import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.HScroll
import io.noties.adapt.ui.element.HScrollStack
import io.noties.adapt.ui.element.Lazy
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScrollStack
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.lazyInject
import io.noties.adapt.ui.element.scrollFillViewPort
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutWeight
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230116182927",
    title = "AdaptUI, Lazy",
    description = "ViewStub-like behaviour in android-ui",
    tags = ["adapt-ui"]
)
class AdaptUILazySample : SampleView() {
    override val layoutResId: Int = R.layout.view_sample_frame

    override fun render(view: View) {
        // STOPSHIP:
        if (true) {
            ViewFactory.addChildren(view as ViewGroup) {
//                HScrollStack {
//                    for (i in 0 until 100) {
//                        Text(i.toString())
//                            .layoutWrap()
//                            .padding(8)
//                            .background(ItemGenerator.nextColor())
//                    }
//                }.layout(FILL, 96)
                VScrollStack {

                    for (i in 0 until 5) {
                        Text(i.toString())
                            .padding(8)
                            .textGravity(Gravity.center)
                            .background(ItemGenerator.nextColor())
                    }

                    View()
                        .layout(FILL, 0)
                        .layoutWeight(1F)

                    Text("Bottom")

                }.layoutFill()
                    .scrollFillViewPort()
            }
            return
        }

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