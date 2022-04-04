package io.noties.adapt;

import static org.junit.Assert.fail;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import io.noties.adapt.Item.Key;
import io.noties.adapt.wrapper.ItemWrapper;

public class ItemKeysTest {

    @Before
    public void before() {
        ItemKeys.clearCache();
    }

    @Test
    public void create_item() {
        // create for a regular Item (not wrapped)

        final Root root = new Root();
        final Key key = Key.of(root);

        Assert.assertEquals(1, key.items().size());
        Assert.assertEquals(Collections.singletonList(Root.class), key.items());

        // must be greater than 0 (API does not give any guaranties how it generates viewTypes)
        Assert.assertNotEquals(root.viewType(), 0);
    }

    @Test
    public void create_wrapped_single() {

        final Root root = new Root();
        final Padding padding = new Padding(root);

        final Key key = Key.of(padding);
        Assert.assertEquals(2, key.items().size());
        Assert.assertEquals(Arrays.asList(Padding.class, Root.class), key.items());

        Assert.assertNotEquals(root.viewType(), padding.viewType());
        Assert.assertNotEquals(root.viewType(), key.viewType());

        // in this case padding viewType is equal to viewType of the whole Key
        Assert.assertEquals(padding.viewType(), key.viewType());
    }

    @Test
    public void create_wrapped_multiple() {

        final Root root = new Root();
        final Padding padding = new Padding(root);
        final Margin margin = new Margin(padding);

        final Key key = Key.of(margin);

        Assert.assertEquals(3, key.items().size());
        Assert.assertEquals(
                Arrays.asList(Margin.class, Padding.class, Root.class),
                key.items()
        );

        Assert.assertNotEquals(root.viewType(), padding.viewType());
        Assert.assertNotEquals(padding.viewType(), margin.viewType());

        Assert.assertNotEquals(root.viewType(), key.viewType());
        Assert.assertNotEquals(padding.viewType(), key.viewType());
        Assert.assertEquals(margin.viewType(), key.viewType());
    }

    @Test
    public void create_unmodifiable() {
        final Key key = Key.of(new Root());
        try {
            key.items().add(Root.class);
            fail();
        } catch (UnsupportedOperationException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void builder_unmodifiable() {
        final Key key = Key.builder(Root.class)
                .wrapped(Padding.class)
                .build();

        try {
            key.items().add(Margin.class);
            fail();
        } catch (UnsupportedOperationException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void builder_single() {
        final Key builder = Key.builder(Root.class).build();
        final Key single = Key.just(Root.class);
        Assert.assertEquals(builder, single);
    }

    @Test
    public void key_differentOrder_distinct() {
        // key contains the same items, but in different order must be considered
        //  a new key and have different hashCode and equals

        final Key pmr = Key.builder(Root.class)
                .wrapped(Padding.class)
                .wrapped(Margin.class)
                .build();

        final Key mpr = Key.builder(Root.class)
                .wrapped(Margin.class)
                .wrapped(Padding.class)
                .build();

        Assert.assertNotEquals(pmr, mpr);
        Assert.assertNotEquals(pmr.viewType(), mpr.viewType());
        Assert.assertNotEquals(pmr.hashCode(), mpr.hashCode());
    }

    private static class Root extends Item<Item.Holder> {

        Root() {
            super(NO_ID);
        }

        @NonNull
        @Override
        public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            throw new IllegalStateException();
        }

        @Override
        public void bind(@NonNull Holder holder) {
            throw new IllegalStateException();
        }
    }

    private static class Padding extends ItemWrapper {
        Padding(@NonNull Item<?> item) {
            super(item);
        }
    }

    private static class Margin extends ItemWrapper {
        Margin(@NonNull Item<?> item) {
            super(item);
        }
    }
}
