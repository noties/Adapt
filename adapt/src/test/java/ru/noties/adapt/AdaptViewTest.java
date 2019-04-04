package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AdaptViewTest {

    private View view;
    private AdaptView adaptView;

    @Before
    public void before() {
        view = mock(View.class);
        adaptView = new AdaptView.Impl(view);
    }

    @Test
    public void bind_no_previous_item() {
        // when bind is called, but item has not been rendered -> exception

        // ensure IN condition
        assertNull(view.getTag(R.id.adapt_internal_item));

        try {
            adaptView.setItem(mock(Item.class));
            fail();
        } catch (AdaptException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Unexpected state, there is no item bound"));
        }
    }

    @Test
    public void bind_no_previous_holder() {
        // when bind is called, but item has not been rendered -> exception

        when(view.getTag(eq(R.id.adapt_internal_item))).thenReturn(mock(Item.class));

        assertNull(view.getTag(R.id.adapt_internal_holder));

        try {
            adaptView.setItem(mock(Item.class));
            fail();
        } catch (AdaptException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Unexpected state, there is no Holder associated"));
        }
    }

    @Test
    public void bind_different_item_types() {
        // when bind is called with an item of different type (class) than previous

        when(view.getTag(eq(R.id.adapt_internal_item))).thenReturn(mock(Item.class));
        when(view.getTag(eq(R.id.adapt_internal_holder))).thenReturn(mock(Item.Holder.class));

        try {
            adaptView.setItem(new Item(0) {
                @NonNull
                @Override
                public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
                    return null;
                }

                @Override
                public void render(@NonNull Holder holder) {

                }
            });
            fail();
        } catch (AdaptException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Supplied item has different view-type as previously bound one"));
        }
    }

    @Test
    public void bind() {
        // normal flow (no exceptions)

        final Item.Holder holder = mock(Item.Holder.class);
        when(view.getTag(eq(R.id.adapt_internal_item))).thenReturn(mock(Item.class));
        when(view.getTag(eq(R.id.adapt_internal_holder))).thenReturn(holder);

        final Item item = mock(Item.class);

        adaptView.setItem(item);

        verify(item, times(1)).render(eq(holder));
        verify(view, times(1)).setTag(eq(R.id.adapt_internal_item), eq(item));
    }

    @Test
    public void factory_append() {
        // factory append method will add created view and store all required information

        final Item item = mock(Item.class);
        final View view = mock(View.class);
        final Item.Holder holder = new Item.Holder(view);
        final ViewGroup group = mock(ViewGroup.class);

        when(item.createHolder(any(LayoutInflater.class), any(ViewGroup.class))).thenReturn(holder);

        assertNotNull(AdaptView.append(mock(LayoutInflater.class), group, item));

        verify(item, times(1)).createHolder(any(LayoutInflater.class), eq(group));
        verify(view, times(1)).setTag(eq(R.id.adapt_internal_holder), eq(holder));
        verify(group, times(1)).addView(eq(view));
        verify(item, times(1)).render(eq(holder));
        verify(view, times(1)).setTag(eq(R.id.adapt_internal_item), eq(item));
    }

    @Test
    public void factory_create() {

        final View view = mock(View.class);
        final Item item = mock(Item.class);
        final Item.Holder holder = new Item.Holder(view);
        final AdaptView.HolderProvider holderProvider = mock(AdaptView.HolderProvider.class);

        when(holderProvider.provide(eq(view))).thenReturn(holder);

        assertNotNull(AdaptView.create(view, item, holderProvider));

        verify(holderProvider, times(1)).provide(eq(view));
        verify(view, times(1)).setTag(eq(R.id.adapt_internal_holder), eq(holder));
        verify(item, times(1)).render(eq(holder));
        verify(view, times(1)).setTag(eq(R.id.adapt_internal_item), eq(item));
    }

    @Test
    public void factory_create_2() {

        final View parent = mock(View.class);
        final View view = mock(View.class);
        final Item item = mock(Item.class);
        final Item.Holder holder = new Item.Holder(view);
        final AdaptView.HolderProvider holderProvider = mock(AdaptView.HolderProvider.class);

        when(parent.findViewById(eq(13))).thenReturn(view);
        when(holderProvider.provide(eq(view))).thenReturn(holder);

        assertNotNull(AdaptView.create(parent, 13, item, holderProvider));

        verify(holderProvider, times(1)).provide(eq(view));
        verify(view, times(1)).setTag(eq(R.id.adapt_internal_holder), eq(holder));
        verify(item, times(1)).render(eq(holder));
        verify(view, times(1)).setTag(eq(R.id.adapt_internal_item), eq(item));
    }
}