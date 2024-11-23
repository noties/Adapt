package io.noties.adapt.sample.ui.color

import android.graphics.Color
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
val Colors.magenta get() = Color.MAGENTA
val Colors.blue get() = Color.BLUE
val Colors.cyan get() = Color.CYAN
val Colors.gray get() = Color.GRAY
val Colors.red get() = Color.RED
val Colors.green get() = Color.GREEN

val Colors.text get() = black
val Colors.textSecondary get() = text.withAlphaComponent(0.62F)
val Colors.textDisabled get() = text.withAlphaComponent(0.33F)

val Colors.steelBlue get() = res(R.color.steelBlue)
val Colors.naplesYellow get() = res(R.color.naplesYellow)
val Colors.salmonRed get() = res(R.color.salmon)
val Colors.emeraldGreen get() = res(R.color.emerald)
val Colors.purpureus get() = res(R.color.purpureus)

val Colors.background get() = white
val Colors.backgroundSecondary get() = hex("#eee")