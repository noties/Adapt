package io.noties.adapt.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.noties.adapt.Item;
import io.noties.adapt.ItemLayout;
import io.noties.adapt.util.AdaptDivider.DividerProvider;

public class AdaptDividerTest {

    @Test
    public void empty() {
        // if supplied iterable is empty, nothing happens -> no dividers added

        assertDividers(Collections.emptyList());
    }

    @Test
    public void single() {
        // if only a single item is present, no dividers are added

        final Item<?> item = mock(Item.class);

        assertDividers(Collections.singletonList(item));
    }

    @Test
    public void two() {
        // a single divider is inserted between two items

        final Item<?> f = mock(Item.class);
        final Item<?> s = mock(Item.class);

        assertDividers(Arrays.asList(f, s));
    }

    @Test
    public void many() {
        final int[] numbers = {
                10,
                100,
                99,
                1001,
                10000
        };

        for (int number : numbers) {
            final List<Item<?>> items = new ArrayList<>(number + 1);
            for (int i = 0; i < number; i++) items.add(mock(Item.class));
            assertDividers(items);
        }
    }

    // does the both - regular and with `divider`
    private void assertDividers(@NonNull List<Item<?>> items) {

        // NB! do not reuse provider between multiple calls

        {
            final DividerProvider provider = mock(DividerProvider.class);
            when(provider.provide(any(Item.class))).then($ -> new DividerItem());
            assertDividers(provider, items, AdaptDivider.divide(items, provider));
        }

        {
            final DividerProvider provider = mock(DividerProvider.class);
            when(provider.provide(any(Item.class))).then($ -> new DividerItem());
            assertDividers(provider, items, AdaptDivider.divider(provider).divide(items));
        }
    }

    private void assertDividers(
            @NonNull DividerProvider provider,
            @NonNull List<Item<?>> items,
            @NonNull List<Item<?>> divided
    ) {
        final int size = items.size();

        if (size == 0) {
            // if original collection is empty, nothing should happen
            Assert.assertEquals(divided.toString(), 0, divided.size());
            verify(provider, never()).provide(any(Item.class));
            return;
        }

        // 1 => 1 + (0) => 1 (so, for a list of one -> only one divider is added)
        Assert.assertEquals(size + (size - 1), divided.size());

        // never would be called for the first item
        verify(provider, never()).provide(eq(items.get(0)));

        // size of items minus 1 (divider is inserted between items)
        verify(provider, times(size - 1)).provide(any(Item.class));

        boolean isDivider = false;
        int index = 0;

        for (Item<?> item : divided) {
            if (isDivider) {
                Assert.assertTrue(item.getClass().getName(), item instanceof DividerItem);
            } else {
                Assert.assertEquals(items.get(index), item);
                index += 1;
            }

            isDivider = !isDivider;
        }
    }

    private static class DividerItem extends ItemLayout {
        DividerItem() {
            super(0, 0);
        }
    }
}