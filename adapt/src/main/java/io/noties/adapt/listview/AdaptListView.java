package io.noties.adapt.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.noties.adapt.Adapt;
import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.ItemWrapper;
import io.noties.adapt.R;
import io.noties.adapt.util.ListUtils;

// NB! The name can be AdaptAdapterView, but AdaptListView is used for better discoverability
public class AdaptListView implements Adapt {

    private static final boolean DEF_ENABLED = false;

    public interface Configuration {

        /**
         * @see Adapter#hasStableIds()
         * By default {@code true}
         */
        @NonNull
        @CheckResult
        Configuration hasStableIds(boolean hasStableIds);

        /**
         * NB, documentation mentions that disabled items are separators
         * (they are also not clickable).
         *
         * @see ListAdapter#areAllItemsEnabled()
         * By default {@code false}
         */
        @NonNull
        @CheckResult
        Configuration areAllItemsEnabled(boolean areAllItemsEnabled);

        /**
         * Includes item (adds to itemViewTypes) and reports as `isEnabled = false`. Please specify
         * <em>unwrapped</em> (or root) item class
         *
         * <strong>NB</strong> including an {@link ItemWrapper} won\'t achieve desired effect
         * here, as each combination of wrappers results in a different view type. Specify
         * here <strong>only regular items</strong>
         *
         * @param type item to include
         * @see #include(Class, boolean)
         */
        @NonNull
        @CheckResult
        Configuration include(@NonNull Class<? extends Item<?>> type);

        /**
         * @param type      item to include
         * @param isEnabled all items of this type will return from `isEnabled` method call (will be
         *                  considered separator and won\'t have click listener attached)
         * @see #include(Class)
         */
        @NonNull
        @CheckResult
        Configuration include(@NonNull Class<? extends Item<?>> type, boolean isEnabled);

        @NonNull
        @CheckResult
        Configuration include(@NonNull Item.Key key);

        @NonNull
        @CheckResult
        Configuration include(@NonNull Item.Key key, boolean isEnabled);
    }

    public interface Configurator {
        void configure(@NonNull Configuration configuration);
    }

    /*
    Spinner does not support more than 1 view type:
        if (targetSdkVersion >= Build.VERSION_CODES.LOLLIPOP && adapter != null && adapter.getViewTypeCount() != 1) {
            throw new IllegalArgumentException("Spinner adapter view type count must be 1");
        }
     */

    @SuppressWarnings("rawtypes")
    @NonNull
    public static AdaptListView init(@NonNull AdapterView adapterView) {
        final AdaptListView adaptListView = new AdaptListView(
                adapterView.getContext(),
                adapterView,
                new ConfigurationImpl()
        );
        //noinspection unchecked
        adapterView.setAdapter(adaptListView.adapter());
        return adaptListView;
    }

    @SuppressWarnings("rawtypes")
    @NonNull
    public static AdaptListView init(@NonNull AdapterView adapterView, @NonNull Configurator configurator) {
        final ConfigurationImpl configuration = new ConfigurationImpl();
        configurator.configure(configuration);
        final AdaptListView adaptListView = new AdaptListView(
                adapterView.getContext(),
                adapterView,
                configuration
        );
        //noinspection unchecked
        adapterView.setAdapter(adaptListView.adapter());
        return adaptListView;
    }

    /**
     * A special factory method to create AdaptListView to be used when there is no information
     * about containing AdapterView (for example when used in an {@code AlertDialog} or a {@code Spinner}
     *
     * <strong>NB</strong> if there are multiple item views then items
     * must be explicitly registered via one of the {@code Configuration.include} methods:
     * <ul>
     *     <li>{@link Configuration#include(Class)}</li>
     *     <li>{@link Configuration#include(Class, boolean)}</li>
     * </ul>
     *
     * <strong>NB</strong> resulting {@link AdaptListView} won\'t be able to handle items wrapped
     * with {@link ItemWrapper}s
     */
    @NonNull
    public static AdaptListView create(
            @NonNull Context context,
            @NonNull Configurator configurator
    ) {
        final ConfigurationImpl configuration = new ConfigurationImpl();
        configurator.configure(configuration);
        return new AdaptListView(context, null, configuration);
    }

    /**
     * Special factory method that creates {@code AdaptListView} with single {@code viewType}.
     * Can be used with Android {@code android.widget.Spinner} or {@code AlertDialog}
     *
     * @see #create(Context, Configurator)
     */
    @NonNull
    public static AdaptListView createSingleViewType(@NonNull Context context) {
        return new AdaptListView(context, null, new ConfigurationImpl());
    }

    @SuppressWarnings("rawtypes")
    @Nullable
    private final AdapterView adapterView;
    private final ConfigurationImpl configuration;
    private final AdapterImpl adapter;

    // cache for AdapterView viewTypes, which must be sequential (internally creates array)
    // NB! key is viewType returned from Item, value is _assigned_ viewType in this AdapterView
    private final Map<Integer, Integer> viewTypes;

    // enabled info (key is the viewType of an Item)
    private final Map<Integer, Boolean> isEnabled;

    private int viewTypesCount = 0;

    private List<Item<?>> items;

