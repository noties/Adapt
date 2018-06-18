package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.view.View;

@SuppressWarnings("unused")
public abstract class OnClickViewProcessor<T> implements ViewProcessor<T> {

    @NonNull
    public static <T> OnClickViewProcessor<T> create(@NonNull final OnClick<T> onClick) {
        return new OnClickViewProcessor<T>() {
            @Override
            public void onClick(@NonNull T item, @NonNull View view) {
                onClick.onClick(item, view);
            }
        };
    }

    public interface OnClick<T> {
        void onClick(@NonNull T item, @NonNull View view);
    }

    public abstract void onClick(@NonNull T item, @NonNull View view);

    @Override
    public void process(@NonNull final T item, @NonNull View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                OnClickViewProcessor.this.onClick(item, v);
            }
        });
    }
}
