package io.noties.adapt.ui

interface ViewFactoryConstants {
    @Deprecated("Use lowercase version `fill`", replaceWith = ReplaceWith("fill"))
    @Suppress("PropertyName")
    val FILL: Int
        get() {
            return LayoutParams.MATCH_PARENT
        }

    @Deprecated("Use lowercase version `wrap`", replaceWith = ReplaceWith("wrap"))
    @Suppress("PropertyName")
    val WRAP: Int
        get() {
            return LayoutParams.WRAP_CONTENT
        }

    val fill: Int
        get() {
            return LayoutParams.MATCH_PARENT
        }

    val wrap: Int
        get() {
            return LayoutParams.WRAP_CONTENT
        }

    companion object : ViewFactoryConstants
}