package io.noties.adapt;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Common interface for {@link Item} that is a group and holds other items.
 *
 * @since 2.3.0-SNAPSHOT
 */
public interface HasChildrenItems {

    @NonNull
    List<Item> getChildren();

    /**
     * Update underlying children items. Please note that this method does not trigger
     * an update notification.
     */
    void setChildren(@NonNull List<Item> children);
}
