package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
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
import io.noties.adapt.ui.shape.Capsule

@AdaptSample(
    id = "20240326215919",
    title = "Activity.setContentUI",
    description = "Helper function to set <tt>Activity's</tt> content via <tt>AdaptUI</tt>",
    tags = ["adapt-ui", "util"]
)
class ActivitySetContentUISample : AdaptUISampleView() {
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

private class PreviewActivitySetContentUISample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = ActivitySetContentUISample()
}