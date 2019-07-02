package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AdaptViewGroupTest {

    private ViewGroup viewGroup;
    private AdaptViewGroupDiff diff;
    private AdaptViewGroup group;

    @Before
    public void before() {
        viewGroup = mock(ViewGroup.class);
        diff = mock(AdaptViewGroupDiff.class);
        group = AdaptViewGroup.builder(viewGroup)
                .adaptViewGroupDiff(diff)
                .layoutInflater(mock(LayoutInflater.class))
                .build();
    }

    @Test
    public void empty_current_items() {
        assertEquals(0, group.getCurrentItems().size());
    }

    @Test
    public void get_current_items_side_effects() {
        // if there are views that have no holder/item attached to a view, this view is removed

        final AtomicInteger count = new AtomicInteger(2);

        // return an answer (we track count internally)
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                return count.get();
            }
        }).when(viewGroup).getChildCount();

        // return mocked view
        when(viewGroup.getChildAt(anyInt())).thenReturn(mock(View.class));

        // decrement our count variable
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                count.decrementAndGet();
                return null;
            }
        }).when(viewGroup).removeViewAt(anyInt());

        assertEquals(0, group.getCurrentItems().size());

        verify(viewGroup, times(2)).removeViewAt(anyInt());
    }

    @Test
    public void get_current_items() {

        final List<Item> items = Arrays.asList(
                (Item) new AbstractItem(1),
                new AbstractItem(2),
                new AbstractItem(3));

        when(viewGroup.getChildCount()).thenReturn(items.size());

        for (int i = 0; i < items.size(); i++) {
            final View view = mock(View.class);
            when(view.getTag(eq(R.id.adapt_internal_item))).thenReturn(items.get(i));
            when(viewGroup.getChildAt(eq(i))).thenReturn(view);
        }

        assertEquals(items, group.getCurrentItems());
    }

    @Test
    public void empty_items_removes_all_views() {
        // for both inputs (null or empty list) all views will be removed

        group.setItems(Collections.<Item>emptyList());

        verify(viewGroup, times(1)).removeAllViews();

        // diff must not be called
        verify(diff, never()).diff(
                any(AdaptViewGroupDiff.Parent.class),
                ArgumentMatchers.<Item>anyList(),
                ArgumentMatchers.<Item>anyList());
    }

    @Test
    public void null_items_removes_all_views() {
        // for both inputs (null or empty list) all views will be removed

        group.setItems(null);

        verify(viewGroup, times(1)).removeAllViews();

        // diff must not be called
        verify(diff, never()).diff(
                any(AdaptViewGroupDiff.Parent.class),
                ArgumentMatchers.<Item>anyList(),
                ArgumentMatchers.<Item>anyList());
    }

    @Test
    public void not_empty_items_calls_diff() {

        group.setItems(Arrays.asList(mock(Item.class), mock(Item.class)));

        verify(diff, times(1)).diff(
                any(AdaptViewGroupDiff.Parent.class),
                ArgumentMatchers.<Item>anyList(),
                ArgumentMatchers.<Item>anyList());
    }

    @Test
    public void parent_callbacks_remove_at() {

        final AdaptViewGroupDiff.Parent parent = (AdaptViewGroupDiff.Parent) group;

        parent.removeAt(0);

        verify(viewGroup, times(1)).removeViewAt(eq(0));
    }

    @Test
    public void parent_callbacks_move() {

        final AdaptViewGroupDiff.Parent parent = (AdaptViewGroupDiff.Parent) group;

        final View view = mock(View.class);
        when(viewGroup.getChildAt(eq(1))).thenReturn(view);

        parent.move(1, 7);

        verify(viewGroup, times(1)).getChildAt(eq(1));
        verify(viewGroup, times(1)).removeViewAt(eq(1));
        verify(viewGroup, times(1)).addView(eq(view), eq(7));
    }

    @Test
    public void parent_callbacks_insert_at() {

        final AdaptViewGroupDiff.Parent parent = (AdaptViewGroupDiff.Parent) group;

        final View view = mock(View.class);
        final Item item = mock(Item.class);
        final Item.Holder holder = new Item.Holder(view);

        when(item.createHolder(any(LayoutInflater.class), any(ViewGroup.class))).thenReturn(holder);

        parent.insertAt(666, item);

        verify(item, times(1))
                .createHolder(any(LayoutInflater.class), eq(viewGroup));

        verify(view, times(1))
                .setTag(eq(R.id.adapt_internal_holder), eq(holder));

        verify(viewGroup, times(1)).addView(eq(view), eq(666));
    }

    @Test
    public void parent_callbacks_render_no_holder() {
        // will throw if view at specified index has no holder

        final AdaptViewGroupDiff.Parent parent = (AdaptViewGroupDiff.Parent) group;

        when(viewGroup.getChildAt(anyInt())).thenReturn(mock(View.class));

        try {
            parent.render(0, mock(Item.class));
            fail();
        } catch (AdaptException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Internal error, attached view has no Holder saved"));
        }
    }

    @Test
    public void parent_callbacks_render() {

        final AdaptViewGroupDiff.Parent parent = (AdaptViewGroupDiff.Parent) group;

        final View view = mock(View.class);
        final Item item = mock(Item.class);
        final Item.Holder holder = new Item.Holder(view);

        when(view.getTag(eq(R.id.adapt_internal_holder))).thenReturn(holder);
        when(viewGroup.getChildAt(eq(777))).thenReturn(view);

        parent.render(777, item);

        //noinspection unchecked
        verify(item, times(1)).render(eq(holder));
        verify(view, times(1)).setTag(eq(R.id.adapt_internal_item), eq(item));
    }

    private static class AbstractItem extends Item {

        AbstractItem(long id) {
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

            return id == that.id;
        }

        @Override
        public int hashCode() {
            return (int) (id ^ (id >>> 32));
        }
    }
}