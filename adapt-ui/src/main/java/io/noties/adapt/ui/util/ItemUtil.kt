package io.noties.adapt.ui.util

import io.noties.adapt.Item
import io.noties.adapt.util.ItemUtils

/**
 * Assigns ids based on index in the list.
 * NB! items are changed - they are wrapped with [IdWrapper]
 */
fun <T : Item<*>> List<T>.withIndexAsItemId(): List<Item<*>> =
    ItemUtils.assignIdsAccordingToIndex(this)


fun <T: Item<*>> List<T>.divided(provider: (T) -> Item<*>): List<Item<*>> {
    // add divider first before each, then remove the very first one
    return flatMap { listOf(provider(it), it) }
        .drop(1)
}