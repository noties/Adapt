package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Class to allow nested RecyclerView. Please note that each unique group must have a dedicated subclass
 * in order to have a unique view-type.
 * <p>
 * Please note that this item automatically takes care
 *
 * @since 2.2.0-SNAPSHOT
 */
public abstract class ItemGroup extends Item<ItemGroup.Holder> {

    // should we allow mutation, how to trigger notification then?
    private List<Item> children;

    protected ItemGroup(long id, @NonNull List<Item> children) {
        super(id);
        this.children = children;
    }

    @NonNull
    public List<Item> getChildren() {
        return children;
    }

    /**
     * Please note that item has no means to invalidate parent recycler-view. This is why, if
     * you call this method make sure to manually dispatch update notification
     *
     * @param children a new set of children for this group
     */
    public void setChildren(@NonNull List<Item> children) {
        this.children = children;
    }

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {

        final View view = createView(inflater, parent);
        final RecyclerView recyclerView = initNestedRecyclerView(view);
        final Adapt adapt = createNestedAdapt();

        processRecyclerViewPool(parent, recyclerView);

        // set adapter
        recyclerView.setAdapter(adapt);

        return new Holder(view, recyclerView, adapt);
    }

    @Override
    public void render(@NonNull Holder holder) {
        holder.adapt.setItems(children);

        processState(holder);
    }

    @NonNull
    protected abstract View createView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    /**
     * Initialize recycler-view in view created by {@link #createView(LayoutInflater, ViewGroup)} call.
     * Here everything must be prepared for a recycler-view to be used (LayoutManager, animations,
     * decorations, etc)
     */
    @NonNull
    protected abstract RecyclerView initNestedRecyclerView(@NonNull View view);

    /**
     * Create an {@link Adapt} instance for nested recycler-view.
     */
    @NonNull
    protected Adapt createNestedAdapt() {
        return Adapt.create();
    }

    protected void processRecyclerViewPool(@NonNull ViewGroup parent, @NonNull RecyclerView recyclerView) {
        // if parent is a recycler-view -> share the recycler pool
        // this item-group can still be displayed in a regular view-group
        if (parent instanceof RecyclerView) {
            recyclerView.setRecycledViewPool(((RecyclerView) parent).getRecycledViewPool());
        }
    }

    protected void processState(@NonNull Holder holder) {
        NestedRecyclerState
                .process(id(), holder.recyclerView);
    }

    protected static class Holder extends Item.Holder {

        public final RecyclerView recyclerView;
        public final Adapt adapt;

        protected Holder(@NonNull View itemView, @NonNull RecyclerView recyclerView, @NonNull Adapt adapt) {
            super(itemView);

            this.recyclerView = recyclerView;
            this.adapt = adapt;
        }
    }
}
