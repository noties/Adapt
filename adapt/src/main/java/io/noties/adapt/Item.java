package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CheckResult;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

import io.noties.adapt.util.ViewUtils;

public abstract class Item<H extends Item.Holder> {

    // `-1` is used for compatibility with the RecyclerView. Actual value from RecyclerView is not
    //  used due to the compileOnly `androidx.recyclerview` dependency (might not be available at runtime).
    // NB! this value must be synchronized with RecyclerView in case it changes there
    public static final long NO_ID = -1;

    protected static long hash(Object... args) {
        return Objects.hash(args);
    }

    private final long id;

    // cache viewType, it must not change during application runtime
    private int viewType = 0;

    protected Item(long id) {
        this.id = id;
    }

    @CheckResult
    public final long id() {
        return id;
    }

    /**
     * Generated view type must not be 0 (zero)
     */
    @CheckResult
    public final int viewType() {
        int viewType = this.viewType;
        if (viewType == 0) {
            viewType = this.viewType = Item.Key.of(this).viewType();
        }
        return viewType;
    }

    /**
     * Think of this method as it is a static method, all instance specific handling should be done
     * in the {@link #bind(Holder)} method instead.
     */
    @NonNull
    @CheckResult
    public abstract H createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    public abstract void bind(@NonNull H holder);


    public static class Holder {
        private final View itemView;

        public Holder(@NonNull View itemView) {
            this.itemView = itemView;
        }

        @NonNull
        @CheckResult
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

    public static abstract class Key {

        @NonNull
        @CheckResult
        public static Builder builder() {
            return new ItemKeys.KeyImpl.BuilderImpl();
        }

        @NonNull
        @CheckResult
        public static Key of(@NonNull Item<?> item) {
            return ItemKeys.create(item);
        }

        /**
         * Creates a Key for an {@link Item} without any wrappers
         */
        @NonNull
        @CheckResult
        public static Key single(@NonNull Class<? extends Item<?>> item) {
            return builder().build(item);
        }

        public interface Builder {

            @NonNull
            @CheckResult
            Builder wrapped(@NonNull Class<? extends ItemWrapper> by);

            @NonNull
            @CheckResult
            Key build(@NonNull Class<? extends Item<?>> item);
        }

        @CheckResult
        public abstract int viewType();

        @NonNull
        @CheckResult
        public abstract List<Class<? extends Item<?>>> items();

        @NonNull
        @CheckResult
        public abstract String toString();

        @NonNull
        @CheckResult
        public abstract String toShortString();

        @Override
        @CheckResult
        public abstract int hashCode();

        @Override
        @CheckResult
        public abstract boolean equals(Object o);
    }
}
