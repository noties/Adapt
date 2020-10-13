package io.noties.adapt.next.recyclerview;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import io.noties.adapt.next.Item;

public class AsyncDiffUtilDataSetChangedHandler implements AdaptRecyclerView.DataSetChangeHandler {

    @NonNull
    public static AsyncDiffUtilDataSetChangedHandler create() {
        return create(DiffUtilDataSetChangedHandler.create());
    }

    @NonNull
    public static AsyncDiffUtilDataSetChangedHandler create(
            @NonNull DiffUtilDataSetChangedHandler diffUtilDataSetChangedHandler
    ) {
        return create(diffUtilDataSetChangedHandler, Executors.newCachedThreadPool());
    }

    @NonNull
    public static AsyncDiffUtilDataSetChangedHandler create(
            @NonNull DiffUtilDataSetChangedHandler diffUtilDataSetChangedHandler,
            @NonNull ExecutorService executorService
    ) {
        return new AsyncDiffUtilDataSetChangedHandler(diffUtilDataSetChangedHandler, executorService);
    }

    private final DiffUtilDataSetChangedHandler diffUtilDataSetChangedHandler;
    private final ExecutorService executorService;

    private final AtomicInteger id = new AtomicInteger();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private Future<?> future;

    AsyncDiffUtilDataSetChangedHandler(
            @NonNull DiffUtilDataSetChangedHandler diffUtilDataSetChangedHandler,
            @NonNull ExecutorService executorService
    ) {
        this.diffUtilDataSetChangedHandler = diffUtilDataSetChangedHandler;
        this.executorService = executorService;
    }

    @Override
    public void handleDataSetChange(
            @NonNull final List<Item<?>> oldList,
            @NonNull final List<Item<?>> newList,
            @NonNull final AdaptRecyclerView.DataSetChangeResultCallback callback) {

        final int generation = id.incrementAndGet();

        if (future != null) {
            future.cancel(true);
        }

        future = executorService.submit(new Runnable() {
            @Override
            public void run() {
                final DiffUtil.DiffResult result = diffUtilDataSetChangedHandler.diffResult(oldList, newList);
                // check if this diff is still relevant (no new tasks were submitted)
                if (generation == id.get()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (generation == id.get()) {
                                final RecyclerView.Adapter<?> adapter = callback.applyItemsChange(newList);
                                if (adapter != null) {
                                    result.dispatchUpdatesTo(adapter);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
