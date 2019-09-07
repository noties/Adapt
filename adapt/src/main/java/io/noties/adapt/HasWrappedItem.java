package io.noties.adapt;

import androidx.annotation.NonNull;

/**
 * A common interface for item wrappers.
 *
 * @see ItemWrapper
 * @see ItemLayoutWrapper
 * @since 2.2.0-SNAPSHOT
 */
public interface HasWrappedItem<H extends Item.Holder> {

    @NonNull
    Item<H> item();
}
