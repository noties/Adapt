package io.noties.adapt.sample

import io.noties.adapt.Item
import io.noties.adapt.sample.items.CardBigItem
import io.noties.adapt.sample.items.CardItem
import io.noties.adapt.sample.items.PlainItem
import kotlin.random.Random

object ItemGenerator {
    private const val seed = 21L
    private var random = Random(seed)
    private val colors = arrayOf(
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
        0xFF5D4037,
    )
    private val types = Type.values()

    fun reset() {
        random = Random(seed)
    }

    fun next(current: Int): List<Item<*>> {
        val count = random.nextInt(1, 10)
        return (0 until count)
            .map { current + it }
            .map(this::nextItem)
    }

    private fun nextItem(i: Int): Item<*> {
        val type = types[random.nextInt(types.size)]
        val letter: String = "${('A' + random.nextInt(26))}"
        val color = colors[random.nextInt(colors.size)].toInt()

        fun title(prefix: String) = "$prefix Item#$i"

        return when (type) {
            Type.PLAIN -> PlainItem(letter, color, title("Plain"))
            Type.CARD -> CardItem(letter, color, title("Card"))
            Type.BIG_CARD -> CardBigItem(letter, color, title("Big Card"))
        }
    }

    private enum class Type {
        PLAIN,
        CARD,
        BIG_CARD
    }
}