package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

/**
 * Class to render an {@link Item} to a regular android.view.View
 *
 * @see #append(ViewGroup, Item)
 * @see #append(LayoutInflater, ViewGroup, Item)
 * @see #create(View, Item, HolderProvider)
 * @see #create(View, int, Item, HolderProvider)
 * @since 2.0.0
 */
public abstract class AdaptView<I extends Item> {

    public interface HolderProvider<H extends Item.Holder> {
        @NonNull
        H provide(@NonNull View view);
    }

    /**
     * Appends item view to specified ViewGroup and renders it.
     *
     * @see #append(LayoutInflater, ViewGroup, Item)
     */
    @NonNull
    public static <I extends Item> AdaptView<I> append(@NonNull ViewGroup group, @NonNull I item) {
        return append(LayoutInflater.from(group.getContext()), group, item);
    }

    /**
     * Appends item view to specified ViewGroup and renders it.
     */
    @NonNull
    public static <I extends Item> AdaptView<I> append(
            @NonNull LayoutInflater inflater,
            @NonNull ViewGroup group,
            @NonNull I item) {
        final Item.Holder holder = item.createHolder(inflater, group);
        final View view = holder.itemView;
        view.setTag(R.id.adapt_internal_holder, holder);
        group.addView(view);
        //noinspection unchecked
        item.render(holder);
        view.setTag(R.id.adapt_internal_item, item);
        return new Impl<>(view);
    }

    /**
     * Create {@link AdaptView} with the specified view as holder\'s itemView.
     *
     * @since 2.0.0
     */
    @NonNull
    public static <H extends Item.Holder, I extends Item<H>> AdaptView<I> create(
            @NonNull View view,
            @NonNull I item,
            @NonNull HolderProvider<H> holderProvider) {
        final H holder = holderProvider.provide(view);
        view.setTag(R.id.adapt_internal_holder, holder);
        item.render(holder);
        view.setTag(R.id.adapt_internal_item, item);
        return new Impl<>(view);
    }

    /**
     * @since 2.0.0
     */
    @NonNull
    public static <H extends Item.Holder, I extends Item<H>> AdaptView<I> create(
            @NonNull View parent,
            @IdRes int viewId,
            @NonNull I item,
            @NonNull HolderProvider<H> holderProvider) {
        final View view = ViewUtils.requireView(parent, viewId);
        final H holder = holderProvider.provide(view);
        view.setTag(R.id.adapt_internal_holder, holder);
        item.render(holder);
        view.setTag(R.id.adapt_internal_item, item);
        return new Impl<>(view);
    }


    public abstract void setItem(@NonNull I item);

    @NonNull
    public abstract I getCurrentItem();

    /**
     * @since 2.2.0-SNAPSHOT
     */
    @NonNull
    public abstract View view();


    static class Impl<I extends Item> extends AdaptView<I> {

        private final View view;

        Impl(@NonNull View view) {
            this.view = view;
        }

        @Override
        public void setItem(@NonNull I item) {

            final Item currentItem = getCurrentItem();

            final Item.Holder holder = (Item.Holder) view.getTag(R.id.adapt_internal_holder);
            if (holder == null) {
                // it's required to have holder at this point, internal error
                throw AdaptException.create("Unexpected state, there is no Holder associated " +
                        "with this view, supplied item: %s, view: %s", item, view);
            }

            if (currentItem.viewType() != item.viewType()) {
                // different type is supplied, cannot proceed
                throw AdaptException.create("Supplied item has different view-type as previously " +
                                "bound one, previous: `%s`, supplied: `%s`, view: %s",
                        currentItem, item, view);
            }

            //noinspection unchecked
            item.render(holder);

            // save item information
            view.setTag(R.id.adapt_internal_item, item);
        }

        @NonNull
        @Override
        public I getCurrentItem() {
            //noinspection unchecked
            final I currentItem = (I) view.getTag(R.id.adapt_internal_item);
            if (currentItem == null) {
                // unexpected internal error, no item is specified (we cannot create an AdaptView without
                // item)
                throw AdaptException.create("Unexpected state, there is no item bound, " +
                        "view: %s", view);
            }
            return currentItem;
        }

        @NonNull
        @Override
        public View view() {
            return view;
        }
    }
}
