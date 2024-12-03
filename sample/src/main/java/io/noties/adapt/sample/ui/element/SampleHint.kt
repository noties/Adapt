package io.noties.adapt.sample.ui.element

import android.widget.FrameLayout
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.color.textSecondary
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.ifCastLayout
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Label
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.util.withAlphaComponent

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.SampleHint(hint: String) = Text(hint)
    .layoutWrap()
    .textSize(16)
    .textColor { text }
    .textGravity { center.horizontal }
    .padding(16)
    .background {
        Rectangle {
            Label("[")
                .textColor { textSecondary.withAlphaComponent(0.2F) }
                .textSize { 48 }
                .textGravity { center.leading }
            Label("]")
                .textColor { textSecondary.withAlphaComponent(0.2F) }
                .textSize { 48 }
                .textGravity { center.trailing }
        }
    }
    .also {
        it.ifCastLayout(FrameLayout.LayoutParams::class) {
            it.layoutGravity { center.horizontal }
        }
    }