package io.noties.adapt.viewgroup;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.viewgroup.AdaptViewGroupDiff.Parent;

import static io.noties.adapt.util.ExceptionUtil.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AdaptViewGroupDiffTest {

    private AdaptViewGroupDiff.Impl impl;

    @Before
    public void before() {
        impl = new AdaptViewGroupDiff.Impl();
    }

    @Test
    public void both_empty() {
        // if both lists are empty, nothing should happen

        final Parent parent = mock(Parent.class);
        impl.diff(
                parent,
                Collections.<Item<?>>emptyList(),
                Collections.<Item<?>>emptyList());

        // failing condition
//        verify(parent, times(100)).removeAt(anyInt());

        verify(parent, never()).removeAt(anyInt());
        verify(parent, never()).move(anyInt(), anyInt());
        //noinspection unchecked
        verify(parent, never()).insertAt(anyInt(), any(Item.class));
        //noinspection unchecked
        verify(parent, never()).render(anyInt(), any(Item.class));
    }

    @Test
    public void all_removed() {

        // new list has no elements, all items must be removed

        final List<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        final int size = list.size();

        final Parent parent = mock(Parent.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                list.remove((int) invocation.getArgument(0));
                return null;
            }
        }).when(parent).removeAt(anyInt());

        final List<Item<?>> previous = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            previous.add(mock(Item.class));
        }

        impl.diff(
                parent,
                previous,
                Collections.<Item<?>>emptyList());

        // failing condition
//        verify(parent, times(100)).move(anyInt(), anyInt());

        // validate non-executed methods
        verify(parent, never()).move(anyInt(), anyInt());
        //noinspection unchecked
        verify(parent, never()).insertAt(anyInt(), any(Item.class));
        //noinspection unchecked
        verify(parent, never()).render(anyInt(), any(Item.class));

        verify(parent, times(size)).removeAt(anyInt());

        assertEquals(0, list.size());
    }

    @Test
    public void all_the_same() {
        // both lists contain same elements

        final List<Item<?>> previous = new ArrayList<>();
        previous.add(new MockItem(1) {
        });
        previous.add(new MockItem(2) {
        });
        previous.add(new MockItem(3) {
        });

        final List<Item<?>> current = new ArrayList<>(previous);

        final Parent parent = mock(Parent.class);

        impl.diff(parent, previous, current);

        // failing condition
//        verify(parent, times(100)).removeAt(anyInt());

        verify(parent, never()).removeAt(anyInt());
        verify(parent, never()).move(anyInt(), anyInt());
        //noinspection unchecked
        verify(parent, never()).insertAt(anyInt(), any(Item.class));

        // bind method must still be called for each item
        //noinspection unchecked
        verify(parent, times(previous.size())).render(anyInt(), any(Item.class));
    }

    @Test
    public void swap_items_2_elements() {
        // 2 elements change places, should be one operation of move

        final List<Item<?>> previous = new ArrayList<Item<?>>() {{
            add(new MockItem(1));
            add(new MockItem(2));
        }};

        final List<Item<?>> current = Arrays.asList(previous.get(1), previous.get(0));

        final Parent parent = mock(Parent.class);

        impl.diff(parent, previous, current);

        // failing condition
//        verify(parent, times(100)).removeAt(anyInt());

        verify(parent, never()).removeAt(anyInt());
        //noinspection unchecked
        verify(parent, never()).insertAt(anyInt(), any(Item.class));

        verify(parent, times(1)).move(eq(1), eq(0));

        // bind called for both
        //noinspection unchecked
        verify(parent, times(current.size())).render(anyInt(), any(Item.class));
    }

    @Test
    public void single_move_5_elements() {
        // both lists contain same items, but current list has last item as first -> single move op

        final int size = 5;
        final List<Item<?>> previous = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            previous.add(new MockItem(i + 1));
        }

        final List<Item<?>> current = new ArrayList<>(size);
        current.addAll(previous.subList(0, size - 1));
        current.add(0, previous.get(size - 1));

        // verify test input
        assertEquals(current.get(0), previous.get(size - 1));

        final Parent parent = mock(Parent.class);

        impl.diff(parent, previous, current);

        // failing condition
