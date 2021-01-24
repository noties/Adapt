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
 * NB! RecyclerView must a <strong>direct child of a {@code FrameLayout}</strong>
 * with preferably equal dimensions
 * <p>
 * NB! Sticky view will always have width equal to RecyclerView width (minus horizontal padding).
 * <p>
 * NB! a single item only can be sticky inside a RecyclerView.
 * <p>
 * NB! RecyclerView should not have {@code padding} set - it can be applied
 * to direct parent {@code FrameLayout}. But {@code clipToPadding=false} and {@code clipChildren=false}
 * attributes would be ignored.
 *
 * @since 2.2.0
 */
public class StickyItemDecoration extends RecyclerView.ItemDecoration {

    // direct parent, can check here
    //  also, can be not equal, so maybe padding or margin on parent
    // allow specifying the container? no, bad
    @NonNull
    public static <I extends Item<? extends Item.Holder>> StickyItemDecoration create(
            @NonNull ViewGroup parent,
            @NonNull I item) {
        final AdaptView<I> adaptView = AdaptView.init(parent, item);
        return new StickyItemDecoration(adaptView);
    }

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

        //noinspection unchecked
        adaptView.setItem(item);

        final View view = prepareStickyView(adaptView, parent);
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

        //noinspection unchecked
        adaptView.setItem(item);

        final View view = prepareStickyView(adaptView, parent);
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
    protected View prepareStickyView(@NonNull AdaptView<?> adaptView, @NonNull RecyclerView recyclerView) {

        final View view = adaptView.view();

        // we rely on item to properly set itself, so measure/layout would not be required
        //  if view has not changed
        if (!view.isLayoutRequested()) {
            return view;
        }

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

        return view;
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
