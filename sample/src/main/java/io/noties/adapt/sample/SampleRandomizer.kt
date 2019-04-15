package io.noties.adapt.sample

import kotlin.random.Random

class SampleRandomizer() {

    // totally random seed value
    val random = Random(776L)

    private val shapes = Shape.values()

    private val colors = listOf(
            0xFFD32F2F,
            0xFFC2185B,
            0xFF7B1FA2,
            0xFF512DA8,
            0xFF303F9F,
            0xFF1976D2,
            0xFF0288D1,
            0xFF0097A7,
            0xFF00796B,
            0xFF388E3C,
            0xFF689F38,
            0xFFAFB42B,
            0xFFFBC02D,
            0xFFFFA000,
            0xFFF57C00,
            0xFFE64A19,
            0xFF5D4037
    ).map { it.toInt() }.toTypedArray()

    fun nextShape(): Shape {
        return shapes[random.nextInt(shapes.size)]
    }

    fun nextColor(): Int {
        return colors[random.nextInt(colors.size)]
    }
}