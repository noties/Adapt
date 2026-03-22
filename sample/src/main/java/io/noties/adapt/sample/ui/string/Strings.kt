package io.noties.adapt.sample.ui.string

import io.noties.adapt.sample.R
import io.noties.adapt.ui.app.string.Strings
import io.noties.adapt.ui.app.string.StringsBuilder

val Strings.configGridBordersTitle get() = res(R.string.sample_grid_borders_grid_borders_configuration_section_title)
val Strings.configGridLayoutTitle get() = res(R.string.sample_grid_borders_grid_layout_configuration_section_title)

// can create nested sections
interface StringsAccount: Strings {
    companion object: StringsAccount

    val name get() = res(0)
}
val Strings.account: StringsAccount get() = StringsAccount

private fun provideText() {
    fun hey(text: StringsBuilder): String {
        return text(Strings)
    }

    hey {
        account.name
    }
}