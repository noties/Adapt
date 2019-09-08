package io.noties.adapt;

import android.annotation.SuppressLint;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

/**
 * @see #process(long, RecyclerView)
 * @since 2.2.0
 */
public abstract class NestedRecyclerState {

    /**
     * Method to save/restore nested RecyclerView state.
     * Call this in your {@link Item#render(Item.Holder)} method.
     */
    public static void process(final long id, @NonNull final RecyclerView recyclerView) {

        final ViewParent parent = recyclerView.getParent();
        if (parent != null) {
            // in most cases and during developing, I've noticed that in `render` recycler doesn't have
            // a parent yet, but it is initialized after render is finished. Anyway, in order to not
            // miss this case, we will process state here also
            final Cache cache = Cache.of(parent);
            cache.restore(id, recyclerView);
        }

        recyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                // restore state
                Cache.of(recyclerView.getParent()).restore(id, recyclerView);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                // save state, scroll to 0 position (reset state), and remove onAttachStateListener (self)
                Cache.of(recyclerView.getParent()).save(id, recyclerView);

                // sometimes a view with recycler is displayed with previous state saved
                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    layoutManager.scrollToPosition(0);
                }

                recyclerView.removeOnAttachStateChangeListener(this);
            }
        });
    }

    /**
     * Method to clear internal cache in case if RecyclerView will have a completely new set of items
     * or a new adapter and state must not be persisted. Please note that this will be done
     * automatically when container is detached from a window.
     */
    public static void clear(@NonNull View container) {
        Cache.of(container).clear();
    }

    private NestedRecyclerState() {
    }

    private static class Cache {

        @NonNull
        static Cache of(@NonNull ViewParent parent) {
            return of((View) parent);
        }

        @NonNull
        static Cache of(@NonNull View container) {
            Cache cache = (Cache) container.getTag(R.id.adapt_internal_nested_recycler_state_cache);
            if (cache == null) {
                cache = new Cache();
                container.setTag(R.id.adapt_internal_nested_recycler_state_cache, cache);
                container.addOnAttachStateChangeListener(new DisposeListener(cache));
            }
            return cache;
        }

        // Studio suggests LongSparseArray... it's not generic, is it?
        // Also, as documentation mentions - sparse array has worse performance that hash-map
        //  (although it has smaller memory footprint, but speed matters for us more)
        @SuppressLint("UseSparseArrays")
        private final Map<Long, Parcelable> cache = new HashMap<>(3);

        void save(long id, @NonNull RecyclerView recyclerView) {

            final Parcelable parcelable;

            final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

            if (layoutManager != null) {
                parcelable = layoutManager.onSaveInstanceState();
            } else {
                parcelable = null;
            }

            if (parcelable == null) {
                // remove it in case there was a previous version
                cache.remove(id);
            } else {
                cache.put(id, parcelable);
            }
        }

        void restore(long id, @NonNull RecyclerView recyclerView) {
            final Parcelable parcelable = cache.remove(id);
            final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (parcelable != null
                    && layoutManager != null) {
                layoutManager.onRestoreInstanceState(parcelable);
            }
        }

        void clear() {
            cache.clear();
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
                v.setTag(R.id.adapt_internal_nested_recycler_state_cache, null);
            }
        }
    }
}
