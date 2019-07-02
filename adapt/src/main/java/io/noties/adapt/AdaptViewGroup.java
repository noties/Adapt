package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 2.0.0
 */
public abstract class AdaptViewGroup {

    public interface Builder {

        @NonNull
        Builder layoutInflater(@NonNull LayoutInflater inflater);

        @NonNull
        Builder adaptViewGroupDiff(@NonNull AdaptViewGroupDiff adaptViewGroupDiff);

        @NonNull
        AdaptViewGroup build();
    }


    @NonNull
    public static AdaptViewGroup create(@NonNull ViewGroup group) {
        return builder(group).build();
    }

    @NonNull
    public static Builder builder(@NonNull ViewGroup group) {
        return new BuilderImpl(group);
    }

    @NonNull
    public abstract ViewGroup viewGroup();

    public abstract void setItems(@Nullable List<Item> items);

    /**
     * Please note that this method has side effects. All views (children) that are not added
     * via Adapt methods will be <strong>removed</strong> (views that have no associated Item and Item.Holder)
     */
    @NonNull
    public abstract List<Item> getCurrentItems();


    static class Impl extends AdaptViewGroup implements AdaptViewGroupDiff.Parent {

        private final ViewGroup group;
        private final LayoutInflater layoutInflater;
        private final AdaptViewGroupDiff diff;

        Impl(
                @NonNull ViewGroup group,
                @NonNull LayoutInflater layoutInflater,
                @NonNull AdaptViewGroupDiff adaptViewGroupDiff) {
            this.group = group;
            this.layoutInflater = layoutInflater;
            this.diff = adaptViewGroupDiff;
        }

        @NonNull
        @Override
        public ViewGroup viewGroup() {
            return group;
        }

        @Override
        public void setItems(@Nullable List<Item> items) {

            if (items == null
                    || items.isEmpty()) {
                // no need to validate what we have at this point -> nothing should be displayed
                group.removeAllViews();
                return;
            }

            diff.diff(this, getCurrentItems(), items);
        }

        @NonNull
        @Override
        public List<Item> getCurrentItems() {

            final List<Item> items = new ArrayList<>(group.getChildCount());

            View view;
            Item item;

            // important to NOT increment index variable (we are removing views)
            // also important to always ask for childCount (we are removing views and this variable can change)
            for (int i = 0; i < group.getChildCount(); /*no increment*/) {
                view = group.getChildAt(i);
                item = (Item) view.getTag(R.id.adapt_internal_item);
                if (item == null) {
                    group.removeViewAt(i);
                } else {
                    items.add(item);
                    i += 1;
                }
            }

            return items;
        }

        @Override
        public void removeAt(int index) {
            group.removeViewAt(index);
        }

        @Override
        public void move(int from, int to) {
            final View child = group.getChildAt(from);
            group.removeViewAt(from);
            group.addView(child, to);
        }

        @Override
        public void insertAt(int index, @NonNull Item item) {
            final Item.Holder holder = item.createHolder(layoutInflater, group);
            final View view = holder.itemView;
            view.setTag(R.id.adapt_internal_holder, holder);
            group.addView(view, index);
        }

        @Override
        public void render(int index, @NonNull Item item) {

            // here we actually can have a special method to check if an item has changed (diff doesn't
            // need to know that), but that would require us to have previous value here... should we also
            // check that types match?

            final View view = group.getChildAt(index);
            final Item.Holder holder = (Item.Holder) view.getTag(R.id.adapt_internal_holder);

            if (holder == null) {
                throw AdaptException.create("Internal error, attached view has no Holder saved, " +
                        "view: " + view);
            }

            //noinspection unchecked
            item.render(holder);

            view.setTag(R.id.adapt_internal_item, item);
        }
    }

    static class BuilderImpl implements Builder {

        private final ViewGroup group;

        private LayoutInflater layoutInflater;
        private AdaptViewGroupDiff adaptViewGroupDiff;

        BuilderImpl(@NonNull ViewGroup group) {
            this.group = group;
        }

        @NonNull
        @Override
        public Builder layoutInflater(@NonNull LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
            return this;
        }

        @NonNull
        @Override
        public Builder adaptViewGroupDiff(@NonNull AdaptViewGroupDiff adaptViewGroupDiff) {
            this.adaptViewGroupDiff = adaptViewGroupDiff;
            return this;
        }

        @NonNull
        @Override
        public AdaptViewGroup build() {

            final LayoutInflater inflater = this.layoutInflater != null
                    ? this.layoutInflater
                    : LayoutInflater.from(group.getContext());

            final AdaptViewGroupDiff adaptViewGroupDiff = this.adaptViewGroupDiff != null
                    ? this.adaptViewGroupDiff
                    : AdaptViewGroupDiff.create();

            return new Impl(group, inflater, adaptViewGroupDiff);
        }
    }
}
