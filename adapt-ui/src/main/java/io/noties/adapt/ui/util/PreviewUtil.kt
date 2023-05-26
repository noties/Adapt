package io.noties.adapt.ui.util

import io.noties.adapt.Item
import io.noties.adapt.util.PreviewUtils
import io.noties.adapt.wrapper.IdWrapper

val isInPreview: Boolean get() = PreviewUtils.isInPreview()

/**
 * Intended to be used with [io.noties.adapt.preview.AdaptPreviewLayout] in layout preview mode.
 * Assigns ids to each item based on index
 */
fun <T : Item<*>, I : Iterable<T>> I.withPreviewIds() = this
    .withIndex()
    .map {
        val wrapper = IdWrapper.init(it.index.toLong())
        wrapper.build(it.value)
    }