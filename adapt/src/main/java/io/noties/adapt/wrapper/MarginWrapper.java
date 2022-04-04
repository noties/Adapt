package io.noties.adapt.wrapper;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.noties.adapt.Item;
import io.noties.adapt.util.Edges;

/**
 * @since $UNRELEASED;
 */
public class MarginWrapper extends ItemWrapper {

    @NonNull
    public static WrapperBuilder all(int margin) {
        return init(Edges.all(margin));
    }

    @NonNull
    public static WrapperBuilder init(@NonNull Edges edges) {
        return item -> new MarginWrapper(item, edges);
    }

    private final Edges edges;

    public MarginWrapper(@NonNull Item<?> item, @NonNull Edges edges) {
        super(item);
        this.edges = edges;
    }

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);

        final View view = holder.itemView();
        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            final Edges edges = this.edges;
            final ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.topMargin = edges.top;
            marginLayoutParams.bottomMargin = edges.bottom;
            marginLayoutParams.setMarginStart(edges.leading);
            marginLayoutParams.setMarginEnd(edges.trailing);
            view.setLayoutParams(marginLayoutParams);
        }
        // else do nothing (if layout params are not of MarginLayoutParams instance)
    }
}