    @SuppressWarnings("rawtypes")
    AdaptListView(
            @NonNull Context context,
            @Nullable AdapterView adapterView,
            @NonNull ConfigurationImpl configuration
    ) {
        this.adapterView = adapterView;
        this.configuration = configuration;
        this.adapter = new AdapterImpl(context);

        // if all enabled -> not need to create and maintain collection
        final boolean areAllItemsEnabled = configuration.areAllItemsEnabled;
        final int capacity = configuration.types.size();
        final Map<Integer, Integer> viewTypes = new HashMap<>(capacity);
        final Map<Integer, Boolean> isEnabled = areAllItemsEnabled
                ? Collections.<Integer, Boolean>emptyMap()
                : new HashMap<Integer, Boolean>(capacity);

        for (Map.Entry<Item.Key, Boolean> entry : configuration.types.entrySet()) {
            final int itemViewType = entry.getKey().viewType();
            if (!areAllItemsEnabled) {
                isEnabled.put(itemViewType, entry.getValue());
            }
            viewTypes.put(itemViewType, viewTypesCount++);
        }
        this.viewTypes = viewTypes;
        this.isEnabled = isEnabled;
    }

    @Nullable
    public AdapterView<?> adapterView() {
        return adapterView;
    }

    @NonNull
    public AdaptListViewAdapter adapter() {
        return adapter;
    }

    @NonNull
    @Override
    public List<Item<?>> items() {
        return ListUtils.freeze(items);
    }

    @Override
    public void setItems(@Nullable List<Item<?>> items) {
        // we must calculate _total_ view item types, if amount changed (we see new items, we must invalidate listView,
        //  by setting adapter again)
        this.items = items;

        // but only if not viewTypesCount = 1 (default value) ?
        if (assignViewTypes(items)
                && viewTypesCount > 1) {

            if (adapterView == null) {
                throw AdaptException.create("Register all item views explicitly when " +
                        "creating AdaptListView in a detached from AdapterView way (for ex. AlertDialog).");
            }

            //noinspection unchecked
            adapterView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyAllItemsChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemChanged(@NonNull Item<?> item) {
        // ListView cannot? update individual item
        adapter.notifyDataSetChanged();
    }

    // return if adapter has new view types and thus must be invalidated
    private boolean assignViewTypes(@Nullable List<Item<?>> items) {
        if (ListUtils.isEmpty(items)) {
            return false;
        }

        boolean hasNew = false;

        int viewType;
        Integer listViewType;

        for (Item<?> item : items) {
            viewType = item.viewType();
            listViewType = viewTypes.get(viewType);
            if (listViewType == null) {
                hasNew = true;
                viewTypes.put(viewType, viewTypesCount++);
            }
        }

        return hasNew;
    }

    private class AdapterImpl extends BaseAdapter implements AdaptListViewAdapter {

        private final LayoutInflater inflater;

        AdapterImpl(@NonNull Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return ListUtils.size(items);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).id();
        }

        @Override
        public int getItemViewType(int position) {
            final Item<?> item = items.get(position);
            final Integer viewType = viewTypes.get(item.viewType());
            if (viewType == null) {
                throw AdaptException.create(String.format(Locale.ROOT, "Unexpected item " +
                        "at position: %d, item: %s (no view type is associated)", position, item));
            }
            return viewType;
        }

        @Override
        public int getViewTypeCount() {
            // ensure at least 1 item view type
            return Math.max(1, viewTypesCount);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View view;
            final Item.Holder holder;

            //noinspection rawtypes
            final Item item = items.get(position);

            if (convertView == null) {
                holder = item.createHolder(inflater, parent);
                holder.setAdapt(AdaptListView.this);
                view = holder.itemView();
                view.setTag(R.id.adapt_internal_listview_holder_tag, holder);
            } else {
                view = convertView;
                holder = (Item.Holder) view.getTag(R.id.adapt_internal_listview_holder_tag);
            }

            //noinspection unchecked
            item.bind(holder);

            return view;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return configuration.areAllItemsEnabled;
        }

        @Override
        public boolean hasStableIds() {
            return configuration.hasStableIds;
        }

        @Override
        public boolean isEnabled(int position) {

            if (configuration.areAllItemsEnabled) {
                return true;
            }

            final Item<?> item = items.get(position);
            final Boolean value = isEnabled.get(item.viewType());
            return value != null
                    ? value
                    : DEF_ENABLED;
        }
    }

    private static class ConfigurationImpl implements Configuration {

        boolean hasStableIds = true;
        boolean areAllItemsEnabled = false;

        final Map<Item.Key, Boolean> types = new HashMap<>(3);

        @NonNull
        @Override
        public Configuration hasStableIds(boolean hasStableIds) {
            this.hasStableIds = hasStableIds;
            return this;
        }

        @NonNull
        @Override
        public Configuration areAllItemsEnabled(boolean areAllItemsEnabled) {
            this.areAllItemsEnabled = areAllItemsEnabled;
            return this;
        }

        @NonNull
        @Override
        public Configuration include(@NonNull Class<? extends Item<?>> type) {
            return include(type, DEF_ENABLED);
        }

        @NonNull
        @Override
        public Configuration include(@NonNull Class<? extends Item<?>> type, boolean isEnabled) {
            return include(Item.Key.single(type), isEnabled);
        }

        @NonNull
        @Override
        public Configuration include(@NonNull Item.Key key) {
            return include(key, DEF_ENABLED);
        }

        @NonNull
        @Override
        public Configuration include(@NonNull Item.Key key, boolean isEnabled) {
            types.put(key, isEnabled ? Boolean.TRUE : Boolean.FALSE);
            return this;
        }
    }
}
