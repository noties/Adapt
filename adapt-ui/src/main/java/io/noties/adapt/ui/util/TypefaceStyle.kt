package io.noties.adapt.ui.util

import android.graphics.Typeface

open class TypefaceStyle(val value: Int) {
    companion object {
        val normal = TypefaceStyle(Typeface.NORMAL)
        val bold = Bold()
        val italic = Italic()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TypefaceStyle) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value
    }

    override fun toString(): String {
        val name = when (value) {
            Typeface.BOLD_ITALIC -> "BOLD_ITALIC"
            Typeface.BOLD -> "BOLD"
            Typeface.ITALIC -> "ITALIC"
            Typeface.NORMAL -> "NORMAL"
            else -> "<unknown>"
        }
        return "TypefaceStyle($value=\"$name\")"
    }

    class Bold : TypefaceStyle(Typeface.BOLD) {
        val italic = TypefaceStyle(Typeface.BOLD_ITALIC)
    }

    class Italic : TypefaceStyle(Typeface.ITALIC) {
        val bold = TypefaceStyle(Typeface.BOLD_ITALIC)
    }
}