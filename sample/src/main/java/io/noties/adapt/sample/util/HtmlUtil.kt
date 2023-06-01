package io.noties.adapt.sample.util

import android.os.Build
import android.text.Html

object HtmlUtil {
    fun fromHtml(text: String): CharSequence {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(text)
        }
    }
}

fun String.html() = HtmlUtil.fromHtml(this)