package io.noties.adapt.sample.ui.text

import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.ui.app.text.TextStyles
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize

val TextStyles.body get() = textStyle {
    it
        .textSize { body }
        .textColor { text }
}