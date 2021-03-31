package io.noties.adapt;

import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.noties.adapt.util.ViewUtils;

/**
 * Holder that caches result of {@link #findView(int)} and {@link #requireView(int)}
 */
public class CachingHolder extends Item.Holder {

    private final Map<Integer, WeakReference<View>> cache;

    public CachingHolder(@NonNull View itemView) {
        this(itemView, new HashMap<Integer, WeakReference<View>>(3));
    }

    @VisibleForTesting
    CachingHolder(@NonNull View itemView, @NonNull Map<Integer, WeakReference<View>> cacheImpl) {
        super(itemView);
        this.cache = cacheImpl;
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
                cache.put(key, new WeakReference<>(newView));
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
        // obtain view via `findView` call, which internally caches views
        final View view = findView(id);
        if (view == null) {
            // throw if no value is available
            throw ViewUtils.notFoundException(itemView(), id);
        }
        //noinspection unchecked
        return (V) view;
    }
}
