package ru.noties.adapt;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;

/**
 * An implementation of {@link Holder} that internally caches requested views. When using this
 * implementation there is no need to create a specific {@link Holder} implementation with
 * enumerated fields.
 */
public class DynamicHolder extends Holder {

    /**
     * @see #on(int, Action)
     */
    public interface Action<V extends View> {
        void apply(@NonNull V v);
    }

    private final SparseArray<View> cache = new SparseArray<>(3);

    public DynamicHolder(@NonNull View view) {
        super(view);
    }

    /**
     * Helper method to apply an action for a view with specified id. Please note that
     * this method is using {@link #requireView(int)} so, if a view is not found a
     * NullPointerException will be thrown
     *
     * @param id     of a view to find
     * @param action {@link Action} to be applied on found view
     * @return self for chaining
     * @throws NullPointerException if requested view is not found in layout
     * @see #requireView(int)
     */
    @NonNull
    public <V extends View> DynamicHolder on(@IdRes int id, @NonNull Action<V> action) throws NullPointerException {
        final V view = requireView(id);
        action.apply(view);
        return this;
    }

    @Override
    @Nullable
    public <V extends View> V findView(@IdRes int id) {

        final View view;

        // if we have already cached value use it (it might be null, but this method allows it)
        final int index = cache.indexOfKey(id);
        if (index > -1) {
            view = cache.valueAt(index);
        } else {
            view = super.findView(id);
            cache.put(id, view);
        }

        //noinspection unchecked
        return (V) view;
    }

    @NonNull
    @Override
    public <V extends View> V requireView(@IdRes int id) throws NullPointerException {

        final View view;

        final int index = cache.indexOfKey(id);
        if (index > -1) {

            view = cache.valueAt(index);

            // we are using one datastore for cached Views, so it might be possible
            // that nullable view was cached (via `findView` call) and then requested
            // here. This is why we need to additionally check for nullability (as this
            // method forbids nullable types).
            if (view == null) {
                throw new NullPointerException("Requested view is not found in layout and " +
                        "was previously requested by `findView` method call. Prefer using " +
                        "one method for the same view to be found (`findView` or `requestView`), " +
                        "id: R.id." + itemView.getResources().getResourceName(id));
            }

        } else {
            view = super.requireView(id);
            cache.put(id, view);
        }

        //noinspection unchecked
        return (V) view;
    }
}
