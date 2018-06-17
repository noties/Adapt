package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.view.View;

public abstract class OnLongClickViewProcessor<T> implements ViewProcessor<T> {

    @NonNull
    public static <T> OnLongClickViewProcessor<T> create(@NonNull final OnLongClick<T> onLongClick) {
        return new OnLongClickViewProcessor<T>() {
            @Override
            public boolean onLongClick(@NonNull T item, @NonNull View view) {
                return onLongClick.onLongClick(item, view);
            }
        };
    }

    public interface OnLongClick<T> {
        boolean onLongClick(@NonNull T item, @NonNull View view);
    }

    public abstract boolean onLongClick(@NonNull T item, @NonNull View view);

    @Override
    public void process(@NonNull final T item, @NonNull View view) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return OnLongClickViewProcessor.this.onLongClick(item, v);
            }
        });
    }
}
