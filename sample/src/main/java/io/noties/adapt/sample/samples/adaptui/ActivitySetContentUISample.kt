package io.noties.adapt.sample.samples.adaptui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.widget.TextView
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.textAllCaps
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.util.setContentUI

@AdaptSample(
    id = "20240326215919",
    title = "Activity.setContentUI",
    description = "Helper function to set <tt>Activity's</tt> content via <tt>AdaptUI</tt>",
    tags = [Tags.adaptUi, Tags.util]
)
class ActivitySetContentUISample : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        ZStack {
            Text("Click me to open activity")
                .layoutGravity { center }
                .layoutWrap()
                .layoutMargin(16)
                .padding(horizontal = 12, vertical = 8)
                .background {
                    Capsule { fill { accent } }
                }
                .textSize { 17 }
                .textColor { white }
                .textAllCaps()
                .onClick {
                    val intent = Intent(context, ActivitySetContentUISampleActivity::class.java)
                    context.startActivity(intent)
                }
                .foregroundDefaultSelectable()
                .clipToOutline()
        }.layoutFill()
    }
}

class ActivitySetContentUISampleActivity : Activity() {

    lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentUI {
            // by default will FILL parent
            ZStack {
                Text("Hello here!")
                    .layoutWrap()
                    .reference(::text)
                    .textColor { white }
                    .textSize { 24 }
                    .layoutGravity { center }
            }.backgroundColor { black }
        }
    }
}

@Preview
private class PreviewActivitySetContentUISample(context: Context, attrs: AttributeSet?) :
    PreviewSampleView(context, attrs) {
    override val sampleView
        get() = ActivitySetContentUISample()
}