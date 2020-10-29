package ru.noties.adapt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;

import java.lang.reflect.Modifier;

/**
 * Main interface to create an instance of {@link Adapt}
 *
 * @see Adapt#builder(Class)
 */
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

    /**
     * Specify an item type to be included in {@link Adapt} instance.
     * Please note that only _exact_ type are supported (the ones that can be instantiated), so
     * no interfaces or abstract classes will be accepted and {@link AdaptConfigurationError}
     * will be thrown
     *
     * @param itemType type of an item
     * @param itemView {@link ItemView}
     * @return self
     * @throws AdaptConfigurationError if specified type is an interface or an abstract class
     * @see #include(Class, ItemView, ViewProcessor)
     */
    @NonNull
    public <I extends T> AdaptBuilder<T> include(
            @NonNull Class<I> itemType,
            @NonNull ItemView<? super I, ? extends Holder> itemView) throws AdaptConfigurationError {
        addEntry(itemType, itemView, null);
        return this;
    }

    /**
     * Specify an item type to be included in {@link Adapt} instance.
     * Please note that only _exact_ type are supported (the ones that can be instantiated), so
     * no interfaces or abstract classes will be accepted and {@link AdaptConfigurationError}
     * will be thrown
     *
     * @param itemType      type of an item
     * @param itemView      {@link ItemView}
     * @param viewProcessor {@link ViewProcessor} to be applied to a view after view is bound in adapter.
     *                      can be used for example to apply click listeners
     * @return self
     * @throws AdaptConfigurationError if specified type is an interface or an abstract class
     * @see #include(Class, ItemView)
     * @see ViewProcessor
     */
    @NonNull
    public <I extends T> AdaptBuilder<T> include(
            @NonNull Class<I> itemType,
            @NonNull ItemView<? super I, ? extends Holder> itemView,
            @NonNull ViewProcessor<I> viewProcessor) {
        addEntry(itemType, itemView, viewProcessor);
        return this;
    }

    /**
     * @param hasStableIds option to be redirected to a created RecyclerViewAdapter.
     *                     By default this value is `true`
     * @return self
     */
    @SuppressWarnings("unused")
    @NonNull
    public AdaptBuilder<T> hasStableIds(boolean hasStableIds) {
        this.hasStableIds = hasStableIds;
        return this;
    }

    /**
     * @param layoutInflater LayoutInflater to be used when creating views in adapter. If not specified
     *                       inflater will be obtained automatically with a Context of ViewGroup
     * @return self
     */
    @SuppressWarnings("unused")
    @NonNull
    public AdaptBuilder<T> layoutInflater(@NonNull LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
        return this;
    }

    /**
     * @param adaptUpdate {@link AdaptUpdate} to trigger items update. By default {@link NotifyDataSetChangedUpdate}
     *                    will be used if nothing is specified
     * @return self
     * @see AdaptUpdate
     * @see NotifyDataSetChangedUpdate
     * @see DiffUtilUpdate
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public AdaptBuilder<T> adaptUpdate(@NonNull AdaptUpdate<T> adaptUpdate) {
        this.adaptUpdate = adaptUpdate;
        return this;
    }

    /**
     * @return an instance of {@link Adapt}
     * @throws AdaptConfigurationError if no items were added via {@link #include(Class, ItemView)}
     *                                 or {@link #include(Class, ItemView, ViewProcessor)} calls
     */
    @NonNull
    public Adapt<T> build() throws AdaptConfigurationError {

        if (adaptUpdate == null) {
            adaptUpdate = NotifyDataSetChangedUpdate.create();
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
