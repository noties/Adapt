package io.noties.adapt.recyclerview;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.view.AdaptView;

/**
 * RecyclerView.ItemDecoration that <em>stick</em> an {@link Item} to the top of a
 * RecyclerView (aka sticky header or section). Internally uses a duplicated view in layout (data is bound
 * through {@link AdaptView}). Reverse layout is supported. Also as sticky view is in layout
 * all widget accessibility features are available. Can be used with GridLayoutManager if sticky view
 * fills all the spans of GridLayoutManager (matches width).
 * <p>
 * NB! {@code RecyclerView} must a direct child of a {@code FrameLayout}
 * <p>
 * NB! do not use {@code margins} for {@code RecyclerView} as sticky view will be positioned
 * incorrectly, instead consider using margins on direct parent {@code FrameLayout} or padding
 * on {@code RecyclerView} itself combined with {@code clipToPadding=false} if vertical padding is present (top or bottom)
 */
public class StickyItemDecoration extends RecyclerView.ItemDecoration {

    @NonNull
    public static StickyItemDecoration create(
            @NonNull RecyclerView recyclerView,
            @NonNull Item<?> item
    ) {
        final ViewGroup parent = processRecyclerView(recyclerView);
        return new StickyItemDecoration(AdaptView.init(parent, item));
    }

    @NonNull
    private static ViewGroup processRecyclerView(@NonNull RecyclerView recyclerView) {

        final ViewParent parent = recyclerView.getParent();
        if (parent == null) {
            throw AdaptException.create("RecyclerView must be attached to a FrameLayout parent");
        }

        if (!(parent instanceof FrameLayout)) {
            throw AdaptException.create("RecyclerView parent must be FrameLayout, now: " + parent);
        }

        final FrameLayout layout = (FrameLayout) parent;
        if (layout.getPaddingLeft() != 0
                || layout.getPaddingTop() != 0
                || layout.getPaddingRight() != 0
                || layout.getPaddingBottom() != 0) {
            // issue a warning to use margins
            Log.w("Adapt", "StickyItemDecoration, FrameLayout parent uses padding, " +
                    "use margins instead");
        }

        if (!recyclerView.getClipToPadding()) {
            if (recyclerView.getPaddingTop() != 0
                    || recyclerView.getPaddingBottom() != 0) {
                Log.w("Adapt", "StickyItemDecoration, RecyclerView uses vertical " +
                        "padding without `clipToPadding=false`");
            }
        }

        return layout;
    }

    private static final int MEASURE_SPEC_UNSPECIFIED =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

    private final AdaptView adaptView;
    private final int stickyViewType;

    // flag that is used to invalidate sticky view
    //  as it is inside a different than RecyclerView parent, then parent might measure/layout it
    //  independently, so we must listen fro such an event, so we can invalidate sticky view accordingly
    private boolean adaptViewInvalidated;

    // For the future, multiple viewtypes can be sticky, even dynamically with a callback interface,
    //  as AdaptView is no longer typed and allows multiple item types
    StickyItemDecoration(@NonNull AdaptView adaptView) {
        this.adaptView = adaptView;
        this.stickyViewType = AdaptRecyclerView.assignedViewType(adaptView.item().getClass());

        prepareAdaptView(adaptView);
    }

