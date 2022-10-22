package io.noties.adapt.ui.util

import java.util.Locale

// all colors on android implicitly defined with alpha channel,
//  so `#000000` => `#FF000000` (with 255 alpha)
internal fun Int.toHexString(): String = String.format(Locale.ROOT, "#%08X", this)