package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ItemLayoutWrapperTest {

    @Test
    public void no_layout_id() {
        // throws when createLayout is called

        //noinspection unchecked
        final ItemLayoutWrapper wrapper = new ItemLayoutWrapper(mock(Item.class));

        try {
            wrapper.createLayout(mock(LayoutInflater.class), mock(ViewGroup.class));
            fail();
        } catch (AdaptException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Override #createLayout method or specify layout XML"));
        }
    }

    @Test
    public void append_layout_adds_view() {

        //noinspection unchecked
        final ItemLayoutWrapper wrapper = new ItemLayoutWrapper(mock(Item.class));

        final ViewGroup group = mock(ViewGroup.class);
        final View view = mock(View.class);

        wrapper.appendWrappedViewToLayout(group, view);

        verify(group, times(1)).addView(eq(view));
    }

    @Test
    public void view_type_item() {
        // wrapper returns view-type of wrapped item

        final AbstractItem item = new AbstractItem(1L) {
            @Override
            public int viewType() {
                return 42;
            }
        };

        //noinspection unchecked
        final ItemLayoutWrapper wrapper = new ItemLayoutWrapper(item);
        assertEquals(42, wrapper.viewType());
    }

    @Test
    public void recycler_decoration_item() {

        final RecyclerView.ItemDecoration decoration = mock(RecyclerView.ItemDecoration.class);
        final Item item = mock(Item.class);
        when(item.recyclerDecoration(any(RecyclerView.class))).thenReturn(decoration);

        //noinspection unchecked
        final ItemLayoutWrapper wrapper = new ItemLayoutWrapper(item);

        assertEquals(decoration, wrapper.recyclerDecoration(mock(RecyclerView.class)));
    }

    @Test
    public void create_holder() {

        final LayoutInflater inflater = mock(LayoutInflater.class);
        when(inflater.inflate(anyInt(), nullable(ViewGroup.class), anyBoolean())).thenReturn(mock(ViewGroup.class));

        final Item.Holder holder = new Item.Holder(mock(View.class));
        final Item item = mock(Item.class);
        when(item.createHolder(any(LayoutInflater.class), nullable(ViewGroup.class)))
                .thenReturn(holder);

        //noinspection unchecked
        final ItemLayoutWrapper wrapper = new ItemLayoutWrapper(1, item);
        final ItemLayoutWrapper.Holder wrapperHolder =
                wrapper.createHolder(inflater, mock(ViewGroup.class));

        verify(item, times(1))
                .createHolder(any(LayoutInflater.class), any(ViewGroup.class));

        assertEquals(holder, wrapperHolder.wrapped);
        assertNotSame(wrapperHolder.itemView, holder.itemView);
    }
}