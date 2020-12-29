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

    // there is no restriction of using only signed integers for ids, so it is
    //  strange for RecyclerView to use -1 as an indication of NO VALUE
    // TODO: use some value here (obtain from recycler if available?)
    public static final long NO_ID = 0;

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
