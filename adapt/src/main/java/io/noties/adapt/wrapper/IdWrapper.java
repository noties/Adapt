package io.noties.adapt.wrapper;

import androidx.annotation.NonNull;

import io.noties.adapt.Item;

/**
 * @since 4.0.0
 */
public class IdWrapper extends ItemWrapper {

    @NonNull
    public static WrapperBuilder init(long id) {
        return item -> new IdWrapper(item, id);
    }

    public IdWrapper(@NonNull Item<?> item, long id) {
        super(item, id);
    }
}
