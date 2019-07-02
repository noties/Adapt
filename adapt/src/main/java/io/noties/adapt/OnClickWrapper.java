package io.noties.adapt;

import android.view.View;

import androidx.annotation.NonNull;

public class OnClickWrapper<H extends Item.Holder> extends ItemWrapper<H> {

    public interface OnItemClickListener<H extends Holder> {
        void onItemClick(@NonNull Item<H> item, @NonNull H holder);
    }

    private final OnItemClickListener<H> listener;

    public OnClickWrapper(@NonNull Item<H> item, @NonNull OnItemClickListener<H> listener) {
        super(item);
        this.listener = listener;
    }

    @Override
    public void render(@NonNull final H holder) {
        super.render(holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item(), holder);
            }
        });
    }
}
