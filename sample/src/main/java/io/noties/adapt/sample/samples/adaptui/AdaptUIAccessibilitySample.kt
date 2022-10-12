package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.PreviewLayout
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.accessibilityDescription
import io.noties.adapt.ui.accessibilityLabelFor
import io.noties.adapt.ui.accessibilityTraversalBefore
import io.noties.adapt.ui.addChildren
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Progress
import io.noties.adapt.ui.element.Spacer
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.TextInput
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.focusable
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20221009162741",
    title = "AdaptUI - Accessibility",
    tags = ["adapt-ui", "ui-accessibility"]
)
class AdaptUIAccessibilitySample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {
        ViewFactory.addChildren(view as ViewGroup) {
            VScroll {
                VStack {

                    val inputGroup = HStack {

                        // input element that would be added later
                        lateinit var input: ViewElement<out View, *>

                        Text("Your name")
                            .padding(4)
                            .layoutWrap()
                            .accessibilityLabelFor { input }

                        Spacer()

                        input = TextInput(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME)
                            .textSize(16)
                            .layoutWrap()

                    }.focusable(focusable = true, focusableInTouchMode = false)
                        .padding(horizontal = 16, vertical = 8)

                    Text("Text")
                        .accessibilityDescription("A text view")
                        .padding(16)
                        .ifAvailable(Build.VERSION_CODES.LOLLIPOP_MR1) {
                            it.accessibilityTraversalBefore { inputGroup }
                        }
                }
            }.layoutFill()
        }
    }
}

@Suppress("ClassName", "unused")
class __AdaptUIAccessibilitySample(context: Context, attributeSet: AttributeSet) :
    PreviewLayout(context, attributeSet) {
    init {
        AdaptUIAccessibilitySample().render(this)
    }
}