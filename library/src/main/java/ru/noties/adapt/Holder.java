package ru.noties.adapt;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class Holder extends RecyclerView.ViewHolder {

    public Holder(@NonNull View view) {
        super(view);
    }

    @SuppressWarnings("unused")
    public <V extends View> V findView(@IdRes int id) {
        //noinspection unchecked
        return (V) itemView.findViewById(id);
    }
}
