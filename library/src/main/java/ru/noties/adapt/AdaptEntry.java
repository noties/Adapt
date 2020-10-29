package ru.noties.adapt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class AdaptEntry<T> {

    final ItemView<T, Holder> itemView;
    final ViewProcessor<T> viewProcessor;

    AdaptEntry(@NonNull ItemView<T, Holder> itemView, @Nullable ViewProcessor<T> viewProcessor) {
        this.itemView = itemView;
        this.viewProcessor = viewProcessor;
    }
}
