package io.noties.adapt.ui.widget.grid

data class GridOverlayInfo(val columns: Int, val rows: Int)

typealias GridOverlaySpanBuilder = GridOverlaySpanFactory.(info: GridOverlayInfo) -> IntRange

interface GridOverlaySpanFactory {
    companion object: GridOverlaySpanFactory {
        @Suppress("EmptyRange")
        private val _skip: IntRange = 0 until 0
        private val _fill: IntRange = 0 until Int.MAX_VALUE
        private val _first: IntRange = 0 until 1
    }

    val skip: IntRange get() = _skip

    // fill and first are specifically not properties, first they might collide with other - ViewFactory.fill
    //  and secondly, it makes the only `skip` distinguishable and brings attention to it
    fun fill(): IntRange = _fill

    fun first(): IntRange = _first

    fun just(value: Int): IntRange = value until (value + 1)

    fun last(count: Int): IntRange = (count - 1) until count

    /**
     * Moves forward receiver int-range by 1
     * ```kotlin
     *
     * (0 until 1) + 1 // => (1 until 2)
     * (10 ... 20) + 1 // => (11 ... 21)
     *
     * // can combine with helper ranges:
     * first() + 1 // => (1 until 2)
     * just(2) + 1 // => (3 until 4)
     * ```
     */
    operator fun IntRange.plus(value: Int) = IntRange(start + value, endInclusive + value)

    /**
     * Moves backward received int-range by 1
     * ```kotlin
     * (1 until 2) - 1 // => (0 until 1)
     * (11 ... 21) - 1 // => (10 ... 20)
     *
     * // can combine with helper ranges:
     * last(5) - 1 // => (3 until 4)
     * just(1) - 1 // => (0 until 1)
     * ```
     */
    operator fun IntRange.minus(value: Int) = IntRange(start - value, endInclusive - value)
}