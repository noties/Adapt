package ru.noties.adapt.next;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncDiffUtilDataSetChanged implements Adapt.DataSetChangeHandler {

    @NonNull
    public static AsyncDiffUtilDataSetChanged create() {
        return create(Executors.newCachedThreadPool(), DiffUtilDataSetChanged.create());
    }

    @NonNull
    public static AsyncDiffUtilDataSetChanged create(@NonNull ExecutorService executorService) {
        return create(executorService, DiffUtilDataSetChanged.create());
    }

    @NonNull
    public static AsyncDiffUtilDataSetChanged create(@NonNull DiffUtilDataSetChanged diffUtilDataSetChanged) {
        return create(Executors.newCachedThreadPool(), diffUtilDataSetChanged);
    }

    @NonNull
    public static AsyncDiffUtilDataSetChanged create(
            @NonNull ExecutorService executorService,
            @NonNull DiffUtilDataSetChanged diffUtilDataSetChanged) {
        return new AsyncDiffUtilDataSetChanged(executorService, diffUtilDataSetChanged);
    }

    private final ExecutorService executorService;
    private final DiffUtilDataSetChanged diffUtilDataSetChanged;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final AtomicInteger id = new AtomicInteger();

    private Future<?> future;

    public AsyncDiffUtilDataSetChanged(
            @NonNull ExecutorService executorService,
            @NonNull DiffUtilDataSetChanged diffUtilDataSetChanged) {
        this.executorService = executorService;
        this.diffUtilDataSetChanged = diffUtilDataSetChanged;
    }

    @Override
    public void handleDataSetChange(
            @NonNull final Adapt adapt,
            @Nullable final Adapt.ItemViewTypeFactory itemViewTypeFactory,
            @NonNull final List<Item> oldList,
            @NonNull final List<Item> newList) {

        final int generation = id.incrementAndGet();

        if (future != null) {
            future.cancel(true);
        }

        future = executorService.submit(new Runnable() {
            @Override
            public void run() {

                final DiffUtil.DiffResult result = diffUtilDataSetChanged.diffResult(oldList, newList);

                // additionally create itemViewFactory in background if it's not specified
                final Adapt.ItemViewTypeFactory factory = itemViewTypeFactory == null
                        ? Adapt.createItemViewFactory(newList)
                        : itemViewTypeFactory;

                // we have other request started
                if (generation != id.get()) {
                    return;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (generation == id.get()) {
                            result.dispatchUpdatesTo(adapt.swapItemsBeforeUpdate(newList, factory));
                        }
                    }
                });
            }
        });
    }
}