//        verify(parent, times(100)).removeAt(anyInt());

        verify(parent, never()).removeAt(anyInt());
        //noinspection unchecked
        verify(parent, never()).insertAt(anyInt(), any(Item.class));

        verify(parent, times(1)).move(eq(size - 1), eq(0));

        //noinspection unchecked
        verify(parent, times(size)).render(anyInt(), any(Item.class));
    }

    @Test
    public void three_items() {
        // old: [square, triangle, circle],
        // new: [triangle, square, circle]

        final List<Item<?>> previous = new ArrayList<Item<?>>() {{
            add(new MockItem(1) {
            });
            add(new MockItem(2) {
            });
            add(new MockItem(3) {
            });
        }};

        final List<Item<?>> current = new ArrayList<Item<?>>() {{
            add(previous.get(1));
            add(previous.get(0));
            add(previous.get(2));
        }};

        final Parent parent = mock(Parent.class);

        impl.diff(parent, previous, current);

        verify(parent, times(1)).move(eq(1), eq(0));
        verify(parent, times(1)).move(anyInt(), anyInt());

        verify(parent, never()).removeAt(anyInt());
        //noinspection unchecked
        verify(parent, never()).insertAt(anyInt(), any(Item.class));

//        verifyNoMoreInteractions(parent);
    }

    @Test
    public void same_id_different_types_same_position() {
        // items share the same id, but actually of different types -> old removed, new inserted

        final List<Item<?>> previous = Collections.<Item<?>>singletonList(new MockItem(1));

        // anonymous inner class will have different type
        final List<Item<?>> current = Collections.<Item<?>>singletonList(new MockItem(1) {
        });

        assertEquals(previous.get(0).id(), current.get(0).id());
        assertNotEquals(previous.get(0), current.get(0));

        final Parent parent = mock(Parent.class);

        impl.diff(parent, previous, current);

        // failing condition
//        verify(parent, times(100)).removeAt(anyInt());

        verify(parent, times(1)).removeAt(eq(0));
        verify(parent, times(1)).insertAt(eq(0), eq(current.get(0)));
        verify(parent, times(1)).render(eq(0), eq(current.get(0)));

        verify(parent, never()).move(anyInt(), anyInt());
    }

    @Test
    public void same_id_different_types_different_position() {
        // items share the same id, but actually of different types -> old removed, new inserted

        final List<Item<?>> previous = new ArrayList<Item<?>>() {{
            add(new MockItem(1));
            add(new MockItem(2));
        }};

        final List<Item<?>> current = new ArrayList<Item<?>>() {{
            add(new MockItem(2) {
            });
            add(new MockItem(1));
        }};

        // ids are the same, but actual equals is false
        assertEquals(previous.get(1).id(), current.get(0).id());
        assertNotEquals(previous.get(1), current.get(0));

        final Parent parent = mock(Parent.class);

        impl.diff(parent, previous, current);

        // failing condition
//        verify(parent, times(100)).removeAt(anyInt());

        verify(parent, times(1)).removeAt(eq(1));
        verify(parent, times(1)).insertAt(eq(0), eq(current.get(0)));

        verify(parent, times(1)).render(eq(0), eq(current.get(0)));
        verify(parent, times(1)).render(eq(1), eq(current.get(1)));

        verify(parent, never()).move(anyInt(), anyInt());
    }

    @Test
    public void duplicate_items_throws() {
        final Item<?> item = new MockItem(1L);
        final List<Item<?>> items = Arrays.asList(item, item);
        try {
            impl.diff(mock(Parent.class), Collections.<Item<?>>emptyList(), items);
        } catch (AdaptException e) {
            // A duplicate item is found at indices
            assertContains(e, "A duplicate item is found at indices");
        }
    }

    @Test
    public void no_id_initial() {
        // previous is empty, no ids are kept as-is (all added)
        final Item<?> first = new MockItem(Item.NO_ID);
        final Item<?> second = new MockItem(Item.NO_ID);
        final List<Item<?>> items = Arrays.asList(first, second);

        final Parent parent = mock(Parent.class);

        impl.diff(parent, Collections.<Item<?>>emptyList(), items);

        verify(parent, times(1)).insertAt(0, items.get(0));
        verify(parent, times(1)).insertAt(1, items.get(1));
    }

    @Test
    public void no_id() {
        final List<Item<?>> previous = new ArrayList<Item<?>>() {{
            add(new MockItem(Item.NO_ID));
            add(new MockItem(Item.NO_ID));
        }};
        final List<Item<?>> current = new ArrayList<Item<?>>() {{
            add(new MockItem(Item.NO_ID));
            add(new MockItem(Item.NO_ID));
        }};

        final Parent parent = mock(Parent.class);

        impl.diff(parent, previous, current);

        verify(parent, times(1)).removeAt(1);
        verify(parent, times(1)).removeAt(0);

        verify(parent, times(1)).insertAt(0, current.get(0));
        verify(parent, times(1)).insertAt(1, current.get(1));
    }

    @SuppressWarnings("rawtypes")
    private static class MockItem extends Item {

        MockItem(long id) {
            super(id);
        }

        @NonNull
        @Override
        public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            throw new RuntimeException();
        }

        @Override
        public void bind(@NonNull Holder holder) {
            throw new RuntimeException();
        }

        @Override
        @NonNull
        public String toString() {
            return "Mock(" + id() + ")";
        }
    }
}