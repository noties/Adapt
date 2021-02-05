package io.noties.adapt.sample

import io.noties.adapt.Item
import io.noties.adapt.sample.items.CardBigItem
import io.noties.adapt.sample.items.CardItem
import io.noties.adapt.sample.items.PlainItem
import kotlin.random.Random

// TODO: create tags collection and assign colors manually
object ItemGenerator {
    private const val seed = 21L
    private var random = Random(seed)
    private val colors = arrayOf(
        0xFF5cb578,
        0xFFf9f871,
        0xFFf2a3c0,
        0xFF526b92,
        0xFF413550,
        0xFF863557,
        0xFF92705a
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