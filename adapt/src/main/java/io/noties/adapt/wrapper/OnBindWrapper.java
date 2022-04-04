package io.noties.adapt.wrapper;

import androidx.annotation.NonNull;

import io.noties.adapt.Item;

/**
 * @since $UNRELEASED;
 */
public class OnBindWrapper extends ItemWrapper {

    public interface OnBind {
        void onBind(@NonNull Item.Holder holder);
    }

    @NonNull
    public static Wrapper init(@NonNull OnBind onBind) {
        return item -> new OnBindWrapper(item, onBind);
    }

    private final OnBind onBind;

    public OnBindWrapper(@NonNull Item<?> item, @NonNull OnBind onBind) {
        super(item);
        this.onBind = onBind;
    }

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);

        onBind.onBind(holder);
    }
}
