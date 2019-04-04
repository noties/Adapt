package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Wrapper that allows enhancing existing {@link Item}. Please note that this wrapper
 * by default will use own \'recyclerViewType\' in order to distinguish from original (wrapped) item.
 * Other methods {@link #createHolder(LayoutInflater, ViewGroup)}, {@link #render(Holder)},
 * {@link #recyclerDecoration(RecyclerView)} are calling
 * original item. Ids are shared (the same for original and wrapped (this) items)
 *
 * @see OnClickWrapper
 * @since 2.0.0-SNAPSHOT
 */
public class ItemWrapper<H extends Item.Holder> extends Item<H> {

    private final Item<H> item;

    protected ItemWrapper(@NonNull Item<H> item) {
        super(item.id());
        this.item = item;
    }

    @NonNull
    public Item<H> item() {
        return item;
    }

    @NonNull
    @Override
    public H createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return item.createHolder(inflater, parent);
    }

    @Override
    public void render(@NonNull H holder) {
        item.render(holder);
    }

    @Override
    public int viewType() {
        // we will be using original viewType (if not all wrapped items will have the same viewType).
        // Please note, that wrapper must not modify view of original item (which can lead to unexpected
        // errors and bugs)
        return item.viewType();
    }

    @Nullable
    @Override
    public RecyclerView.ItemDecoration recyclerDecoration(@NonNull RecyclerView recyclerView) {
        return item.recyclerDecoration(recyclerView);
    }
}
