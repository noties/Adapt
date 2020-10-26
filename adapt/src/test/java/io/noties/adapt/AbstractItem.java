package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class AbstractItem extends Item<Item.Holder> {

    public AbstractItem(long id) {
        super(id);
    }

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return null;
    }

    @Override
    public void render(@NonNull Holder holder) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractItem that = (AbstractItem) o;

        return id() == that.id();
    }

    @Override
    public int hashCode() {
        return (int) (id() ^ (id() >>> 32));
    }
}
