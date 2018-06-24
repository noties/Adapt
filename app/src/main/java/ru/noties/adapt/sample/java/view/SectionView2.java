package ru.noties.adapt.sample.java.view;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.noties.adapt.DynamicHolder;
import ru.noties.adapt.ItemView;
import ru.noties.adapt.sample.R;
import ru.noties.adapt.sample.core.item.SectionItem;

public class SectionView2 extends ItemView<SectionItem, DynamicHolder> {

    @NonNull
    @Override
    public DynamicHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new DynamicHolder(inflater.inflate(R.layout.view_section, parent, false));
    }

    @Override
    public void bindHolder(@NonNull DynamicHolder holder, @NonNull final SectionItem item) {
        holder
                .on(R.id.text, new DynamicHolder.Action<TextView>() {
                    @Override
                    public void apply(@NonNull TextView view) {
                        view.setText(item.name());
                    }
                })
                .on(R.id.text, new DynamicHolder.Action<View>() {
                    @Override
                    public void apply(@NonNull View view) {
                        // hey another one call chained
                    }
                });
    }
}
