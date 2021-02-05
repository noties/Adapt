package io.noties.adapt.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.noties.adapt.Adapt;
import io.noties.adapt.AdaptException;
import io.noties.adapt.AdaptStore;
import io.noties.adapt.Item;
import io.noties.adapt.R;
import io.noties.adapt.util.ListUtils;

// TODO: bind to existing view in layout
public class AdaptView implements Adapt {

    public interface Configuration {

        @NonNull
        Configuration layoutInflater(@NonNull LayoutInflater layoutInflater);

        @NonNull
        Configuration item(@NonNull Item<?> item);
    }

    public interface Configurator {
        void configure(@NonNull Configuration configuration);
    }

    @NonNull
    public static AdaptView init(@NonNull ViewGroup viewGroup) {
        return new AdaptView(viewGroup, new ConfigurationImpl());
    }

    @NonNull
    public static AdaptView init(@NonNull ViewGroup viewGroup, @NonNull Configurator configurator) {
        final ConfigurationImpl configuration = new ConfigurationImpl();
        configurator.configure(configuration);
        return new AdaptView(viewGroup, configuration);
    }

    private static final int ID_HOLDER = R.id.adapt_internal_holder;
    private static final int ID_ITEM = R.id.adapt_internal_item;

    private final ViewGroup viewGroup;
    private final LayoutInflater layoutInflater;

    private View view;

    AdaptView(
            @NonNull ViewGroup viewGroup,
            @NonNull ConfigurationImpl configuration
    ) {
        this.viewGroup = viewGroup;

        LayoutInflater layoutInflater;
        this.layoutInflater = (layoutInflater = configuration.layoutInflater) != null
                ? layoutInflater
                : LayoutInflater.from(viewGroup.getContext());

        final View view;
        final Item<?> item = configuration.item;
        if (item != null) {

            final Item.Holder holder = item.createHolder(this.layoutInflater, viewGroup);

            view = holder.itemView();
            view.setTag(ID_HOLDER, holder);
            view.setTag(ID_ITEM, item);

            viewGroup.addView(view);

            AdaptStore.assign(view, this);

            //noinspection unchecked,rawtypes
            ((Item) item).bind(holder);

        } else {
            // we still need to create a mock view, so later we can add real one at the correct place
            view = new View(viewGroup.getContext());
            viewGroup.addView(view);
        }
        this.view = view;
    }

    @NonNull
    public LayoutInflater inflater() {
        return layoutInflater;
    }

    @NonNull
    public ViewGroup viewGroup() {
        return viewGroup;
    }

    /**
     * As multiple types can be bound, returned view might be different between different items
     */
    @NonNull
    public View view() {
        return view;
    }

    @Nullable
    public Item<?> item() {
        return (Item<?>) view.getTag(ID_ITEM);
    }

    public void setItem(@Nullable Item<?> item) {

        if (item == null) {
            // just put mocked view
            view = replaceView(new View(viewGroup.getContext()));
        } else {

            final Item.Holder holder = (Item.Holder) view.getTag(ID_HOLDER);

            if (holder == null
                    || !bind(item, holder)) {
                // create new
                createHolder(item);
            }

            // save item information
            view.setTag(ID_ITEM, item);
        }
    }

    public void notifyChanged() {
        setItem(item());
    }

    private boolean bind(@NonNull Item<?> item, @NonNull Item.Holder holder) {
        try {
            //noinspection unchecked,rawtypes
            ((Item) item).bind(holder);
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }

    private void createHolder(@NonNull Item<?> item) {

        final Item.Holder holder = item.createHolder(layoutInflater, viewGroup);

        view = replaceView(holder.itemView());
        view.setTag(ID_HOLDER, holder);

        AdaptStore.assign(view, this);

        //noinspection unchecked,rawtypes
        ((Item) item).bind(holder);
    }

    @NonNull
    private View replaceView(@NonNull View view) {
        final int index = indexOfViewInGroup();
        viewGroup.removeViewAt(index);
        viewGroup.addView(view, index);
        return view;
    }

    // Checks for proper index adn throws in case of an error
    private int indexOfViewInGroup() {
        final int index = viewGroup.indexOfChild(view);
        if (index < 0) {
            throw AdaptException.create(String.format(Locale.ROOT,
                    "View is not attached to parent, view: %s, parent: %s", view, viewGroup));
        }
        return index;
    }

    @NonNull
    @Override
    public List<Item<?>> items() {
        return Collections.<Item<?>>singletonList(item());
    }

    @Override
    public void setItems(@Nullable List<Item<?>> items) {

        final int size = ListUtils.size(items);
        if (size > 1) {
            throw AdaptException.create("AdaptView can hold at most one item, items: " + items);
        }

        //noinspection ConstantConditions,
        final Item<?> item = size == 0
                ? null
                : items.get(0);

        setItem(item);
    }

    @Override
    public void notifyAllItemsChanged() {
        notifyChanged();
    }

    @Override
    public void notifyItemChanged(@NonNull Item<?> item) {

        final Item<?> current = item();
        if (current == null) {
            return;
        }

        // TODO: check class now, but after use proper itemViewType (to wrap)
        if (current.getClass().equals(item.getClass())
                && current.id() == item.id()) {
            notifyChanged();
        }
    }

    private static class ConfigurationImpl implements Configuration {

        LayoutInflater layoutInflater;
        Item<?> item;

        @NonNull
        @Override
        public Configuration layoutInflater(@NonNull LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
            return this;
        }

        @NonNull
        @Override
        public Configuration item(@NonNull Item<?> item) {
            this.item = item;
            return this;
        }
    }
}
