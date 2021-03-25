package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public abstract class ItemView extends Item<CachedHolder> {

    protected ItemView(long id) {
        super(id);
    }

    @NonNull
    public abstract View createView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    @NonNull
    @Override
    public CachedHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new CachedHolder(createView(inflater, parent));
    }
}
