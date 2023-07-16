package io.noties.adapt.ui.util

import io.noties.adapt.Item
import io.noties.adapt.util.ItemUtils

/**
 * Assigns ids based on index in the list.
 * NB! items are changed - they are wrapped with [IdWrapper]
 */
fun <T : Item<*>> List<T>.withIndexAsItemId(): List<Item<*>> =
    ItemUtils.assignIdsAccordingToIndex(this)