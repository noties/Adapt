package io.noties.adapt.ui.util

import android.graphics.PorterDuff

interface PorterDuffModeFactory {
    /**
     * @see PorterDuff.Mode.CLEAR
     */
    val clear get() = PorterDuffMode(PorterDuff.Mode.CLEAR)

    /**
     * @see PorterDuff.Mode.SRC
     */
    val src get() = PorterDuffMode(PorterDuff.Mode.SRC)

    /**
     * @see PorterDuff.Mode.DST
     */
    val dst get() = PorterDuffMode(PorterDuff.Mode.DST)

    /**
     * @see PorterDuff.Mode.SRC_OVER
     */
    val srcOver get() = PorterDuffMode(PorterDuff.Mode.SRC_OVER)

    /**
     * @see PorterDuff.Mode.DST_OVER
     */
    val dstOver get() = PorterDuffMode(PorterDuff.Mode.DST_OVER)

    /**
     * @see PorterDuff.Mode.SRC_IN
     */
    val srcIn get() = PorterDuffMode(PorterDuff.Mode.SRC_IN)

    /**
     * @see PorterDuff.Mode.DST_IN
     */
    val dstIn get() = PorterDuffMode(PorterDuff.Mode.DST_IN)

    /**
     * @see PorterDuff.Mode.SRC_OUT
     */
    val srcOut get() = PorterDuffMode(PorterDuff.Mode.SRC_OUT)

    /**
     * @see PorterDuff.Mode.DST_OUT
     */
    val dstOut get() = PorterDuffMode(PorterDuff.Mode.DST_OUT)

    /**
     * @see PorterDuff.Mode.SRC_ATOP
     */
    val srcAtop get() = PorterDuffMode(PorterDuff.Mode.SRC_ATOP)

    /**
     * @see PorterDuff.Mode.DST_ATOP
     */
    val dstAtop get() = PorterDuffMode(PorterDuff.Mode.DST_ATOP)

    /**
     * @see PorterDuff.Mode.XOR
     */
    val xor get() = PorterDuffMode(PorterDuff.Mode.XOR)

    /**
     * @see PorterDuff.Mode.DARKEN
     */
    val darken get() = PorterDuffMode(PorterDuff.Mode.DARKEN)

    /**
     * @see PorterDuff.Mode.LIGHTEN
     */
    val lighten get() = PorterDuffMode(PorterDuff.Mode.LIGHTEN)

    /**
     * @see PorterDuff.Mode.MULTIPLY
     */
    val multiply get() = PorterDuffMode(PorterDuff.Mode.MULTIPLY)

    /**
     * @see PorterDuff.Mode.SCREEN
     */
    val screen get() = PorterDuffMode(PorterDuff.Mode.SCREEN)

    /**
     * @see PorterDuff.Mode.ADD
     */
    val add get() = PorterDuffMode(PorterDuff.Mode.ADD)

    /**
     * @see PorterDuff.Mode.OVERLAY
     */
    val overlay get() = PorterDuffMode(PorterDuff.Mode.OVERLAY)

    fun raw(mode: PorterDuff.Mode) = PorterDuffMode(mode)
}

@JvmInline
value class PorterDuffMode(val rawValue: PorterDuff.Mode) {
    companion object: PorterDuffModeFactory {
        fun build(block: PorterDuffModeBuilder): PorterDuffMode {
            return block(this)
        }
    }
}

typealias PorterDuffModeBuilder = PorterDuffModeFactory.() -> PorterDuffMode