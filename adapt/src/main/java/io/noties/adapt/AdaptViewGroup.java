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

        /**
         * @param changeHandler to handle layout changes
         * @since 2.3.0-SNAPSHOT
         */
        @NonNull
        Builder changeHandler(@NonNull ChangeHandler changeHandler);

        @NonNull
        AdaptViewGroup build();
    }

    /**
     * @see Builder#changeHandler(ChangeHandler)
     * @see ChangeHandlerDef
     * @see TransitionChangeHandler
     * @since 2.3.0-SNAPSHOT
     */
    public interface ChangeHandler {

        void begin(@NonNull ViewGroup group);

        void removeAll(@NonNull ViewGroup group);

        void removeAt(@NonNull ViewGroup group, int position);

        void move(@NonNull ViewGroup group, int from, int to);

        void insertAt(@NonNull ViewGroup group, @NonNull View view, int position);

        void end(@NonNull ViewGroup group);
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

    /**
     * @since 2.3.0-SNAPSHOT
     */
    public static class ChangeHandlerDef implements ChangeHandler {

        @Override
        public void begin(@NonNull ViewGroup group) {
            // no op
        }

        @Override
        public void removeAll(@NonNull ViewGroup group) {
            group.removeAllViews();
        }

        @Override
        public void removeAt(@NonNull ViewGroup group, int position) {
            group.removeViewAt(position);
        }

        @Override
        public void move(@NonNull ViewGroup group, int from, int to) {
            final View child = group.getChildAt(from);
            group.removeViewAt(from);
            group.addView(child, to);
        }

        @Override
        public void insertAt(@NonNull ViewGroup group, @NonNull View view, int position) {
            group.addView(view, position);
        }

        @Override
        public void end(@NonNull ViewGroup group) {
            // no op
        }
    }

    static class Impl extends AdaptViewGroup implements AdaptViewGroupDiff.Parent {

        private final ViewGroup group;
        private final LayoutInflater layoutInflater;
        private final AdaptViewGroupDiff diff;
        private final ChangeHandler changeHandler;

        Impl(
                @NonNull ViewGroup group,
                @NonNull LayoutInflater layoutInflater,
                @NonNull AdaptViewGroupDiff adaptViewGroupDiff,
                @NonNull ChangeHandler changeHandler) {
            this.group = group;
            this.layoutInflater = layoutInflater;
            this.diff = adaptViewGroupDiff;
            this.changeHandler = changeHandler;
        }

        @NonNull
        @Override
        public ViewGroup viewGroup() {
            return group;
        }

        @Override
        public void setItems(@Nullable List<Item> items) {

            changeHandler.begin(group);
            try {

                if (items == null
                        || items.isEmpty()) {
                    // no need to validate what we have at this point -> nothing should be displayed
                    changeHandler.removeAll(group);
                    return;
                }

                diff.diff(this, getCurrentItems(), items);

            } finally {
                changeHandler.end(group);
            }
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
            changeHandler.removeAt(group, index);
        }

        @Override
        public void move(int from, int to) {
            changeHandler.move(group, from, to);
        }

        @Override
        public void insertAt(int index, @NonNull Item item) {
            final Item.Holder holder = item.createHolder(layoutInflater, group);
            final View view = holder.itemView;

            // @since 2.3.0-SNAPSHOT validate that returned view has no parent
            if (view.getParent() != null) {
                throw AdaptException.create("Returned view already has parent. Make sure that " +
                        "you do not attach view manually or call `inflater.inflate(resId, parent)` or " +
                        "`inflater.inflate(resId, parent, true)` when inflating view. Item: %s", item);
            }

            view.setTag(R.id.adapt_internal_holder, holder);
            changeHandler.insertAt(group, view, index);
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
        private ChangeHandler changeHandler;

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
        public Builder changeHandler(@NonNull ChangeHandler changeHandler) {
            this.changeHandler = changeHandler;
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

            final ChangeHandler changeHandler = this.changeHandler != null
                    ? this.changeHandler
                    : new ChangeHandlerDef();

            return new Impl(group, inflater, adaptViewGroupDiff, changeHandler);
        }
    }


}
