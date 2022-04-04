package io.noties.adapt.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.noties.adapt.Item;

@RunWith(RobolectricTestRunner.class)
public class ItemWrapperTest {

    @Test
    public void unwrap_notWrapped() {
        final Item<?> item = mock(Item.class);
        final Item<?> unwrapped = ItemWrapper.unwrap(item);
        assertEquals(item, unwrapped);
    }

    @Test
    public void isWrapped_false() {
        final Item<?> item = mock(Item.class);
        assertFalse(ItemWrapper.isWrapped(item));
    }

    @Test
    public void isWrapped_true() {
        final Wrap wrap = new Wrap(mock(Item.class));
        assertTrue(ItemWrapper.isWrapped(wrap));
    }

    @Test
    public void unwrap() {
        final Item<?> item = mock(Item.class);
        final Wrap wrap = new Wrap(item);
        final Item<?> unwrapped = ItemWrapper.unwrap(wrap);
        assertEquals(item, unwrapped);
    }

    @Test
    public void unwrap_multiple() {
        final Item<?> item = mock(Item.class);
        final Item<?> wrapped = item
                .wrap(Wrap.create())
                .wrap(Wrap.create());
        assertEquals(item, ItemWrapper.unwrap(wrapped));
    }

    @Test
    public void init_sameIdAsWrapped() {
        final long id = 42L;
        final Root root = new Root(id);
        final Wrap wrap = new Wrap(root);
        assertEquals(id, wrap.id());
    }

    @Test
    public void item() {
        // getter returns the same item supplied to init
        final Item<?> item = mock(Item.class);
        final Wrap wrap = new Wrap(item);
        assertEquals(item, wrap.item());
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

    @Test
    public void findWrapper_notWrapped() {
        final Item<?> item = mock(Item.class);
        Assert.assertNull(ItemWrapper.findWrapper(item, Wrap.class));
    }

    @Test
    public void findWrapper_notFound() {
        // wrapped, but not in required wrapper type
        final Item<?> item = mock(Item.class);
        final Item<?> wrapped = item.wrap(Wrap.create());
        final Yrap yrap = ItemWrapper.findWrapper(wrapped, Yrap.class);
        Assert.assertNull(yrap);
    }

    @Test
    public void findWrapper() {
        final Item<?> item = mock(Item.class);
        final Wrap wrap = new Wrap(item);
        assertEquals(wrap, ItemWrapper.findWrapper(wrap, Wrap.class));
    }

    @Test
    public void findWrapper_multiple() {
        final Item<?> item = mock(Item.class);
        final Wrap wrap = new Wrap(item);
        final Yrap yrap = new Yrap(wrap);

        //noinspection UnnecessaryLocalVariable
        final Item<?> wrapped = yrap;
        assertEquals(yrap, ItemWrapper.findWrapper(wrapped, Yrap.class));
        assertEquals(wrap, ItemWrapper.findWrapper(wrapped, Wrap.class));
    }

    @Test
    public void findWrapper_subclass() {
        final Item<?> item = mock(Item.class);
        final Zrap zrap = new Zrap(item);

        final Yrap yrap = ItemWrapper.findWrapper(zrap, Yrap.class);
        final Yrap yrapZ = ItemWrapper.findWrapper(zrap, Zrap.class);

        assertEquals(zrap, yrap);
        assertEquals(zrap, yrapZ);
    }

    private static class Wrap extends ItemWrapper {

        @NonNull
        public static WrapperBuilder create() {
            return Wrap::new;
        }

        Wrap(@NonNull Item<?> item) {
            super(item);
        }
    }

    private static class Yrap extends ItemWrapper {
        Yrap(@NonNull Item<?> item) {
            super(item);
        }
    }

    private static class Zrap extends Yrap {
        Zrap(@NonNull Item<?> item) {
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