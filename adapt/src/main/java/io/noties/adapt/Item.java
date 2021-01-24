package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CheckResult;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import io.noties.adapt.util.ViewUtils;

public abstract class Item<H extends Item.Holder> {

    // `-1` is used for compatibility with the RecyclerView. Actual value from RecyclerView is not
    //  used due to the compileOnly `androidx.recyclerview` dependency (might not be available at runtime).
    // NB! this value must be synchronized with RecyclerView in case it changes
    public static final long NO_ID = -1;

    protected static long hash(Object... args) {
        return Objects.hash(args);
    }

    private final long id;

    protected Item(long id) {
        this.id = id;
    }

    public final long id() {
        return id;
    }

    /**
     * Think of this method as it is a static method, all instance specific handling should be done
     * in the {@link #bind(Holder)} method instead.
     */
    @NonNull
    public abstract H createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    public abstract void bind(@NonNull H holder);

    public static class Holder {
        private final View itemView;

        public Holder(@NonNull View itemView) {
            this.itemView = itemView;
        }

        @NonNull
        public View itemView() {
            return itemView;
        }

        @Nullable
        @CheckResult
        public <V extends View> V findView(@IdRes int id) {
            return itemView.findViewById(id);
        }

        @NonNull
        @CheckResult
        public <V extends View> V requireView(@IdRes int id) {
            return ViewUtils.requireView(itemView, id);
        }
    }
}
