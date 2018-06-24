package ru.noties.adapt.sample.java.view;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import ru.noties.adapt.Holder;
import ru.noties.adapt.sample.R;
import ru.noties.adapt.sample.core.item.SectionItem;

public class SectionView extends BaseItemView<SectionItem, SectionView.SectionHolder> {

    @Override
    protected int layoutResId() {
        return R.layout.view_section;
    }

    @NonNull
    @Override
    protected SectionHolder createHolder(@NonNull View view) {
        return new SectionHolder(view);
    }

    @Override
    public void bindHolder(@NonNull SectionHolder holder, @NonNull SectionItem item) {
        holder.text.setText(item.name());
    }

    static class SectionHolder extends Holder {

        final TextView text = requireView(R.id.text);

        SectionHolder(@NonNull View view) {
            super(view);
        }
    }
}
