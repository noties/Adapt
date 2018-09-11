package ru.noties.adapt;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * @since 1.1.0
 */
public abstract class DynamicItemView<T> extends ItemView<T, DynamicHolder> {

    public interface BindHolder<T> {
        void bindHolder(@NonNull DynamicHolder holder, @NonNull T item);
    }

    @NonNull
    public static <T> DynamicItemView create(@LayoutRes int layoutResId, @NonNull BindHolder<T> bindHolder) {
        return new Impl<>(layoutResId, bindHolder);
    }

    private final int layoutResId;

    @SuppressWarnings("WeakerAccess")
    public DynamicItemView(@LayoutRes int layoutResId) {
        this.layoutResId = layoutResId;
    }

    @NonNull
    @Override
    public DynamicHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new DynamicHolder(inflater.inflate(layoutResId, parent, false));
    }

    @Override
    public abstract void bindHolder(@NonNull DynamicHolder holder, @NonNull T item);


    private static class Impl<T> extends DynamicItemView<T> {

        private final BindHolder<T> bindHolder;

        Impl(@LayoutRes int layoutResId, @NonNull BindHolder<T> bindHolder) {
            super(layoutResId);
            this.bindHolder = bindHolder;
        }

        @Override
        public void bindHolder(@NonNull DynamicHolder holder, @NonNull T item) {
            bindHolder.bindHolder(holder, item);
        }
    }
}
