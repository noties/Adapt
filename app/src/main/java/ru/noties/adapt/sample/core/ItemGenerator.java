package ru.noties.adapt.sample.core;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.noties.adapt.sample.core.item.AppendItem;
import ru.noties.adapt.sample.core.item.Item;
import ru.noties.adapt.sample.core.item.SectionItem;
import ru.noties.adapt.sample.core.item.ShapeItem;

public abstract class ItemGenerator {

    @NonNull
    public static ItemGenerator create() {
        return new Impl();
    }

    public abstract List<Item> generate(@NonNull List<? extends Item> existing);

    static class Impl extends ItemGenerator {

        private final IdGenerator idGenerator = IdGenerator.create();
        private final ShapeRandom shapeRandom = ShapeRandom.create();
        private final ColorRandom colorRandom = ColorRandom.create();

        @Override
        public List<Item> generate(@NonNull List<? extends Item> existing) {

            final int count = existing.size();
            final List<Item> items = new ArrayList<>(count + 11);
            if (count > 0) {
                items.addAll(existing.subList(0, count - 1));
            }

            items.add(new SectionItem(idGenerator.next(), new Date().toString()));

            for (int i = 0; i < 9; i++) {
                items.add(new ShapeItem(
                        idGenerator.next(),
                        shapeRandom.next(),
                        colorRandom.next()
                ));
            }

            items.add(new AppendItem(Long.MAX_VALUE));

            return items;
        }
    }
}
