package io.noties.adapt.view;

import android.os.Build;
import android.transition.TransitionManager;
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

    /**
     * @since $UNRELEASED;
     */
    public interface ChangeHandler {

        void begin(@NonNull ViewGroup container, @NonNull View view);

        void end(@NonNull ViewGroup container, @NonNull View view);
    }

    public interface Configurator {
        void configure(@NonNull Configuration configuration);
    }

    @NonNull
    @CheckResult
    public static AdaptView init(@NonNull ViewGroup viewGroup) {
        return new AdaptView(viewGroup, new Configuration());
    }

    @NonNull
    @CheckResult
    public static AdaptView init(@NonNull ViewGroup viewGroup, @NonNull Configurator configurator) {
        final Configuration configuration = new Configuration();
        configurator.configure(configuration);
        return new AdaptView(viewGroup, configuration);
    }

    /**
     * @since 4.0.0
     */
    @NonNull
    @CheckResult
    public static AdaptView init(@NonNull ViewGroup viewGroup, @NonNull Item<?> item) {
        final Configuration configuration = new Configuration();
        configuration.item(item);
        return new AdaptView(viewGroup, configuration);
    }

    static final int ID_HOLDER = R.id.adapt_internal_holder;
    static final int ID_ITEM = R.id.adapt_internal_item;

    @NonNull
    private final ViewGroup viewGroup;

    @Nullable
    private final ChangeHandler changeHandler; // @since $UNRELEASED;

    @NonNull
    private final LayoutInflater layoutInflater;

    private View view;

    AdaptView(
            @NonNull ViewGroup viewGroup,
            @NonNull Configuration configuration
    ) {
        this.viewGroup = viewGroup;
        this.changeHandler = configuration.changeHandler;

        LayoutInflater layoutInflater;
        this.layoutInflater = (layoutInflater = configuration.layoutInflater) != null
                ? layoutInflater
                : LayoutInflater.from(viewGroup.getContext());

        final View placeholderView = configuration.placeholderView;
        final int placeholderViewIndex = placeholderView != null
                ? viewGroup.indexOfChild(placeholderView)
                : -1;
        // Validate that placeholder view is a child of view-group
        if (placeholderView != null && placeholderViewIndex < 0) {
            throw AdaptException.create(String.format(
                    Locale.ROOT,
                    "Placeholder-view is not a child of view-group, placeholder:%s view-group:%s",
                    placeholderView,
                    viewGroup
            ));
        }

        final View view;
        final Item<?> item = configuration.item;
        if (item != null) {

            final Item.Holder holder = item.createHolder(this.layoutInflater, viewGroup);

            view = holder.itemView();
            view.setTag(ID_HOLDER, holder);
            view.setTag(ID_ITEM, item);

            if (placeholderView != null) {
                // replace created view in place of placeholder
                viewGroup.removeViewAt(placeholderViewIndex);
                viewGroup.addView(view, placeholderViewIndex);
            } else {
                // else just add it
                viewGroup.addView(view);
            }

            //noinspection unchecked,rawtypes
            ((Item) item).bind(holder);

        } else {
            if (placeholderView == null) {
                // we still need to create a mock view, so later we can add real one at the correct place
                view = newEmptyView();
                viewGroup.addView(view);
            } else {
                // else, just keep the placeholder
                view = placeholderView;
            }
        }

        if (placeholderView != null) {
            // clear the reference to it
            configuration.placeholderView = null;
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
        final ChangeHandler changeHandler = this.changeHandler;
        if (changeHandler != null) {
            changeHandler.begin(viewGroup, view);
        }

        final Item<?> currentItem = item();

        if (item == null) {
            // if we have no item at this point, then there is no need to create a new mocked view
            if (currentItem != null) {
                this.view = replaceView(newEmptyView());
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

        if (changeHandler != null) {
            changeHandler.end(viewGroup, view);
        }
    }

    public void notifyChanged() {
        setItem(item());
    }

    @NonNull
    private View newEmptyView() {
        final View view = new View(viewGroup.getContext());
        // provide explicit layout size to be 0, otherwise can be treated
        //  by some layouts as MATCH/MATCH
        view.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        return view;
    }

    private void createHolder(@NonNull Item<?> item) {

        final Item.Holder holder = item.createHolder(layoutInflater, viewGroup);
        holder.setAdapt(AdaptView.this);

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
        return Collections.singletonList(item());
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
        if (item.equals(current)) {
            notifyChanged();
        }
    }

    public static class Configuration {

        @Nullable
        ChangeHandler changeHandler;

        @Nullable
        LayoutInflater layoutInflater;

        @Nullable
        Item<?> item;

        @Nullable
        View placeholderView;

        /**
         * @see #changeHandlerTransitionSelf()
         * @see #changeHandlerTransitionParent()
         * @since $UNRELEASED;
         */
        @NonNull
        public Configuration changeHandler(@NonNull ChangeHandler changeHandler) {
            this.changeHandler = changeHandler;
            return this;
        }

        @NonNull
        public Configuration layoutInflater(@NonNull LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
            return this;
        }

        @NonNull
        public Configuration item(@NonNull Item<?> item) {
            this.item = item;
            return this;
        }

        @NonNull
        public Configuration placeholderView(@NonNull View placeholderView) {
            this.placeholderView = placeholderView;
            return this;
        }

        /**
         * @since $UNRELEASED;
         */
        @NonNull
        public Configuration changeHandlerTransitionSelf() {
            this.changeHandler = new TransitionChangeHandler(true);
            return this;
        }

        /**
         * @see #changeHandler(ChangeHandler)
         * @since $UNRELEASED;
         */
        @NonNull
        public Configuration changeHandlerTransitionParent() {
            this.changeHandler = new TransitionChangeHandler(false);
            return this;
        }
    }

    private static class TransitionChangeHandler implements ChangeHandler {

        private final boolean self;

        TransitionChangeHandler(boolean self) {
            this.self = self;
        }

        @Override
        public void begin(@NonNull ViewGroup container, @NonNull View view) {
            final ViewGroup group = viewGroup(container, view);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TransitionManager.endTransitions(group);
            }
            TransitionManager.beginDelayedTransition(group);
        }

        @Override
        public void end(@NonNull ViewGroup container, @NonNull View view) {
            // no op
        }

        @NonNull
        private ViewGroup viewGroup(@NonNull ViewGroup container, @NonNull View view) {
            if (self) {
                if (view instanceof ViewGroup) {
                    return (ViewGroup) view;
                }
            }
            return container;
        }
    }
}
