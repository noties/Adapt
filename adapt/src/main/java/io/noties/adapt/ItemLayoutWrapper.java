package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A wrapper to put {@link Item} view inside another layout. Please note that this wrapper
 * uses supplied item {@code viewType} so make sure that all items of the same type is wrapped
 * with the same ItemLayoutWrapper.
 *
 * @since 2.2.0
 */
public class ItemLayoutWrapper<H extends Item.Holder>
        extends Item<ItemLayoutWrapper.Holder> implements HasWrappedItem<H> {

    /**
     * @param layout resource to wrap supplied {@code item}, root view tag must be a ViewGroup
     * @param item   to wrap
     */
    @NonNull
    public static <H extends Item.Holder> ItemLayoutWrapper<H> create(
            @LayoutRes int layout,
            @NonNull Item<H> item) {
        return new ItemLayoutWrapper<>(layout, item);
    }

    private final int layout;
    private final Item<H> item;

    @SuppressWarnings("WeakerAccess")
    public ItemLayoutWrapper(@LayoutRes int layout, @NonNull Item<H> item) {
        super(item.id());
        this.layout = layout;
        this.item = item;
    }

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        final ViewGroup root = (ViewGroup) inflater.inflate(layout, parent, false);
        return new Holder(root, item.createHolder(inflater, root));
    }

    @Override
    public void render(@NonNull Holder holder) {
        //noinspection unchecked
        item.render((H) holder.wrapped);
    }

    @Override
    public int viewType() {
        return item.viewType();
    }

    @Nullable
    @Override
    public RecyclerView.ItemDecoration recyclerDecoration(@NonNull RecyclerView recyclerView) {
        return item.recyclerDecoration(recyclerView);
    }

    @NonNull
    @Override
    public Item<H> item() {
        return item;
    }

    @SuppressWarnings("WeakerAccess")
    protected static class Holder extends Item.Holder {

        protected final Item.Holder wrapped;

        protected Holder(@NonNull ViewGroup itemView, @NonNull Item.Holder wrapped) {
            super(itemView);
            this.wrapped = wrapped;

            appendWrappedView(itemView, wrapped);
        }

        protected void appendWrappedView(@NonNull ViewGroup group, @NonNull Item.Holder wrapped) {
            // manually add wrapped item view
            group.addView(wrapped.itemView);
        }
    }
}
