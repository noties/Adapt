package io.noties.adapt.sample.explore

import android.text.Spannable
import android.text.SpannableStringBuilder

object ExploreSpannableBuilder {

    class SpannableBuilder(val builder: SpannableStringBuilder) {

        private class InternalString(
            val string: String,
            val spans: MutableList<Any> = mutableListOf()
        )

        private val internal = mutableListOf<InternalString>()

        // not the best, possible string duplicates
        fun String.span(who: Any, flag: Int = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE): String {
            return this
        }

        fun String.bold(): String {
            return TODO()
        }
    }

    fun build(builder: SpannableBuilder.() -> Unit): SpannableStringBuilder {
        val ssb = SpannableStringBuilder()
        val b = SpannableBuilder(ssb)
        builder(b)
        return ssb
    }

    fun hey() {
        val text = build {
            "hello"
                .span(1)
                .span(2)
                .bold()
        }
    }
}