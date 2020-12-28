package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

public abstract class ItemLayout extends ItemView {

    private final int layoutResId;

    protected ItemLayout(long id, @LayoutRes int layoutResId) {
        super(id);
        this.layoutResId = layoutResId;
    }

    @NonNull
    @Override
    public View createView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return inflater.inflate(layoutResId, parent, false);
    }
}
