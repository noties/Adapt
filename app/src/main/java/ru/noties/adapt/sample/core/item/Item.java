package ru.noties.adapt.sample.core.item;

public abstract class Item {

    private final long id;

    public Item(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return id == item.id;
    }

    @Override
    public final int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
