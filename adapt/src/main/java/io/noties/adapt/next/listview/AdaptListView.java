package io.noties.adapt.next.listview;

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

import io.noties.adapt.R;
import io.noties.adapt.next.Adapt;
import io.noties.adapt.next.AdaptException;
import io.noties.adapt.next.Item;
import io.noties.adapt.next.utils.ListUtils;

// NB! The name can be AdaptAdapterView, but AdaptListView is used for better discoverability
public class AdaptListView implements Adapt {

    public interface Configuration {
        /**
         * @see Adapter#hasStableIds()
         * By default {@code false}
         */
        @NonNull
        Configuration hasStableIds(boolean hasStableIds);

        /**
         * NB, documentation mentions that disabled items are separators
         * (they are also not clickable)
         *
         * @see ListAdapter#areAllItemsEnabled()
         * By default {@code true}
         */
        @NonNull
        Configuration areAllItemsEnabled(boolean areAllItemsEnabled);
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

    // options:
    // * pre-calculated amount of view types (manually assign?)

    // TODO: no diff animations

    @SuppressWarnings("rawtypes")
    @NonNull
    public static AdaptListView init(@NonNull AdapterView adapterView) {
        final AdaptListView adaptListView = new AdaptListView(adapterView, new ConfigurationImpl());
        //noinspection unchecked
        adapterView.setAdapter(adaptListView.adapter());
        return adaptListView;
    }

    @SuppressWarnings("rawtypes")
    @NonNull
    public static AdaptListView init(@NonNull AdapterView adapterView, @NonNull Configurator configurator) {
        final ConfigurationImpl configuration = new ConfigurationImpl();
        configurator.configure(configuration);
        final AdaptListView adaptListView = new AdaptListView(adapterView, configuration);
        //noinspection unchecked
        adapterView.setAdapter(adaptListView.adapter());
        return adaptListView;
    }

    @SuppressWarnings("rawtypes")
    private final AdapterView adapterView;
    private final ConfigurationImpl configuration;
    private final AdapterImpl adapter;

    @SuppressWarnings("rawtypes")
    private final Map<Class<? extends Item>, Integer> viewTypes = new HashMap<>(3);
    private int viewTypesCount = 0;

    private List<Item<?>> items;

    @SuppressWarnings("rawtypes")
    AdaptListView(@NonNull AdapterView adapterView, @NonNull ConfigurationImpl configuration) {
        this.adapterView = adapterView;
        this.configuration = configuration;
        this.adapter = new AdapterImpl(adapterView.getContext());
    }

    @NonNull
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
        // TODO: defensive copy here?
        this.items = items;

        // but only if not viewTypesCount = 1 (default value) ?
        if (assignViewTypes(items)
                && viewTypesCount > 1) {
            //noinspection unchecked
            adapterView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    // return if adapter has new view types and thus must be invalidated
    // TODO: allow to pre-populate factory
    private boolean assignViewTypes(@Nullable List<Item<? extends Item.Holder>> items) {
        if (ListUtils.isEmpty(items)) {
            return false;
        }

        boolean hasNew = false;

        //noinspection rawtypes
        for (Class<? extends Item> type : prepareItemTypes(items)) {
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

    @SuppressWarnings("rawtypes")
    @NonNull
    private static Set<Class<? extends Item>> prepareItemTypes(@NonNull List<Item<? extends Item.Holder>> items) {
        final Set<Class<? extends Item>> set = new HashSet<>(3);
        for (Item<?> item : items) {
            set.add(item.getClass());
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
            item.render(holder);

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
            // TODO: check if we need to validate that option, maybe it is called anyway
            if (configuration.areAllItemsEnabled) {
                return true;
            }

            //noinspection rawtypes
            final Item item = items.get(position);

            if (!(item instanceof ListViewItem)) {
                // if not -> then default true
                return true;
            }

            return ((ListViewItem) item).listViewIsEnabled();
        }
    }

    private static class ConfigurationImpl implements Configuration {

        boolean hasStableIds = false;
        boolean areAllItemsEnabled = true;

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
    }
}
