package io.noties.adapt.recyclerview;

import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.noties.adapt.Item;
import io.noties.adapt.view.AdaptView;

/**
 * RecyclerView.ItemDecoration that <em>stick</em> an {@link Item} to the top of a
 * RecyclerView (aka sticky header or section). Internally uses a duplicated view in layout (data is bound
 * through {@link AdaptView}). Reverse layout is supported. Also as sticky view is in layout
 * all widget accessibility features are available. Can be used with GridLayoutManager if sticky view
 * fills all the spans of GridLayoutManager (matches width).
 * <p>
 * Sticky view will always have width equal to RecyclerView width (minus horizontal padding).
 * <p>
 * Please note that only a single item can eb sticky inside a recycler-view
 *
 * @since 2.2.0
 */
// TODO: do not measure with each draw call, use size changed listener (or layout) and persist a flag if view has changed
public class StickyItemDecoration extends RecyclerView.ItemDecoration {

//    /**
//     * Factory method to create {@link StickyItemDecoration}. Passed {@code item} can be a <em>mocked</em>
//     * one (can have invalid data) - it is just used to create layout only (no bind will be called on that item).
//     * Please note that {@code recyclerView} must be wrapped inside a FrameLayout in order to add sticky view.
//     * If you want to provide custom placing of {@link AdaptView} use {@link #create(AdaptView)} or {@link #create(ViewGroup, Item)}.
//     *
//     * @see #create(ViewGroup, Item)
//     * @see #create(AdaptView)
//     */
//    @NonNull
//    public static <I extends Item<? extends Item.Holder>> StickyItemDecoration create(
//            @NonNull RecyclerView recyclerView,
//            @NonNull I item) {
//        final FrameLayout frameLayout = parentFrameLayout(recyclerView);
//        return create(frameLayout, item);
//    }

    //    /**
//     * Unlike {@link #create(RecyclerView, Item)} this method does not require FrameLayout as
//     * parent of RecyclerView. Created {@link AdaptView} will be directly added to specified {@code parent}.
//     *
//     * @see #create(RecyclerView, Item)
//     * @see #create(AdaptView)
//     */
    @NonNull
    public static <I extends Item<? extends Item.Holder>> StickyItemDecoration create(
            @NonNull ViewGroup parent,
            @NonNull I item) {
        final AdaptView<I> adaptView = AdaptView.init(parent, item);
        return new StickyItemDecoration(adaptView);
    }

//    /**
//     * Unlike {@link #create(RecyclerView, Item)} this method does not require FrameLayout
//     * as parent of RecyclerView, but it expects that {@link AdaptView} is already correctly
//     * placed in layout.
//     *
//     * @see #create(RecyclerView, Item)
//     * @see #create(ViewGroup, Item)
//     */
//    @NonNull
//    public static <I extends Item> StickyItemDecoration create(@NonNull AdaptView<I> adaptView) {
//        return new StickyItemDecoration(adaptView);
//    }

//    // let's make it explicitly FrameLayout
//    @NonNull
//    private static FrameLayout parentFrameLayout(@NonNull RecyclerView recyclerView) {
//
//        final ViewParent parent = recyclerView.getParent();
//
//        // please note... that, for example, ScrollView is a sibling of FrameLayout
//        //      and we must filter those...
//
//        final boolean isFrame = parent instanceof FrameLayout;
//
//        // so, ScrollView, HorizontalScrollView (they kinda weird to see here, who would
//        // want to do so)? Also... there are NestedScrollViews...
//        final boolean isScroll = isFrame && (parent instanceof ScrollView
//                || parent instanceof HorizontalScrollView
//                || parent instanceof ScrollingView);
//
//        if (isFrame && !isScroll) {
//            return (FrameLayout) parent;
//        }
//
//        throw AdaptException.create("RecyclerView must be placed inside FrameLayout " +
//                "in order to add sticky view. Consider wrapping RecyclerView inside FrameLayout " +
//                "or use different #create factory method that allows manual placing of a sticky view.");
//    }

    private static final int MEASURE_SPEC_UNSPECIFIED =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

    @SuppressWarnings("rawtypes")
    private final AdaptView adaptView;
    private final int stickyViewType;

    @SuppressWarnings("rawtypes")
    protected StickyItemDecoration(@NonNull AdaptView adaptView) {
        this.adaptView = adaptView;
        // @since 2.3.0-SNAPSHOT it's important to use item viewType
        // (instead of asking for a generated one)
        this.stickyViewType = AdaptRecyclerView.assignedViewType(adaptView.item().getClass());
        hideStickyView();
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
                final RecyclerView.ViewHolder viewHolder = parent.findViewHolderForAdapterPosition(position);
                nextStickyViewTop = viewHolder != null
                        ? viewHolder.itemView.getTop()
                        : 0;
                break;
            }
            position += 1;
        }

        //noinspection unchecked
        adaptView.setItem(item);

        final View view = prepareStickyView(adaptView, parent);
        view.setAlpha(1F);

        final int height = view.getMeasuredHeight();
        final float y;
        if (nextStickyViewTop > 0 && nextStickyViewTop < height) {
            y = -(height - nextStickyViewTop);
            processStickyView(view, (float) nextStickyViewTop / height);
        } else {
            y = 0F;
            processStickyView(view, 1F);
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

        //noinspection unchecked
        adaptView.setItem(item);

        final View view = prepareStickyView(adaptView, parent);
        view.setAlpha(1F);

        final int height = view.getHeight();
        final int y;
        if (previousStickyViewTop > 0 && previousStickyViewTop < height) {
            y = -(height - previousStickyViewTop);
            processStickyView(view, (float) previousStickyViewTop / height);
        } else {
            y = 0;
            processStickyView(view, 1F);
        }
        view.setTranslationY(y);
    }

    @NonNull
    protected View prepareStickyView(@NonNull AdaptView<?> adaptView, @NonNull RecyclerView recyclerView) {

        final int left = recyclerView.getPaddingLeft();
        final int width = recyclerView.getWidth() - left - recyclerView.getPaddingRight();
        final View view = adaptView.view();

        // @since 2.3.0-SNAPSHOT we check if view has exact height and use it
        final int heightMeasureSpec;
        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null
                || layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            heightMeasureSpec = MEASURE_SPEC_UNSPECIFIED;
        } else if (layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            // interesting case
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.getHeight(), View.MeasureSpec.EXACTLY);
        } else {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                heightMeasureSpec
        );
        view.layout(left, 0, left + view.getMeasuredWidth(), view.getMeasuredHeight());
        return view;
    }

    // 1F - is fully sticky, 0F - completely not
    protected void processStickyView(@NonNull View view, float ratio) {

    }

    protected void hideStickyView() {
        adaptView.view().setAlpha(0F);
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
