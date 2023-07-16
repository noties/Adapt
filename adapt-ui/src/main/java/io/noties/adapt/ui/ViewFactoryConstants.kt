package io.noties.adapt.ui

interface ViewFactoryConstants {
    @Suppress("PropertyName")
    val FILL: Int
        get() {
            return LayoutParams.MATCH_PARENT
        }

    @Suppress("PropertyName")
    val WRAP: Int
        get() {
            return LayoutParams.WRAP_CONTENT
        }

    companion object Impl: ViewFactoryConstants
}