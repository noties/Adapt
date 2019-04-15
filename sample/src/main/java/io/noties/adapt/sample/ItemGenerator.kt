package io.noties.adapt.sample

import io.noties.adapt.Item
import io.noties.adapt.sample.items.CircleItem
import io.noties.adapt.sample.items.SquareItem
import io.noties.adapt.sample.items.TriangleItem
import java.util.concurrent.atomic.AtomicLong

class ItemGenerator {

    private val randomizer = SampleRandomizer()
    private val idGenerator = AtomicLong()

    fun generate(count: Int): List<Item<*>> {
        return List(count) {
            when (randomizer.nextShape()) {
                Shape.CIRCLE -> CircleItem(idGenerator.incrementAndGet(), randomizer.nextColor())
                Shape.SQUARE -> SquareItem(idGenerator.incrementAndGet(), randomizer.nextColor())
                Shape.TRIANGLE -> TriangleItem(idGenerator.incrementAndGet(), randomizer.nextColor())
            }
        }
    }

    fun shuffle(items: List<Item<*>>) = items.shuffled(randomizer.random)
}