package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.noties.adapt.util.ViewUtils;

public abstract class InflatedItem extends Item<InflatedItem.Holder> {

    public final int layoutResId;

    protected InflatedItem(long id, @LayoutRes int layoutResId) {
        super(id);
        this.layoutResId = layoutResId;
    }

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(inflater.inflate(layoutResId, parent, false));
    }

    @Override
    public abstract void render(@NonNull Holder holder);

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
        public <V extends View> V findView(@IdRes int id) {

            // if key is not present -> first lookup
            // if key is present:
            //  if value is null -> not found
            //  if value is not null
            //      if get == null -> detached/recycled -> throw?
            //      else -> return

            final View view;

            final Integer key = id;

            if (cache.containsKey(key)) {
                final WeakReference<View> reference = cache.get(key);
                view = reference != null
                        ? reference.get()
                        : null;
            } else {
                view = super.findView(id);
                final WeakReference<View> reference = view != null
                        ? new WeakReference<View>(view)
                        : null;

                cache.put(key, reference);
            }

            //noinspection unchecked
            return (V) view;
        }

        @NonNull
        @Override
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
