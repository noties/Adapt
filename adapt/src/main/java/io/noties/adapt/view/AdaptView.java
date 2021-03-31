package io.noties.adapt.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.noties.adapt.Adapt;
import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.R;
import io.noties.adapt.util.ListUtils;

public class AdaptView implements Adapt {

    @SuppressWarnings("UnusedReturnValue")
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
    @CheckResult
    public static AdaptView init(@NonNull ViewGroup viewGroup) {
        return new AdaptView(viewGroup, new ConfigurationImpl());
    }

    @NonNull
    @CheckResult
    public static AdaptView init(@NonNull ViewGroup viewGroup, @NonNull Configurator configurator) {
        final ConfigurationImpl configuration = new ConfigurationImpl();
        configurator.configure(configuration);
        return new AdaptView(viewGroup, configuration);
    }

    static final int ID_HOLDER = R.id.adapt_internal_holder;
    static final int ID_ITEM = R.id.adapt_internal_item;

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
    @CheckResult
    public LayoutInflater inflater() {
        return layoutInflater;
    }

    @NonNull
    @CheckResult
    public ViewGroup viewGroup() {
        return viewGroup;
    }

    /**
     * As multiple types can be bound, returned view might be different between different items
     */
    @NonNull
    @CheckResult
    public View view() {
        return view;
    }

    @Nullable
    @CheckResult
    public Item<?> item() {
        return (Item<?>) view.getTag(ID_ITEM);
    }

    public void setItem(@Nullable Item<?> item) {

        final Item<?> currentItem = item();

        if (item == null) {
            // if we have no item at this point, then there is no need to create a new mocked view
            if (currentItem != null) {
                // just put mocked view
                view = replaceView(new View(viewGroup.getContext()));
            }
        } else {

            // if previous is null, or viewTypes are different, then create a new holder/view
            if (currentItem == null
                    || currentItem.viewType() != item.viewType()) {
                // create new holder/view
                createHolder(item);
            } else {
                // items have the same itemViewType, proceed with the same holder/view
                final Item.Holder holder = (Item.Holder) view.getTag(ID_HOLDER);
                //noinspection unchecked,rawtypes
                ((Item) item).bind(holder);
            }

            // save item information
            view.setTag(ID_ITEM, item);
        }
    }

    public void notifyChanged() {
        setItem(item());
    }

    private void createHolder(@NonNull Item<?> item) {

        final Item.Holder holder = item.createHolder(layoutInflater, viewGroup);

        view = replaceView(holder.itemView());
        view.setTag(ID_HOLDER, holder);

        //noinspection unchecked,rawtypes
        ((Item) item).bind(holder);
    }

    @NonNull
    @CheckResult
    private View replaceView(@NonNull View view) {
        final int index = indexOfViewInGroup();
        viewGroup.removeViewAt(index);
        viewGroup.addView(view, index);
        return view;
    }

    // Checks for proper index and throws in case of an error
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
    @CheckResult
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

        if (current.viewType() == item.viewType()
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
