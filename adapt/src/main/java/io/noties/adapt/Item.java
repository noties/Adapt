package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CheckResult;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import java.util.Objects;

import io.noties.adapt.util.ViewUtils;

public abstract class Item<H extends Item.Holder> {

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

    public abstract void render(@NonNull H holder);

    public static class Holder {
        private final View itemView;

        public Holder(@NonNull View itemView) {
            this.itemView = itemView;
        }

        @NonNull
        public View itemView() {
            return itemView;
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
}
