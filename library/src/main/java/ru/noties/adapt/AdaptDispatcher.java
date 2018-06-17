package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public abstract class AdaptDispatcher<M, T> {

    @NonNull
    public static <M, T> AdaptDispatcher<M, T> create(
            @NonNull Adapt<T> adapt,
            @NonNull Reducer<M, T> reducer) {
        return new Impl<>(adapt, reducer);
    }

    public interface Reducer<M, T> {
        @Nullable
        List<? extends T> reduce(@NonNull M model);
    }

    public abstract void dispatch(@NonNull M model);


    static class Impl<M, T> extends AdaptDispatcher<M, T> {

        private final Adapt<T> adapt;
        private final Reducer<M, T> reducer;

        Impl(@NonNull Adapt<T> adapt, @NonNull Reducer<M, T> reducer) {
            this.adapt = adapt;
            this.reducer = reducer;
        }

        @Override
        public void dispatch(@NonNull M model) {
            adapt.setItems(reducer.reduce(model));
        }
    }
}
