package io.noties.adapt.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.noties.adapt.Item;
import io.noties.adapt.ItemView;

public class ItemUtilsTest {

    private static class MyItem extends ItemView {

        MyItem() {
            // always the same
            super(0L);
        }

        @NonNull
        @Override
        public View createView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            throw new IllegalStateException("Must not be called");
        }
    }

    @Test
    public void test() {
        final List<Item<?>> items = new ArrayList<Item<?>>() {{
            add(new MyItem());
            add(new MyItem());
            add(new MyItem());
            add(new MyItem());
        }};

        for (Item<?> item : items) {
            Assert.assertEquals(0L, item.id());
        }

        final List<Item<?>> list = ItemUtils.assignIdsAccordingToIndex(items);
        for (int i = 0, size = list.size(); i < size; i++) {
            final Item<?> item = list.get(i);
            Assert.assertEquals(i, item.id());
        }
    }
}
