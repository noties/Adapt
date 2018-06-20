package ru.noties.adapt.sample.java.view;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.noties.adapt.Holder;
import ru.noties.adapt.ItemView;
import ru.noties.adapt.sample.R;
import ru.noties.adapt.sample.core.item.SectionItem;

public class SectionView extends ItemView<SectionItem, SectionView.SectionHolder> {

    @NonNull
    @Override
    public SectionHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new SectionHolder(inflater.inflate(R.layout.view_section, parent, false));
    }

    @Override
    public void bindHolder(@NonNull SectionHolder holder, @NonNull SectionItem item) {
        holder.text.setText(item.name());
    }

    static class SectionHolder extends Holder {

        final TextView text;

        SectionHolder(@NonNull View view) {
            super(view);
            this.text = findView(R.id.text);
        }
    }
}
