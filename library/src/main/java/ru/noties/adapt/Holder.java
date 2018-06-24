package ru.noties.adapt;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @see DynamicHolder
 */
public class Holder extends RecyclerView.ViewHolder {

    public Holder(@NonNull View view) {
        super(view);
    }

    /**
     * Helper method to obtain a View by specified id. Please note that this method might
     * return null if there is no View with provided id. Consider using {@link #requireView(int)}
     * if a view must be present
     *
     * @param id of a view to find
     * @return found view or null
     * @see #requireView(int)
     */
    @Nullable
    public <V extends View> V findView(@IdRes int id) {
        //noinspection unchecked
        return (V) itemView.findViewById(id);
    }

    /**
     * Helper method to obtain a view. If no view is found by specified id a
     * NullPointerException will be thrown
     *
     * @param id of a view to find
     * @return view with specified id
     * @throws NullPointerException if there is no view with provided id
     * @see #findView(int)
     */
    @NonNull
    public <V extends View> V requireView(@IdRes int id) throws NullPointerException {
        final View view = itemView.findViewById(id);
        if (view == null) {
            throw new NullPointerException("View with specified id is not found: " +
                    "R.id." + itemView.getResources().getResourceName(id));
        }
        //noinspection unchecked
        return (V) view;
    }
}
