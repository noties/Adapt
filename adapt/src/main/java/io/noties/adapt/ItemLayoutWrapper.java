package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
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

    /**
     * Please note that if this constructor is used, then subclasses must override {@link #createLayout(LayoutInflater, ViewGroup)}.
     * If you know layout file before-hand, then {@link #ItemLayoutWrapper(int, Item)}
     * constructor can be used.
     *
     * @see #createLayout(LayoutInflater, ViewGroup)
     * @since 2.3.0-SNAPSHOT
     */
    public ItemLayoutWrapper(@NonNull Item<H> item) {
        this(0, item);
    }

    @SuppressWarnings("WeakerAccess")
    public ItemLayoutWrapper(@LayoutRes int layout, @NonNull Item<H> item) {
        super(item.id());
        this.layout = layout;
        this.item = item;
    }

    /**
     * You do not need to override this method if you use {@link #ItemLayoutWrapper(int, Item)}
     * constructor. Otherwise it is required to be overridden.
     *
     * @since 2.3.0-SNAPSHOT
     */
    @NonNull
    protected ViewGroup createLayout(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        if (layout == 0) {
            throw AdaptException.create("Override #createLayout method or specify layout XML " +
                    "resource ID by using appropriate constructor, item: %s", item);
        }
        return (ViewGroup) inflater.inflate(layout, parent, false);
    }

    /**
     * Append wrapped item view to layout. By default wrapped view is added directly to parent,
     * but if created layout has different structure this method can used to place wrapped view
     * in a special manner.
     *
     * @since 2.3.0-SNAPSHOT
     */
    protected void appendWrappedViewToLayout(@NonNull ViewGroup layout, @NonNull View wrappedView) {
        layout.addView(wrappedView);
    }

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        final ViewGroup root = createLayout(inflater, parent);
        final H holder = item.createHolder(inflater, root);
        appendWrappedViewToLayout(root, holder.itemView);
        return new Holder(root, holder);
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

    public static class Holder extends Item.Holder {

        protected final Item.Holder wrapped;

        protected Holder(@NonNull View itemView, @NonNull Item.Holder wrapped) {
            super(itemView);
            this.wrapped = wrapped;
        }
    }
}
