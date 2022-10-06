package io.noties.adapt.ui.util

import io.noties.adapt.ui.LayoutParams

// `null` is not the same as `0`, plus if argument is not specified
//  this should not reduce to null or 0, as it means keep current selection,
//  not override it
// `Int.MIN_VALUE + 42` is a completely random value, should be considered a magic number
internal const val unused = Int.MIN_VALUE + 42
