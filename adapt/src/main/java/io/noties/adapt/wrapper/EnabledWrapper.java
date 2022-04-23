package io.noties.adapt.wrapper;

import androidx.annotation.NonNull;

import io.noties.adapt.Item;

public class EnabledWrapper extends ItemWrapper {

    @NonNull
    public static WrapperBuilder init(boolean isEnabled) {
        return item -> new EnabledWrapper(item, isEnabled);
    }

    private final boolean isEnabled;

    public EnabledWrapper(@NonNull Item<?> item, boolean isEnabled) {
        super(item);
        this.isEnabled = isEnabled;
    }

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);

        holder.itemView()
                .setEnabled(isEnabled);
    }
}
