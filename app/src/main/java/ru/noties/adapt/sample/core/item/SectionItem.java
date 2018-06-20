package ru.noties.adapt.sample.core.item;

import android.support.annotation.NonNull;

public class SectionItem extends Item {

    private final String name;

    public SectionItem(long id, @NonNull String name) {
        super(id);
        this.name = name;
    }

    @NonNull
    public String name() {
        return name;
    }
}
