package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import static io.noties.adapt.ListUtils.safeList;

/**
 * @since 2.3.0-SNAPSHOT
 */
public abstract class ItemViewGroup extends Item<ItemViewGroup.Holder>
        implements HasChildrenItems {

    private List<Item> children;

    protected ItemViewGroup(long id, @NonNull List<Item> children) {
        super(id);
        this.children = children;
    }

    @Override
    @NonNull
    public List<Item> getChildren() {
        return safeList(children);
    }

    @Override
    public void setChildren(@Nullable List<Item> children) {
        this.children = children;
    }

    @NonNull
    protected abstract ViewGroup createViewGroup(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(createAdaptViewGroup(createViewGroup(inflater, parent)));
    }

    @Override
    public void render(@NonNull Holder holder) {
        final AdaptViewGroup adaptViewGroup = holder.adaptViewGroup;
        adaptViewGroup.setItems(children);
        processState(adaptViewGroup);
    }

    @NonNull
    protected AdaptViewGroup createAdaptViewGroup(@NonNull ViewGroup group) {
        return AdaptViewGroup.create(group);
    }

    protected void processState(@NonNull AdaptViewGroup adaptViewGroup) {
        // here state of view can be processed, for example if displayed in a RecyclerView
        // for ex: `ViewState.process(id(), adaptViewGroup.group());`
    }

    protected static class Holder extends Item.Holder {

        protected final AdaptViewGroup adaptViewGroup;

        protected Holder(@NonNull AdaptViewGroup adaptViewGroup) {
            super(adaptViewGroup.viewGroup());
            this.adaptViewGroup = adaptViewGroup;
        }
    }
}
