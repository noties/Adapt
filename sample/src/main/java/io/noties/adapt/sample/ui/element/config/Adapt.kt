package io.noties.adapt.sample.ui.element.config

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.color.textDisabled
import io.noties.adapt.sample.ui.text.body
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.app.color.ColorsBuilder
import io.noties.adapt.ui.app.text.TextStyles
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textEllipsize
import io.noties.adapt.ui.element.textMaxLines
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.preview.AdaptUIPreviewLayout
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.state.textColorWithState

fun TextStyles.configLabel(
    textColor: ColorsBuilder = { text },
    textDisabledColor: ColorsBuilder = { textDisabled }
) = textStyle {
        it
            .textSize { body }
            .textMaxLines(1)
            .textEllipsize { end }
            .textColorWithState {
                enabled = textColor(Colors)
                default = textDisabledColor(Colors)
            }
    }

@Preview
private class PreviewElements(context: Context, attrs: AttributeSet?) :
    AdaptUIPreviewLayout(context, attrs) {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {
            ConfigPicker(
                title = "Some value",
                values = listOf("First", "Second"),
                onSelectedChanged = {}
            )
            ConfigPicker(
                title = "Some value",
                values = listOf("First", "Second"),
                selected = "First",
                onSelectedChanged = {}
            )
            ConfigToggle(
                title = "Some toggle",
                onCheckedChanged = {}
            )
            ConfigToggle(
                title = "Some checked toggle",
                isChecked = true,
                onCheckedChanged = {}
            )
        }.indent()
            .preview { it.previewBounds() }
    }
}