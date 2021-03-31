package io.noties.adapt.sample.items;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.noties.adapt.Adapt;
import io.noties.adapt.Item;
import io.noties.adapt.sample.ItemGenerator;
import io.noties.adapt.viewgroup.AdaptViewGroup;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class Preview_CardItem extends LinearLayout {

    public Preview_CardItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);

        final List<Item<?>> list = new ArrayList<>();
        final ItemGenerator generator = ItemGenerator.INSTANCE;

        list.add(new CardItem("A", Color.RED, "Item A"));
        list.add(new CardItem("-B", Color.GREEN, "Item -B"));
        list.add(new CardItem("😜", Color.BLUE, "Long long long long long long long long long long long long long long long"));

        list.add(new CardBigItem("BC", Color.MAGENTA, "Hey hey"));

        list.add(new PlainItem("Z", Color.DKGRAY, "Zzzzzzz"));

        list.add(new SectionItem(new Date().toString()));
        list.add(new ControlItem(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        }, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        }));

        list.add(new PageIndicatorItem("Whatever", false, new Function1<PageIndicatorItem, Unit>() {
            @Override
            public Unit invoke(PageIndicatorItem pageIndicatorItem) {
                return null;
            }
        }));

        list.add(new CardBigItem("YO", Color.BLACK, "Yep"));

        final Adapt adapt = AdaptViewGroup.init(this);
        adapt.setItems(list);
    }
}
