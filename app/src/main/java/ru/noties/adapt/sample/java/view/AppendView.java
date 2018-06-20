package ru.noties.adapt.sample.java.view;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ru.noties.adapt.Holder;
import ru.noties.adapt.ItemView;
import ru.noties.adapt.sample.R;
import ru.noties.adapt.sample.core.item.AppendItem;

public class AppendView extends ItemView<AppendItem, Holder> {

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(inflater.inflate(R.layout.view_append, parent, false));
    }

    @Override
    public void bindHolder(@NonNull Holder holder, @NonNull AppendItem item) {
        // no op
    }
}
