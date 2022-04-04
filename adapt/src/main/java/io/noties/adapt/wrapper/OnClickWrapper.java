package io.noties.adapt.wrapper;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import io.noties.adapt.Item;

/**
 * @since $UNRELEASED;
 */
public class OnClickWrapper extends ItemWrapper {

    public interface Callbacks {
        void onClick(@NonNull Item<?> item);
    }

    @NonNull
    @CheckResult
    public static WrapperBuilder init(@NonNull Callbacks callbacks) {
        return item -> new OnClickWrapper(item, callbacks);
    }

    private final Callbacks callbacks;

    public OnClickWrapper(@NonNull Item<?> item, @NonNull Callbacks callbacks) {
        super(item);
        this.callbacks = callbacks;
    }

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);

        holder.itemView()
                .setOnClickListener(v -> callbacks.onClick(this));
    }
}
