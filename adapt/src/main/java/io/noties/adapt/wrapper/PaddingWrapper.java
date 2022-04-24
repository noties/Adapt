package io.noties.adapt.wrapper;

import android.view.View;

import androidx.annotation.NonNull;

import io.noties.adapt.Item;
import io.noties.adapt.util.Edges;

/**
 * @since 4.0.0
 */
public class PaddingWrapper extends ItemWrapper {

    @NonNull
    public static WrapperBuilder all(int padding) {
        return init(Edges.all(padding));
    }

    @NonNull
    public static WrapperBuilder init(@NonNull Edges edges) {
        return item -> new PaddingWrapper(item, edges);
    }

    @NonNull
    private final Edges edges;

    public PaddingWrapper(@NonNull Item<?> item, @NonNull Edges edges) {
        super(item);
        this.edges = edges;
    }

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);

        final Edges edges = this.edges;
        final View view = holder.itemView();
        view.setPaddingRelative(
                edges.leading,
                edges.top,
                edges.trailing,
                edges.bottom
        );
    }
}
