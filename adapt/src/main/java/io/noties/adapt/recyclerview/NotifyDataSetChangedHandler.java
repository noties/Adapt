package io.noties.adapt.recyclerview;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.noties.adapt.Item;

public class NotifyDataSetChangedHandler implements AdaptRecyclerView.DataSetChangeHandler {

    @NonNull
    public static NotifyDataSetChangedHandler create() {
        return new NotifyDataSetChangedHandler();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void handleDataSetChange(
            @NonNull List<Item<?>> oldList,
            @NonNull List<Item<?>> newList,
            @NonNull AdaptRecyclerView.DataSetChangeResultCallback callback
    ) {
        final RecyclerView.Adapter<?> adapter = callback.applyItemsChange(newList);
        adapter.notifyDataSetChanged();
    }
}
