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

        final Adapt adapt = AdaptViewGroup.init(linearLayout);
        adapt.setItems(items());
    }

    @NonNull
    protected abstract List<Item<?>> items();

    protected void initialize(@NonNull AdaptPreviewLayout layout) {
    }
}
