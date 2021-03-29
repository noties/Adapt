package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public abstract class ItemView extends Item<CachingHolder> {

    protected ItemView(long id) {
        super(id);
    }

    @NonNull
    public abstract View createView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    @NonNull
    @Override
    public CachingHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new CachingHolder(createView(inflater, parent));
    }
}
