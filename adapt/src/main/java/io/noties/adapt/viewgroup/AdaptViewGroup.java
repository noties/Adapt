package io.noties.adapt.viewgroup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

import io.noties.adapt.Adapt;
import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.R;
import io.noties.adapt.util.ListUtils;

// NB! this class assumes that ViewGroup is not modified from outside (no views are added or removed
//  by other means)
// NB! using items with NO_ID is not efficient, as with each update (setItems), items with NO_ID are
//  always removed and then added
public class AdaptViewGroup implements Adapt, AdaptViewGroupDiff.Parent {

    public interface Configuration {

        @NonNull
        Configuration layoutInflater(@NonNull LayoutInflater inflater);

        @NonNull
        Configuration adaptViewGroupDiff(@NonNull AdaptViewGroupDiff adaptViewGroupDiff);

        /**
         * @param changeHandler to handle layout changes
         */
        @NonNull
        Configuration changeHandler(@NonNull ChangeHandler changeHandler);
    }

    public interface Configurator {
        void configure(@NonNull Configuration configuration);
    }

    /**
     * @see Configuration#changeHandler(ChangeHandler)
     * @see ViewGroupChangeHandler
     * @see TransitionChangeHandler
     */
    public interface ChangeHandler {

        /**
         * Will be called before processing items (diffing)
         */
        void begin(@NonNull ViewGroup group);

        void removeAll(@NonNull ViewGroup group);

        void removeAt(@NonNull ViewGroup group, int position);

        void move(@NonNull ViewGroup group, int from, int to);

        void insertAt(@NonNull ViewGroup group, @NonNull View view, int position);

        /**
         * Will be called after all diff events are dispatched.
         */
        void end(@NonNull ViewGroup group);
    }

    @NonNull
    public static AdaptViewGroup init(@NonNull ViewGroup viewGroup) {
        return new AdaptViewGroup(viewGroup, new ConfigurationImpl());
    }

    @NonNull
    public static AdaptViewGroup init(@NonNull ViewGroup viewGroup, @NonNull Configurator configurator) {
        final ConfigurationImpl configuration = new ConfigurationImpl();
        configurator.configure(configuration);
        return new AdaptViewGroup(viewGroup, configuration);
    }

    private static final int ID_ITEM = R.id.adapt_internal_item;
    private static final int ID_HOLDER = R.id.adapt_internal_holder;

    private final ViewGroup viewGroup;
    private final ConfigurationImpl configuration;

    private List<Item<? extends Item.Holder>> items;

    AdaptViewGroup(@NonNull ViewGroup viewGroup, @NonNull ConfigurationImpl configuration) {
        this.viewGroup = viewGroup;
        this.configuration = configuration;

        final LayoutInflater inflater = configuration.inflater;
        if (inflater == null) {
            configuration.inflater = LayoutInflater.from(viewGroup.getContext());
        }

        // clear viewGroup if there are children at this point?
        // ensure that no views are added/removed via custom ChangeHandler?
    }

    @NonNull
    public ViewGroup viewGroup() {
        return viewGroup;
    }

    @NonNull
    @Override
    public List<Item<?>> items() {
        return ListUtils.freeze(items);
    }

    @Override
    public void setItems(@Nullable List<Item<?>> items) {

        final ChangeHandler changeHandler = configuration.changeHandler;

        changeHandler.begin(viewGroup);
        try {

            if (items == null
                    || items.isEmpty()) {
                // no need to validate what we have at this point -> nothing should be displayed
                changeHandler.removeAll(viewGroup);
                return;
            }

            configuration.adaptViewGroupDiff.diff(
                    this,
                    ListUtils.freeze(this.items),
                    ListUtils.freeze(items)
            );

        } finally {

            changeHandler.end(viewGroup);

            this.items = items;
        }
    }

    @Override
    public void notifyAllItemsChanged() {
        setItems(items);
    }

    @Nullable
    public Item<?> findItemFor(@NonNull View view) {
        //noinspection rawtypes,
        return (Item) view.getTag(ID_ITEM);
    }

    @Nullable
    public View findViewFor(@NonNull Item<?> item) {
        for (int i = 0, count = viewGroup.getChildCount(); i < count; i++) {
            final View view = viewGroup.getChildAt(i);
            if (item == view.getTag(ID_ITEM)) {
                return view;
            }
        }
        return null;
    }

    @Override
    public void notifyItemChanged(@NonNull Item<?> item) {
        final View view = findViewFor(item);
        if (view != null) {
            final int index = viewGroup.indexOfChild(view);
            render(index, item);
        }
    }

    @Override
    public void removeAt(int index) {
        configuration.changeHandler.removeAt(viewGroup, index);
    }

    @Override
    public void move(int from, int to) {
        configuration.changeHandler.move(viewGroup, from, to);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void insertAt(int index, @NonNull Item item) {
        final Item.Holder holder = item.createHolder(configuration.inflater, viewGroup);
        final View view = holder.itemView();
        if (view.getParent() != null) {
            throw AdaptException.create(String.format(Locale.ROOT,
                    "Returned view already has a parent, index: %d, item: %s", index, item));
        }
        view.setTag(ID_HOLDER, holder);
        configuration.changeHandler.insertAt(viewGroup, view, index);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void render(int index, @NonNull Item item) {
        final View view = viewGroup.getChildAt(index);
        final Item.Holder holder = (Item.Holder) view.getTag(ID_HOLDER);
        if (holder == null) {
            throw AdaptException.create(String.format(Locale.ROOT,
                    "Internal error, attached view has no Holder saved, " +
                            "index: %d, item: %s, view: %s", index, item, view));
        }
        //noinspection unchecked
        item.bind(holder);
        view.setTag(ID_ITEM, item);
    }

    private static class ConfigurationImpl implements Configuration {

        private LayoutInflater inflater;
        private AdaptViewGroupDiff adaptViewGroupDiff = AdaptViewGroupDiff.create();
        private ChangeHandler changeHandler = new ViewGroupChangeHandler();

        @NonNull
        @Override
        public Configuration layoutInflater(@NonNull LayoutInflater inflater) {
            this.inflater = inflater;
            return this;
        }

        @NonNull
        @Override
        public Configuration adaptViewGroupDiff(@NonNull AdaptViewGroupDiff adaptViewGroupDiff) {
            this.adaptViewGroupDiff = adaptViewGroupDiff;
            return this;
        }

        @NonNull
        @Override
        public Configuration changeHandler(@NonNull ChangeHandler changeHandler) {
            this.changeHandler = changeHandler;
            return this;
        }
    }
}