    private void prepareAdaptView(@NonNull AdaptView adaptView) {
        hideStickyView();

        adaptView.view().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                adaptViewInvalidated = true;
            }
        });
    }


    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        // we must have children
        final int count = parent.getChildCount();
        if (count == 0) {
            hideStickyView();
            return;
        }

        // layout manager must be present
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager == null) {
            hideStickyView();
            return;
        }

        if (isReverseLayout(layoutManager)) {
            processReverseLayout(parent);
        } else {
            processRegularLayout(parent);
        }
    }

    protected boolean isReverseLayout(@NonNull RecyclerView.LayoutManager layoutManager) {
        return layoutManager instanceof LinearLayoutManager
                && ((LinearLayoutManager) layoutManager).getReverseLayout();
    }

    protected void processRegularLayout(@NonNull RecyclerView parent) {

        final View first = parent.getChildAt(0);
        final RecyclerView.ViewHolder holder = parent.findContainingViewHolder(first);

        // cannot do anything here
        if (holder == null) {
            hideStickyView();
            return;
        }

        final AdaptRecyclerView.Adapter<?> adapter = adapter(parent);
        if (adapter == null) {
            hideStickyView();
            return;
        }

        int position = holder.getAdapterPosition();
        Item<?> item = null;

        while (position >= 0) {
            if (stickyViewType == adapter.getItemViewType(position)) {
                item = adapter.getItem(position);
                break;
            }
            position -= 1;
        }

        if (item == null) {
            hideStickyView();
            return;
        }

        position += 1;

        int nextStickyViewTop = 0;
        final int adapterCount = adapter.getItemCount();

        while (position < adapterCount) {
            if (stickyViewType == adapter.getItemViewType(position)) {
                // we also could use view dimensions from view in RecyclerView (left,top,width,height and do not
                //  measure explicitly - it allow grid with different spans count)
                final RecyclerView.ViewHolder viewHolder = parent.findViewHolderForAdapterPosition(position);
                nextStickyViewTop = viewHolder != null
                        ? viewHolder.itemView.getTop()
                        : 0;
                break;
            }
            position += 1;
        }

        final View view = stickyView(item, parent);
        view.setAlpha(1F);

        final int height = view.getMeasuredHeight();
        final float y;
        if (nextStickyViewTop > 0 && nextStickyViewTop < height) {
            y = -(height - nextStickyViewTop);
        } else {
            y = 0F;
        }
        view.setTranslationY(y);
    }

    protected void processReverseLayout(@NonNull RecyclerView parent) {
        final LinearLayoutManager manager = (LinearLayoutManager) parent.getLayoutManager();
        if (manager == null) {
            hideStickyView();
            return;
        }

        // returns adapter position
        int position = manager.findLastVisibleItemPosition();
        if (position < 0) {
            // -1
            hideStickyView();
            return;
        }

        final AdaptRecyclerView.Adapter<?> adapter = adapter(parent);
        if (adapter == null) {
            hideStickyView();
            return;
        }

        final int adaptCount = adapter.getItemCount();

        Item<?> item = null;

        // okay, from this point we iterate forward to find next sticky item
        while (position < adaptCount) {
            if (stickyViewType == adapter.getItemViewType(position)) {
                item = adapter.getItem(position);
                break;
            }
            position += 1;
        }

        if (item == null) {
            hideStickyView();
            return;
        }

        position -= 1;

        int previousStickyViewTop = 0;
        while (position >= 0) {
            if (stickyViewType == adapter.getItemViewType(position)) {
                final RecyclerView.ViewHolder viewHolder = parent.findViewHolderForAdapterPosition(position);
                previousStickyViewTop = viewHolder != null
                        ? viewHolder.itemView.getTop()
                        : 0;
                break;
            }
            position -= 1;
        }

        final View view = stickyView(item, parent);
        view.setAlpha(1F);

        final int height = view.getHeight();
        final int y;

        if (previousStickyViewTop > 0 && previousStickyViewTop < height) {
            y = -(height - previousStickyViewTop);
        } else {
            y = 0;
        }
        view.setTranslationY(y);
    }

    @NonNull
    private View stickyView(
            @NonNull Item<?> item,
            @NonNull RecyclerView recyclerView
    ) {

        final AdaptView adaptView = this.adaptView;

        final Item<?> previousStickyItem = adaptView.item();

        // if adaptView has been through layout pass outside of this decorator, then request layout
        // if NO_ID, then request layout
        // if ids are different, then request layout

        final long id = item.id();

        if (adaptViewInvalidated
                || id == Item.NO_ID
                || id != previousStickyItem.id()) {

            adaptView.setItem(item);

            requestStickyLayout(adaptView.view(), recyclerView);

            // flip this flag in any case
            adaptViewInvalidated = false;
        }

        return adaptView.view();
    }


    private static void requestStickyLayout(@NonNull View view, @NonNull RecyclerView recyclerView) {

        final int left = recyclerView.getPaddingLeft();
        final int width = recyclerView.getWidth() - left - recyclerView.getPaddingRight();

        // @since 2.3.0-SNAPSHOT we check if view has exact height and use it
        final int heightMeasureSpec;
        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null
                || layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            heightMeasureSpec = MEASURE_SPEC_UNSPECIFIED;
        } else if (layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            // interesting case, can this even happen at all? can we scroll at all with this?
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.getHeight(), View.MeasureSpec.EXACTLY);
        } else {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY);
        }

        // what if view has specific width, why marching width of the recyclerView
        //  also, recyclerView can have clipChildren = false (padding would be ignored)
        view.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                heightMeasureSpec
        );

        view.layout(
                left,
                0,
                left + view.getMeasuredWidth(),
                view.getMeasuredHeight()
        );
    }

    protected void hideStickyView() {
        final View view = adaptView != null
                ? adaptView.view()
                : null;
        if (view != null) {
            view.setAlpha(0F);
        }
    }

    @Nullable
    private static AdaptRecyclerView.Adapter<?> adapter(@NonNull RecyclerView recyclerView) {
        final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        // takes care of null adapter also
        if (!(adapter instanceof AdaptRecyclerView.Adapter)) {
            return null;
        }
        return (AdaptRecyclerView.Adapter<?>) adapter;
    }
}
