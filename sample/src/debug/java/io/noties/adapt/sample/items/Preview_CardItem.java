package io.noties.adapt.sample.items;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import io.noties.adapt.Adapt;
import io.noties.adapt.ItemList;
import io.noties.adapt.viewgroup.AdaptViewGroup;

public class Preview_CardItem extends LinearLayout {

    public Preview_CardItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);

        final ItemList list = new ItemList();
        list.add(new CardItem("A", Color.RED, "The very first one"));
        list.add(new CardItem("B", Color.GREEN, "And then again"));
        list.add(new CardItem("C", Color.BLUE, "So, what?"));

        final Adapt adapt = AdaptViewGroup.init(this);
        adapt.setItems(list);
    }
}
