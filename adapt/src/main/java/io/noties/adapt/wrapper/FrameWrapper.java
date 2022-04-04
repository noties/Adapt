package io.noties.adapt.wrapper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import io.noties.adapt.Item;

/**
 * @since $UNRELEASED;
 */
public class FrameWrapper extends ItemWrapper {

    public static int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    public static int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    public static int NO_GRAVITY = -1;

    @NonNull
    public static Wrapper init() {
        return init(MATCH_PARENT, WRAP_CONTENT);
    }

    @NonNull
    public static Wrapper init(int width, int height) {
        return init(width, height, NO_GRAVITY);
    }

    @NonNull
    public static Wrapper init(int width, int height, int contentGravity) {
        return item -> new FrameWrapper(item, width, height, contentGravity);
    }

    private final int width;
    private final int height;
    private final int contentGravity;

    public FrameWrapper(@NonNull Item<?> item, int width, int height, int contentGravity) {
        super(item);
        this.width = width;
        this.height = height;
        this.contentGravity = contentGravity;
    }

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        // can we create the same holder but with a different view?
        // width and height
        final FrameLayout frameLayout = new FrameLayout(inflater.getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(
                width,
                height
        ));
        final Item.Holder holder = super.createHolder(inflater, frameLayout);
        frameLayout.addView(holder.itemView());
        return new Holder(frameLayout, holder);
    }

    @Override
    public void bind(@NonNull Item.Holder holder) {
        final Holder h = (Holder) holder;
        super.bind(h.holder);

        // this is our frameLayout
        final FrameLayout frameLayout = (FrameLayout) h.itemView();
        final ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
        if (params.width != width
                || params.height != height) {
            params.width = width;
            params.height = height;
            frameLayout.setLayoutParams(params);
        }

        // this is a child of our frameLayout (original view)
        final View childView = frameLayout.getChildAt(0);
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childView.getLayoutParams();
        if (layoutParams != null && layoutParams.gravity != contentGravity) {
            layoutParams.gravity = contentGravity;
            childView.setLayoutParams(layoutParams);
        }
    }

    private static class Holder extends Item.Holder {

        private final Item.Holder holder;

        public Holder(@NonNull View view, @NonNull Item.Holder holder) {
            super(view);
            this.holder = holder;
        }
    }
}
