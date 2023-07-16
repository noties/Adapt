package io.noties.adapt.preview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.noties.adapt.Adapt;
import io.noties.adapt.Item;
import io.noties.adapt.util.ItemUtils;
import io.noties.adapt.viewgroup.AdaptViewGroup;

// @since $UNRELEASED;
public abstract class AdaptPreviewLayout extends FrameLayout {

    public AdaptPreviewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        initialize(this);

        final ViewGroup layout = createLayout(context);
        final Adapt adapt = createAdapt(layout);

        adapt.setItems(ItemUtils.assignIdsAccordingToIndex(items()));
    }

    @NonNull
    protected abstract List<Item<?>> items();

    protected void initialize(@NonNull AdaptPreviewLayout layout) {
    }

    /**
     * NB! Layout must be placed into root view (caller would not add it automatically).
     * This method should return ViewGroup that would be initialized with Adapt
     */
    @NonNull
    protected ViewGroup createLayout(@NonNull Context context) {

        final ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(linearLayout);
        addView(scrollView);

        return linearLayout;
    }

    @NonNull
    protected Adapt createAdapt(@NonNull ViewGroup viewGroup) {
        return AdaptViewGroup.init(viewGroup);
    }
}
