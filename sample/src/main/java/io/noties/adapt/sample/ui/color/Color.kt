package io.noties.adapt.sample.ui.color

import io.noties.adapt.sample.R
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.util.withAlphaComponent

val Colors.white get() = res(R.color.white)
val Colors.black get() = res(R.color.black)
val Colors.orange get() = res(R.color.orange)
val Colors.primary get() = res(R.color.primary)
val Colors.accent get() = res(R.color.accent)
val Colors.yellow get() = res(R.color.yellow)
val Colors.purple get() = res(R.color.purple)

val Colors.text get() = black
val Colors.textSecondary get() = text.withAlphaComponent(0.82F)