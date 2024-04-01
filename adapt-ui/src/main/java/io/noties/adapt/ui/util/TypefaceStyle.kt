package io.noties.adapt.ui.util

import android.graphics.Typeface

open class TypefaceStyle(val rawValue: Int) {

    @Deprecated("Use `rawValue`", ReplaceWith("rawValue"))
    val value: Int get() = rawValue

    companion object {
        val normal = TypefaceStyle(Typeface.NORMAL)
        val bold = Bold()
        val italic = Italic()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TypefaceStyle) return false

        if (rawValue != other.rawValue) return false

        return true
    }

    override fun hashCode(): Int {
        return rawValue
    }

    override fun toString(): String {
        val name = when (rawValue) {
            Typeface.BOLD_ITALIC -> "BOLD_ITALIC"
            Typeface.BOLD -> "BOLD"
            Typeface.ITALIC -> "ITALIC"
            Typeface.NORMAL -> "NORMAL"
            else -> "<unknown>"
        }
        return "TypefaceStyle($rawValue=\"$name\")"
    }

    class Bold : TypefaceStyle(Typeface.BOLD) {
        val italic = TypefaceStyle(Typeface.BOLD_ITALIC)
    }

    class Italic : TypefaceStyle(Typeface.ITALIC) {
        val bold = TypefaceStyle(Typeface.BOLD_ITALIC)
    }
}