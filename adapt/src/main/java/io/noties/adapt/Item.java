package io.noties.adapt;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CheckResult;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Item<H extends Item.Holder> {

    /**
     * Helper method to obtain automatically assigned itemViewType ({@link #viewType()}).
     * If you override {@link #viewType()} to provide own value then do not use this method.
     *
     * @param type of Item to obtain value
     * @return generated item-view-type
     */
    public static int generatedViewType(@NonNull Class<? extends Item> type) {
        return ViewTypeStore.viewType(type);
    }

    public static final long NO_ID = RecyclerView.NO_ID;

    private static final int NO_VIEW_TYPE = -1;

    protected final long id;

    // cached value of viewType (once initialized will be saved)
    private int viewType = NO_VIEW_TYPE;

    protected Item(long id) {
        this.id = id;
    }

    public final long id() {
        return id;
    }

    @NonNull
    public abstract H createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    public abstract void render(@NonNull H holder);

    public int viewType() {
        int viewType = this.viewType;
        if (viewType == NO_VIEW_TYPE) {
            viewType = this.viewType = generatedViewType(getClass());
        }
        return viewType;
    }

    /**
     * Used only in RecyclerView context. Please note that returned ItemDecoration will still
     * operate on the whole RecyclerView like it was added explicitly via \'recyclerView#addItemDecoration\'
     */
    @Nullable
    public RecyclerView.ItemDecoration recyclerDecoration(@NonNull RecyclerView recyclerView) {
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    public static class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView) {
            super(itemView);
        }

        @NonNull
        @CheckResult
        protected <V extends View> V requireView(@IdRes int id) {
            return requireView(itemView, id);
        }

        @NonNull
        @CheckResult
        protected <V extends View> V requireView(@NonNull View view, @IdRes int id) {
            return ViewUtils.requireView(view, id);
        }
    }

    @VisibleForTesting
    static class ViewTypeStore {

        static final SparseIntArray CACHE = new SparseIntArray();
        static final AtomicInteger GENERATOR = new AtomicInteger();

        static int viewType(@NonNull Class<? extends Item> type) {
            final int hash = type.hashCode();
            int viewType = CACHE.get(hash, NO_VIEW_TYPE);
            if (viewType == NO_VIEW_TYPE) {
                synchronized (CACHE) {
                    viewType = CACHE.get(hash, NO_VIEW_TYPE);
                    if (viewType == NO_VIEW_TYPE) {
                        viewType = GENERATOR.incrementAndGet();
                        CACHE.put(hash, viewType);
                    }
                }
            }
            return viewType;
        }
    }
}
