package io.noties.adapt.viewpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Collections;
import java.util.List;

import io.noties.adapt.Adapt;
import io.noties.adapt.Item;
import io.noties.adapt.R;
import io.noties.adapt.util.ListUtils;

/**
 * @since $UNRELEASED;
 */
public class AdaptViewPager implements Adapt {

    public interface Configuration {
        @NonNull
        Configuration layoutInflater(@NonNull LayoutInflater inflater);

        @NonNull
        Configuration pageWidth(@FloatRange(from = 0.0, to = 1.0) float pageWidth);
    }

    public interface Configurator {
        void configure(@NonNull Configuration configuration);
    }

    @NonNull
    public static AdaptViewPager init(@NonNull ViewPager viewPager) {
        final ConfigurationImpl configuration = new ConfigurationImpl(viewPager.getContext());
        final AdaptViewPager adaptViewPager = new AdaptViewPager(viewPager, configuration);
        viewPager.setAdapter(adaptViewPager.pagerAdapter());
        return adaptViewPager;
    }

    @NonNull
    public static AdaptViewPager init(@NonNull ViewPager viewPager, @NonNull Configurator configurator) {
        final ConfigurationImpl configuration = new ConfigurationImpl(viewPager.getContext());
        configurator.configure(configuration);
        final AdaptViewPager adaptViewPager = new AdaptViewPager(viewPager, configuration);
        viewPager.setAdapter(adaptViewPager.pagerAdapter());
        return adaptViewPager;
    }

    @Nullable
    public static AdaptViewPager find(@NonNull ViewPager viewPager) {
        final PagerAdapter adapter = viewPager.getAdapter();
        if (adapter instanceof Adapter) {
            return ((Adapter) adapter).adaptViewPager();
        }
        return null;
    }

    private static final int ID_ITEM = R.id.adapt_internal_item;
    private static final int ID_HOLDER = R.id.adapt_internal_holder;

    private final ViewPager viewPager;
    private final ConfigurationImpl configuration;
    private final Adapter adapter;

    private List<Item<?>> items = Collections.emptyList();

    AdaptViewPager(@NonNull ViewPager viewPager, @NonNull ConfigurationImpl configuration) {
        this.viewPager = viewPager;
        this.configuration = configuration;
        this.adapter = new Adapter();
    }

    @NonNull
    public PagerAdapter pagerAdapter() {
        return adapter;
    }

    @NonNull
    @Override
    public List<Item<?>> items() {
        return ListUtils.freeze(items);
    }

    @Override
    public void setItems(@Nullable List<Item<?>> items) {
        this.items = ListUtils.freeze(items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyAllItemsChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemChanged(@NonNull Item<?> item) {
        final int count = viewPager.getChildCount();
        View view;
        for (int i = 0; i < count; i++) {
            view = viewPager.getChildAt(i);
            if (item.equals(view.getTag(ID_ITEM))) {
                final Item.Holder holder = (Item.Holder) view.getTag(ID_HOLDER);
                if (holder != null) {
                    //noinspection unchecked,rawtypes,
                    ((Item) item).bind(holder);
                }
            }
        }
    }

    @Nullable
    public View findViewForAdapterPosition(int position) {
        //noinspection rawtypes
        final Item item = items.get(position);

        View view;

        for (int i = 0, count = viewPager.getChildCount(); i < count; i++) {
            view = viewPager.getChildAt(i);
            if (item.equals(view.getTag(ID_ITEM))) {
                return view;
            }
        }

        return null;
    }

    private class Adapter extends PagerAdapter {

        @NonNull
        AdaptViewPager adaptViewPager() {
            return AdaptViewPager.this;
        }

        @Override
        public int getCount() {
            return ListUtils.size(items);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            //noinspection rawtypes
            final Item item = items.get(position);
            final Item.Holder holder = item.createHolder(configuration.inflater, container);
            holder.setAdapt(AdaptViewPager.this);

            final View view = holder.itemView();
            view.setTag(ID_ITEM, item);
            view.setTag(ID_HOLDER, holder);

            //noinspection unchecked
            item.bind(holder);

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

            final int count = viewPager.getChildCount();

            View view;
            //noinspection rawtypes
            Item item;
            Item.Holder holder;

            for (int i = 0; i < count; i++) {
                view = viewPager.getChildAt(i);
                //noinspection rawtypes
                item = (Item) view.getTag(ID_ITEM);
                holder = (Item.Holder) view.getTag(ID_HOLDER);
                if (item != null && holder != null) {
                    //noinspection unchecked
                    item.bind(holder);
                }
            }
        }

        @Override
        public float getPageWidth(int position) {
            return configuration.pageWidth;
        }
    }

    private static class ConfigurationImpl implements Configuration {

        private LayoutInflater inflater;
        private float pageWidth = 1F;

        ConfigurationImpl(@NonNull Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public Configuration layoutInflater(@NonNull LayoutInflater inflater) {
            this.inflater = inflater;
            return this;
        }

        @NonNull
        @Override
        public Configuration pageWidth(float pageWidth) {
            this.pageWidth = pageWidth;
            return this;
        }
    }
}
