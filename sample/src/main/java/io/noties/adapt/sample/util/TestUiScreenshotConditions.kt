package io.noties.adapt.sample.util

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import io.noties.adapt.sample.ui.color.green
import io.noties.adapt.sample.ui.color.red
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.isRunningScreenshotTests
import io.noties.adapt.sample.ui.text.body
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.adaptViewGroup
import io.noties.adapt.ui.alpha
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.element.textTypeface
import io.noties.adapt.ui.flex.Flex
import io.noties.adapt.ui.flex.flexGap
import io.noties.adapt.ui.flex.flexWrap
import io.noties.adapt.ui.item.ItemTypeFactory
import io.noties.adapt.ui.item.build
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.setItems
import io.noties.adapt.ui.util.isInPreview

class TestUiCondition(val name: String, value: () -> Boolean) {
    val value = value()

    override fun toString(): String {
        return "TestUiCondition(name='$name', value=$value)"
    }
}

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.TestUiScreenshotConditions(
    showInPreview: Boolean = true,
    vararg conditions: TestUiCondition
) {
    if ((showInPreview && isInPreview) || isRunningScreenshotTests) {
        Flex {}
            .layout(fill, wrap)
            .flexWrap()
            .flexGap(8)
            .alpha(0.5F)
            .adaptViewGroup()
            .setItems(
                conditions.map(ConditionItem)
            )
    }
}

private val ConditionItem = ItemTypeFactory.builder()
    .input(TestUiCondition::class)
    .idHashInput()
    .ref {
        class Ref {
            lateinit var textView: TextView
        }
        Ref()
    }
    .view {
        Text()
            .reference(it::textView)
            .layoutWrap()
            .textSize { body }
            .textColor { text }
            .textTypeface(Typeface.MONOSPACE)
    }
    .bind {
        val span = SpannableStringBuilder("${it.name}:■")
        span.setSpan(
            ForegroundColorSpan(
                if (it.value) Colors.green else Colors.red
            ),
            span.length - 1,
            span.length,
            SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ref.textView.text = span
    }
    .build()