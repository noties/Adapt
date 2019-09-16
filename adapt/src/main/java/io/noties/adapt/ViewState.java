package io.noties.adapt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 2.3.0-SNAPSHOT
 */
public abstract class ViewState {

    /**
     * Method to save/restore view state. Must be called for each _child_ view inside a container
     * Call this in your {@link Item#render(Item.Holder)} method. Please note that this method
     * will save state of a view only after it is detached from a parent. If you need to save
     * state explicitly use {@link #save(long, View)}.
     */
    public static void process(final long id, @NonNull final View view) {

        // todo: validate the NO_ID (and do not save in that case)

        final ViewParent parent = view.getParent();
        if (parent != null) {
            // in most cases and during developing, I've noticed that in `render` recycler doesn't have
            // a parent yet, but it is initialized after render is finished. Anyway, in order to not
            // miss this case, we will process state here also
            final Cache cache = Cache.of(parent);
            cache.restore(id, view);
        }

        // in case when view is processed multiple times, use only the last attach listener
        final View.OnAttachStateChangeListener current =
                (View.OnAttachStateChangeListener) view.getTag(ID_ATTACH_LISTENER);
        if (current != null) {
            view.removeOnAttachStateChangeListener(current);
        }

        final View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                final ViewParent viewParent = v.getParent();
                if (viewParent != null) {
                    Cache.of(viewParent).restore(id, view);
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                final ViewParent viewParent = v.getParent();
                if (viewParent != null) {
                    Cache.of(viewParent).save(id, view);
                }
                view.removeOnAttachStateChangeListener(this);
                view.setTag(ID_ATTACH_LISTENER, null);
            }
        };
        view.addOnAttachStateChangeListener(listener);
        view.setTag(ID_ATTACH_LISTENER, listener);
    }

    /**
     * By default {@link #process(long, View)} will save view state when it is detached from parent.
     * In case when state must be saved before that this explicit method can be used.
     *
     * @param id   associated with {@link Item} (by calling {@link Item#id()})
     * @param view to save state
     * @see #onSaveInstanceState(View)
     * @see #onRestoreInstanceState(View, Bundle)
     */
    public static void save(long id, @NonNull View view) {

        final ViewParent parent = view.getParent();

        // if we have no parent -> nothing can be done
        if (parent != null) {
            final Cache cache = Cache.of(parent);
            cache.save(id, view);
        }
    }

    public static void restore(long id, @NonNull View view) {

        final ViewParent parent = view.getParent();

        if (parent != null) {
            final Cache cache = Cache.of(parent);
            cache.restore(id, view);
        }
    }

    /**
     * Method to clear internal cache in case if RecyclerView will have a completely new set of items
     * or a new adapter and state must not be persisted. Please note that this will be done
     * automatically when container is detached from a window.
     */
    public static void clear(@NonNull View container) {
        Cache.of(container).clear();
    }

    public static void clear(@NonNull View container, long id) {
        Cache.of(container).clear(id);
    }

    @Nullable
    public static Bundle onSaveInstanceState(@NonNull View container) {
        // here we should not append the cache as tag to the view,
        // we should obtain it _only_ if it's already present
        final Cache cache = Cache.get(container);
        final Map<Long, SparseArray<Parcelable>> map = cache != null
                ? cache.cache
                : null;
        if (map != null) {
            final Bundle bundle = new Bundle();
            for (Map.Entry<Long, SparseArray<Parcelable>> entry : map.entrySet()) {
                bundle.putSparseParcelableArray(Long.toString(entry.getKey()), entry.getValue());
            }
            return bundle;
        }
        return null;
    }

    public static boolean onRestoreInstanceState(@NonNull View container, @Nullable Bundle bundle) {
        // if restore is requested, most likely cache will be needed at some point, so
        // we can as well initialize it here
        final Cache cache = Cache.of(container);

        if (bundle != null) {

            final Map<Long, SparseArray<Parcelable>> map = cache.cache;

            long id;
            SparseArray<Parcelable> sparseArray;

            for (String key : bundle.keySet()) {
                try {
                    id = Long.parseLong(key);
                } catch (NumberFormatException e) {
                    // no op
                    continue;
                }
                sparseArray = bundle.getSparseParcelableArray(key);
                if (sparseArray != null) {
                    map.put(id, sparseArray);
                }
            }

            return true;
        }

        return false;
    }

    private ViewState() {
    }

    private static final int ID_CACHE = R.id.adapt_internal_view_state_cache;
    private static final int ID_ATTACH_LISTENER = R.id.adapt_internal_view_state_attach_listener;

    @VisibleForTesting
    static class Cache {

        @NonNull
        static Cache of(@NonNull ViewParent parent) {
            return of((View) parent);
        }

        @NonNull
        static Cache of(@NonNull View container) {
            Cache cache = (Cache) container.getTag(ID_CACHE);
            if (cache == null) {
                cache = new Cache();
                container.setTag(ID_CACHE, cache);
                container.addOnAttachStateChangeListener(new Cache.DisposeListener(cache));
            }
            return cache;
        }

        /**
         * Unlike {@link #of(View)} and {@link #of(ViewParent)} this method won\'t create a new
         * Cache instance if it\'s not present
         */
        @Nullable
        static Cache get(@NonNull View container) {
            return (Cache) container.getTag(ID_CACHE);
        }

        // Studio suggests LongSparseArray... it's not generic, is it?
        // Also, as documentation mentions - sparse array has worse performance that hash-map
        //  (although it has smaller memory footprint, but speed matters for us more)
        @SuppressLint("UseSparseArrays")
        final Map<Long, SparseArray<Parcelable>> cache = new HashMap<>(3);

        void save(long id, @NonNull View view) {
            final SparseArray<Parcelable> sparseArray = new SparseArray<>(1);
            view.saveHierarchyState(sparseArray);
            cache.put(id, sparseArray);
        }

        void restore(long id, @NonNull View view) {
            final SparseArray<Parcelable> sparseArray = cache.remove(id);
            if (sparseArray != null) {
                view.restoreHierarchyState(sparseArray);
            }
        }

        void clear() {
            cache.clear();
        }

        void clear(long id) {
            cache.remove(id);
        }

        private static class DisposeListener implements View.OnAttachStateChangeListener {

            private final Cache cache;

            DisposeListener(@NonNull Cache cache) {
                this.cache = cache;
            }

            @Override
            public void onViewAttachedToWindow(View v) {
                // no op
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                cache.clear();
                v.removeOnAttachStateChangeListener(this);
                v.setTag(ID_CACHE, null);
            }
        }
    }
}
