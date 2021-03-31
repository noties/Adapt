package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ItemWrapperTest {

    @Test
    public void unwrap_notWrapped() {
        final Item<?> item = mock(Item.class);
        final Item<?> unwrapped = ItemWrapper.unwrap(item);
        Assert.assertEquals(item, unwrapped);
    }

    @Test
    public void isWrapped_false() {
        final Item<?> item = mock(Item.class);
        Assert.assertFalse(ItemWrapper.isWrapped(item));
    }

    @Test
    public void isWrapped_true() {
        final Wrap wrap = new Wrap(mock(Item.class));
        Assert.assertTrue(ItemWrapper.isWrapped(wrap));
    }

    @Test
    public void unwrap() {
        final Item<?> item = mock(Item.class);
        final Wrap wrap = new Wrap(item);
        final Item<?> unwrapped = ItemWrapper.unwrap(wrap);
        Assert.assertEquals(item, unwrapped);
    }

    @Test
    public void init_sameIdAsWrapped() {
        final long id = 42L;
        final Root root = new Root(id);
        final Wrap wrap = new Wrap(root);
        Assert.assertEquals(id, wrap.id());
    }

    @Test
    public void item() {
        // getter returns the same item supplied to init
        final Item<?> item = mock(Item.class);
        final Wrap wrap = new Wrap(item);
        Assert.assertEquals(item, wrap.item());
    }

    @Test
    public void createHolder_callsWrapped() {
        final Item<?> item = mock(Item.class);
        final Wrap wrap = new Wrap(item);
        wrap.createHolder(mock(LayoutInflater.class), mock(ViewGroup.class));
        verify(item, times(1)).createHolder(any(LayoutInflater.class), any(ViewGroup.class));
    }

    @Test
    public void bind_callsWrapped() {
        //noinspection unchecked
        final Item<Item.Holder> item = mock(Item.class);
        final Wrap wrap = new Wrap(item);
        wrap.bind(mock(Item.Holder.class));
        verify(item, times(1)).bind(any(Item.Holder.class));
    }

    private static class Wrap extends ItemWrapper {
        Wrap(@NonNull Item<?> item) {
            super(item);
        }
    }

    private static class Root extends Item<Item.Holder> {

        Root(long id) {
            super(id);
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
}