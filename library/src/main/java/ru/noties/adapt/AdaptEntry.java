package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class AdaptEntry<T> {

    final ItemView<T, Holder> itemView;
    final ViewProcessor<T> viewProcessor;

    AdaptEntry(@NonNull ItemView<T, Holder> itemView, @Nullable ViewProcessor<T> viewProcessor) {
        this.itemView = itemView;
        this.viewProcessor = viewProcessor;
    }
}
