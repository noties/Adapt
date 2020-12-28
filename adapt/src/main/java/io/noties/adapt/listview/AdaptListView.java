package io.noties.adapt.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.noties.adapt.Adapt;
import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.R;
import io.noties.adapt.util.ListUtils;

// NB! The name can be AdaptAdapterView, but AdaptListView is used for better discoverability
public class AdaptListView implements Adapt {

    public interface Configuration {

        interface EnabledProvider<I extends Item<? extends Item.Holder>> {
            boolean isEnabled(@NonNull I item);
        }

        /**
         * @see Adapter#hasStableIds()
         * By default {@code false}
         */
        @NonNull
        Configuration hasStableIds(boolean hasStableIds);

        /**
         * NB, documentation mentions that disabled items are separators
         * (they are also not clickable).
         *
         * @see ListAdapter#areAllItemsEnabled()
         * By default {@code false}
         */
        @NonNull
        Configuration areAllItemsEnabled(boolean areAllItemsEnabled);

        /**
         * Includes item (adds to itemViewTypes) and reports as `isEnabled = false`
         *
         * @param type item to include
         * @see #include(Class, boolean)
         * @see #include(Class, EnabledProvider)
         */
        @NonNull
        Configuration include(@NonNull Class<? extends Item<?>> type);

        /**
         * @param type      item to include
         * @param isEnabled all items of this type will return from `isEnabled` method call (will be
         *                  considered separator and won\'t have click listener attached)
         * @see #include(Class)
         * @see #include(Class, EnabledProvider)
         */
        @NonNull
        Configuration include(@NonNull Class<? extends Item<?>> type, boolean isEnabled);

        /**
         * @param type     item to include
         * @param provider to report `isEnabled` state
         * @see #include(Class)
         * @see #include(Class, boolean)
         */
        @NonNull
        <I extends Item<? extends Item.Holder>> Configuration include(
                @NonNull Class<? extends I> type,
                @NonNull EnabledProvider<I> provider
        );
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
     * about containing AdapterView (for example when used in an {@code AlertDialog}
     *
     * <strong>NB</strong> if there are multiple item views then items
     * must be explicitly registered via one of the {@code Configuration.include} methods:
     * <ul>
     *     <li>{@link Configuration#include(Class)}</li>
     *     <li>{@link Configuration#include(Class, Configuration.EnabledProvider)}</li>
     *     <li>{@link Configuration#include(Class, boolean)}</li>
     * </ul>
     */
    @NonNull
    public static AdaptListView create(
            @NonNull Context context,
            @NonNull Configurator configurator) {
        final ConfigurationImpl configuration = new ConfigurationImpl();
        configurator.configure(configuration);
        return new AdaptListView(context, null, configuration);
    }

    @SuppressWarnings("rawtypes")
    @Nullable
    private final AdapterView adapterView;
    private final ConfigurationImpl configuration;
    private final AdapterImpl adapter;

    private final Map<Class<? extends Item<?>>, Integer> viewTypes = new HashMap<>(3);
    private final Map<Class<? extends Item<?>>, Configuration.EnabledProvider<? extends Item<?>>> isEnabled;

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

        this.isEnabled = new HashMap<>(configuration.isEnabled);

        for (Map.Entry<Class<? extends Item<?>>, Configuration.EnabledProvider<? extends Item<?>>> entry : isEnabled.entrySet()) {
            viewTypes.put(entry.getKey(), viewTypesCount++);
        }
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
    public List<Item<? extends Item.Holder>> items() {
        return ListUtils.freeze(items);
    }

    @Override
    public void setItems(@Nullable List<Item<? extends Item.Holder>> items) {
        // we must calculate _total_ view item types, if amount changed (we see new items, we must invalidate listView,
        //  by setting adapter again)
        this.items = items;

        // but only if not viewTypesCount = 1 (default value) ?
        if (assignViewTypes(items)
                && viewTypesCount > 1) {

            if (adapterView == null) {
                throw AdaptException.create("Register all item views explicitly when " +
                        "creating AdaptListView in a detached from AdapterView way");
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
    public void notifyItemChanged(@NonNull Item<? extends Item.Holder> item) {
        // ListView cannot? update individual item
        adapter.notifyDataSetChanged();
    }

    // return if adapter has new view types and thus must be invalidated
    private boolean assignViewTypes(@Nullable List<Item<? extends Item.Holder>> items) {
        if (ListUtils.isEmpty(items)) {
            return false;
        }

        boolean hasNew = false;

        for (Class<? extends Item<?>> type : prepareItemTypes(items)) {
            final Integer viewType = viewTypes.get(type);
            if (viewType == null) {
                // not found
                hasNew = true;

                // generate new (take current count and increment it)
                viewTypes.put(type, viewTypesCount++);
            }
        }

        return hasNew;
    }

    @NonNull
    private static Set<Class<? extends Item<?>>> prepareItemTypes(@NonNull List<Item<? extends Item.Holder>> items) {
        final Set<Class<? extends Item<?>>> set = new HashSet<>(3);
        for (Item<?> item : items) {
            //noinspection unchecked
            set.add((Class<? extends Item<?>>) item.getClass());
        }
        return set;
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
            //noinspection rawtypes
            final Class<? extends Item> type = items.get(position).getClass();
            final Integer viewType = viewTypes.get(type);
            if (viewType == null) {
                throw AdaptException.create("Unexpected view type: %s", type.getName());
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

            final Configuration.EnabledProvider<? extends Item<?>> provider = isEnabled.get(item.getClass());
            //noinspection unchecked,rawtypes
            return provider != null
                    && ((Configuration.EnabledProvider) provider).isEnabled(item);
        }
    }

    private static class ConfigurationImpl implements Configuration {

        boolean hasStableIds = false;
        boolean areAllItemsEnabled = false;

        final Map<Class<? extends Item<?>>, EnabledProvider<? extends Item<?>>> isEnabled
                = new HashMap<>(3);

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
            include(type, false);
            return this;
        }

        @NonNull
        @Override
        public Configuration include(@NonNull Class<? extends Item<?>> type, boolean isEnabled) {
            //noinspection unchecked
            this.isEnabled.put(type, isEnabled ? EnabledProviderTrue.I : null);
            return this;
        }

        @NonNull
        @Override
        public <I extends Item<? extends Item.Holder>> Configuration include(@NonNull Class<? extends I> type, @NonNull EnabledProvider<I> provider) {
            isEnabled.put(type, provider);
            return this;
        }

        @SuppressWarnings("rawtypes")
        private static class EnabledProviderTrue implements EnabledProvider {

            static final EnabledProviderTrue I = new EnabledProviderTrue();

            @Override
            public boolean isEnabled(@NonNull Item item) {
                return true;
            }
        }
    }
}
