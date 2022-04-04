package io.noties.adapt.sample.util

import java.text.Normalizer
import java.util.*

fun String.normalized(): String {
    return Normalizer
        .normalize(this, Normalizer.Form.NFKD)
        .replace(Regex("[\\p{InCombiningDiacriticalMarks}]"), "")
        .lowercase(Locale.ROOT)
}