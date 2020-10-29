package ru.noties.adapt.sample.java.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import ru.noties.adapt.Holder;
import ru.noties.adapt.ItemView;

public abstract class BaseItemView<T, H extends Holder> extends ItemView<T, H> {

    @LayoutRes
    protected abstract int layoutResId();

    @NonNull
    protected abstract H createHolder(@NonNull View view);

    @NonNull
    @Override
    public H createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return createHolder(inflater.inflate(layoutResId(), parent, false));
    }
}
