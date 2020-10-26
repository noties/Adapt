package io.noties.adapt.viewgroup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.noties.adapt.AbstractItem;
import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AdaptViewGroupTest {

    private static final int ID_ITEM = R.id.adapt_internal_item;
    private static final int ID_HOLDER = R.id.adapt_internal_holder;

    private AdaptViewGroup group;

    private ViewGroup viewGroup;
    private AdaptViewGroupDiff diff;
    private AdaptViewGroup.ChangeHandler changeHandler;

    @Before
    public void before() {
        viewGroup = mock(ViewGroup.class);
        diff = mock(AdaptViewGroupDiff.class);
        changeHandler = mock(AdaptViewGroup.ChangeHandler.class);
        group = AdaptViewGroup.init(viewGroup, new AdaptViewGroup.Configurator() {
            @Override
            public void configure(@NonNull AdaptViewGroup.Configuration configuration) {
                configuration
                        .adaptViewGroupDiff(diff)
                        .layoutInflater(mock(LayoutInflater.class))
                        .changeHandler(changeHandler);
            }
        });
    }

    @Test
    public void init_removes_all_children_from_group() {
        // any count > 0 should do
        final ViewGroup viewGroup = mock(ViewGroup.class);
        when(viewGroup.getChildCount()).thenReturn(1);
        AdaptViewGroup.init(viewGroup, new AdaptViewGroup.Configurator() {
            @Override
            public void configure(@NonNull AdaptViewGroup.Configuration configuration) {
                configuration.layoutInflater(mock(LayoutInflater.class));
            }
        });
        verify(viewGroup, times(1)).getChildCount();
        verify(viewGroup, times(1)).removeAllViews();
    }

    @Test
    public void empty_current_items() {
        assertEquals(0, group.items().size());
    }

    @Test
    public void get_current_items_throws_if_not_associated() {
        // if there are views that were added manually (not through setItems), then
        // getCurrentItems will thrown an exception

        when(viewGroup.getChildCount()).thenReturn(3);

        when(viewGroup.getChildAt(eq(0))).thenAnswer(new Answer<View>() {
            @Override
            public View answer(InvocationOnMock invocation) {
                final View view = mock(View.class);
                when(view.getTag(eq(ID_ITEM))).thenReturn(mock(Item.class));
                return view;
            }
        });
        when(viewGroup.getChildAt(eq(1))).thenReturn(mock(View.class));
        when(viewGroup.getChildAt(eq(2))).thenAnswer(new Answer<View>() {
            @Override
            public View answer(InvocationOnMock invocation) {
                final View view = mock(View.class);
                when(view.getTag(eq(ID_ITEM))).thenReturn(mock(Item.class));
                return view;
            }
        });

        try {
            group.items();
            fail();
        } catch (AdaptException e) {
            assertContains(e, "View at position(1) doesn't have Item associated");
        }
    }

    @Test
    public void notify_item_changed_throws_if_not_associated() {

        when(viewGroup.getChildCount()).thenReturn(3);

        when(viewGroup.getChildAt(eq(0))).thenAnswer(new Answer<View>() {
            @Override
            public View answer(InvocationOnMock invocation) {
                final View view = mock(View.class);
                when(view.getTag(eq(ID_ITEM))).thenReturn(mock(Item.class));
                return view;
            }
        });
        when(viewGroup.getChildAt(eq(1))).thenReturn(mock(View.class));
        when(viewGroup.getChildAt(eq(2))).thenAnswer(new Answer<View>() {
            @Override
            public View answer(InvocationOnMock invocation) {
                final View view = mock(View.class);
                when(view.getTag(eq(ID_ITEM))).thenReturn(mock(Item.class));
                return view;
            }
        });

        try {
            group.notifyItemChanged(new AbstractItem(1L));
            fail();
        } catch (AdaptException e) {
            assertContains(e, "View at position(1) doesn't have Item associated");
        }
    }

    @Test
    public void notify_item_changed_not_found() {

        final View view = mock(View.class);
        when(view.getTag(eq(ID_ITEM))).thenReturn(new AbstractItem(1L));

        when(viewGroup.getChildCount()).thenReturn(1);
        when(viewGroup.getChildAt(eq(0))).thenReturn(view);

        try {
            group.notifyItemChanged(mock(Item.class));
            fail();
        } catch (AdaptException e) {
            assertContains(e, "Item is not associated with this AdaptViewGroup");
        }
    }

    @Test
    public void get_current_items() {

        final List<Item<?>> items = new ArrayList<Item<?>>() {{
            add(new AbstractItem(1));
            add(new AbstractItem(3));
            add(new AbstractItem(2));
        }};

        when(viewGroup.getChildCount()).thenReturn(items.size());

        for (int i = 0; i < items.size(); i++) {
            final View view = mock(View.class);
            when(view.getTag(eq(R.id.adapt_internal_item))).thenReturn(items.get(i));
            when(viewGroup.getChildAt(eq(i))).thenReturn(view);
        }

        assertEquals(items, group.items());
    }

    @Test
    public void empty_items_removes_all_views() {
        // for both inputs (null or empty list) all views will be removed

        group.setItems(Collections.<Item<?>>emptyList());

        verify(changeHandler, times(1)).removeAll(eq(viewGroup));

        // diff must not be called
        verify(diff, never()).diff(
                any(AdaptViewGroupDiff.Parent.class),
                ArgumentMatchers.<Item<?>>anyList(),
                ArgumentMatchers.<Item<?>>anyList());
    }

    @Test
    public void null_items_removes_all_views() {
        // for both inputs (null or empty list) all views will be removed

        group.setItems(null);

        verify(changeHandler, times(1)).removeAll(eq(viewGroup));

        // diff must not be called
        verify(diff, never()).diff(
                any(AdaptViewGroupDiff.Parent.class),
                ArgumentMatchers.<Item<?>>anyList(),
                ArgumentMatchers.<Item<?>>anyList());
    }

    @Test
    public void not_empty_items_calls_diff() {

        final List<Item<?>> items = new ArrayList<Item<?>>() {{
            add(mock(Item.class));
            add(mock(Item.class));
        }};
        group.setItems(items);

        verify(diff, times(1)).diff(
                any(AdaptViewGroupDiff.Parent.class),
                ArgumentMatchers.<Item<?>>anyList(),
                ArgumentMatchers.<Item<?>>anyList());
    }

    @Test
    public void parent_insert_at_view_already_attached() {

        final Item<?> item = mock(Item.class);
        final View view = mock(View.class);
        when(view.getParent()).thenReturn(mock(ViewParent.class));
        final Item.Holder holder = new Item.Holder(view);
        when(item.createHolder(any(LayoutInflater.class), any(ViewGroup.class))).thenReturn(holder);

        try {
            ((AdaptViewGroupDiff.Parent) group).insertAt(0, item);
            fail();
        } catch (AdaptException e) {
            assertContains(e, "Returned view already has parent");
        }
    }

    @Test
    public void parent_callbacks_remove_at() {

        final AdaptViewGroupDiff.Parent parent = (AdaptViewGroupDiff.Parent) group;

        parent.removeAt(0);

        verify(changeHandler, times(1)).removeAt(eq(viewGroup), eq(0));
    }

    @Test
    public void find_item_for_view() {
        final View view = mock(View.class);
        final Item<?> item = mock(Item.class);
        when(view.getTag(eq(ID_ITEM))).thenReturn(item);

        assertEquals(item, group.findItemFor(view));
    }

    @Test
    public void parent_callbacks_move() {

        final AdaptViewGroupDiff.Parent parent = (AdaptViewGroupDiff.Parent) group;

        final View view = mock(View.class);
        when(viewGroup.getChildAt(eq(1))).thenReturn(view);

        parent.move(1, 7);

        verify(changeHandler, times(1)).move(eq(viewGroup), eq(1), eq(7));
    }

    @Test
    public void parent_callbacks_insert_at() {

        final AdaptViewGroupDiff.Parent parent = group;

        final View view = mock(View.class);
        final Item<?> item = mock(Item.class);
        final Item.Holder holder = new Item.Holder(view);

        when(item.createHolder(any(LayoutInflater.class), any(ViewGroup.class))).thenReturn(holder);

        parent.insertAt(666, item);

        verify(item, times(1))
                .createHolder(any(LayoutInflater.class), eq(viewGroup));

        verify(view, times(1))
                .setTag(eq(ID_HOLDER), eq(holder));

        verify(changeHandler, times(1)).insertAt(eq(viewGroup), eq(view), eq(666));
    }

    @Test
    public void parent_callbacks_render_no_holder() {
        // will throw if view at specified index has no holder

        final AdaptViewGroupDiff.Parent parent = (AdaptViewGroupDiff.Parent) group;

        when(viewGroup.getChildAt(anyInt())).thenReturn(mock(View.class));

        try {
            //noinspection unchecked
            parent.render(0, mock(Item.class));
            fail();
        } catch (AdaptException e) {
            assertContains(e, "Internal error, attached view has no Holder saved");
        }
    }

    @Test
    public void parent_callbacks_render() {

        final AdaptViewGroupDiff.Parent parent = (AdaptViewGroupDiff.Parent) group;

        final View view = mock(View.class);
        //noinspection unchecked
        final Item<Item.Holder> item = mock(Item.class);
        final Item.Holder holder = new Item.Holder(view);

        when(view.getTag(eq(R.id.adapt_internal_holder))).thenReturn(holder);
        when(viewGroup.getChildAt(eq(777))).thenReturn(view);

        parent.render(777, item);

        verify(item, times(1)).render(eq(holder));
        verify(view, times(1)).setTag(eq(ID_ITEM), eq(item));
    }

    private static void assertContains(@NonNull Throwable t, @NonNull String message) {
        final String actual;
        {
            final String m = t.getMessage();
            if (m == null) {
                actual = "";
            } else {
                actual = m;
            }
        }
        assertTrue(actual, actual.contains(message));
    }
}