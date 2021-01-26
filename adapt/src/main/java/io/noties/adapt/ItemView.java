package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CheckResult;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.noties.adapt.util.ViewUtils;

public abstract class ItemView extends Item<ItemView.Holder> {

    protected ItemView(long id) {
        super(id);
    }

    @NonNull
    public abstract View createView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(createView(inflater, parent));
    }

    /**
     * Holder that caches result of {@link #findView(int)} and {@link #requireView(int)}
     */
    public static class Holder extends Item.Holder {

        private final Map<Integer, WeakReference<View>> cache = new HashMap<>(3);

        Holder(@NonNull View itemView) {
            super(itemView);
        }

        @Nullable
        @Override
        @CheckResult
        public <V extends View> V findView(@IdRes int id) {

            final Integer key = id;

            final WeakReference<View> reference = cache.get(key);
            final View cachedView = reference != null
                    ? reference.get()
                    : null;

            final View view;

            if (cachedView == null) {
                final View newView = super.findView(id);
                // cache
                if (newView != null) {
                    cache.put(key, new WeakReference<View>(newView));
                }

                view = newView;
            } else {
                view = cachedView;
            }

            //noinspection unchecked
            return (V) view;
        }

        @NonNull
        @Override
        @CheckResult
        public <V extends View> V requireView(@IdRes int id) {
            final View view = findView(id);
            if (view == null) {
                throw ViewUtils.notFoundException(itemView(), id);
            }
            //noinspection unchecked
            return (V) view;
        }
    }
}
