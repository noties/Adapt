package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;

import java.lang.reflect.Modifier;

public class AdaptBuilder<T> {

    private final Class<T> baseItemType;

    private final AdaptSource.Builder<T> adaptSourceBuilder;

    // by default this value is true
    private boolean hasStableIds = true;

    private LayoutInflater layoutInflater;

    private AdaptUpdate<T> adaptUpdate;

    AdaptBuilder(@NonNull Class<T> baseItemType) {
        this.baseItemType = baseItemType;
        this.adaptSourceBuilder = AdaptSource.builder(baseItemType);
    }

    @NonNull
    public <I extends T> AdaptBuilder<T> include(
            @NonNull Class<I> itemType,
            @NonNull ItemView<? super I, ? extends Holder> itemView) {
        addEntry(itemType, itemView, null);
        return this;
    }

    @NonNull
    public <I extends T> AdaptBuilder<T> include(
            @NonNull Class<I> itemType,
            @NonNull ItemView<? super I, ? extends Holder> itemView,
            @NonNull ViewProcessor<I> viewProcessor) {
        addEntry(itemType, itemView, viewProcessor);
        return this;
    }

    @NonNull
    public AdaptBuilder<T> hasStableIds(boolean hasStableIds) {
        this.hasStableIds = hasStableIds;
        return this;
    }

    @NonNull
    public AdaptBuilder<T> layoutInflater(@NonNull LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
        return this;
    }

    @NonNull
    public AdaptBuilder<T> adaptUpdate(@NonNull AdaptUpdate<T> adaptUpdate) {
        this.adaptUpdate = adaptUpdate;
        return this;
    }

    @NonNull
    public Adapt<T> build() {

        if (adaptUpdate == null) {
            adaptUpdate = new NotifyDataSetChangedUpdate<>();
        }

        return new AdaptImpl<>(
                baseItemType,
                layoutInflater,
                hasStableIds,
                adaptSourceBuilder.build(),
                adaptUpdate
        );
    }

    private <I extends T> void addEntry(
            @NonNull Class<I> type,
            @NonNull ItemView<? super I, ? extends Holder> itemView,
            @Nullable ViewProcessor<I> viewProcessor) throws AdaptConfigurationError {

        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            throw new AdaptConfigurationError("Cannot include an interface or an abstract class: " + type.getName());
        }

        //noinspection unchecked
        final AdaptEntry<I> entry = new AdaptEntry<>((ItemView<I, Holder>) itemView, viewProcessor);

        if (!adaptSourceBuilder.append(type, entry)) {
            throw new AdaptConfigurationError("Provided type has been added already: " + type.getName());
        }
    }
}
