package ru.noties.adapt.next;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.noties.adapt.next.AdaptViewGroupDiff.Parent;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        impl.diff(parent, Collections.<Item>emptyList(), Collections.<Item>emptyList());

        // failing condition
//        verify(parent, times(100)).removeAt(anyInt());

        verify(parent, never()).removeAt(anyInt());
        verify(parent, never()).move(anyInt(), anyInt());
        verify(parent, never()).insertAt(anyInt(), any(Item.class));
        verify(parent, never()).render(anyInt(), any(Item.class));
    }

    @Test
    public void all_removed() {

        // new list has no elements, all items must be removed

        final List<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        final int size = list.size();

        final Parent parent = mock(Parent.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                list.remove((int) invocation.getArgument(0));
                return null;
            }
        }).when(parent).removeAt(anyInt());

        final List<Item> previous = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            previous.add(mock(Item.class));
        }

        impl.diff(parent, previous, Collections.<Item>emptyList());

        // failing condition
//        verify(parent, times(100)).move(anyInt(), anyInt());

        // validate non-executed methods
        verify(parent, never()).move(anyInt(), anyInt());
        verify(parent, never()).insertAt(anyInt(), any(Item.class));
        verify(parent, never()).render(anyInt(), any(Item.class));

        verify(parent, times(size)).removeAt(anyInt());

        assertEquals(0, list.size());
    }

    @Test
    public void all_the_same() {
        // both lists contain same elements

        final List<Item> previous = new ArrayList<>();
        previous.add(new MockItem(1));
        previous.add(new MockItem(2));
        previous.add(new MockItem(3));

        final List<Item> current = new ArrayList<>(previous);

        final Parent parent = mock(Parent.class);

        impl.diff(parent, previous, current);

        // failing condition
//        verify(parent, times(100)).removeAt(anyInt());

        verify(parent, never()).removeAt(anyInt());
        verify(parent, never()).move(anyInt(), anyInt());
        verify(parent, never()).insertAt(anyInt(), any(Item.class));

        // render method must still be called for each item
        verify(parent, times(previous.size())).render(anyInt(), any(Item.class));
    }

    @Test
    public void swap_items_2_elements() {
        // 2 elements change places, should be one operation of move

        final List<Item> previous = Arrays.asList(
                (Item) new MockItem(1),
                new MockItem(2));

        final List<Item> current = Arrays.asList(previous.get(1), previous.get(0));

        final Parent parent = mock(Parent.class);

        impl.diff(parent, previous, current);

        // failing condition
//        verify(parent, times(100)).removeAt(anyInt());

        verify(parent, never()).removeAt(anyInt());
        verify(parent, never()).insertAt(anyInt(), any(Item.class));

        verify(parent, times(1)).move(eq(1), eq(0));

        // render called for both
        verify(parent, times(current.size())).render(anyInt(), any(Item.class));
    }

    @Test
    public void single_move_5_elements() {
        // both lists contain same items, but current list has last item as first -> single move op

        final int size = 5;
        final List<Item> previous = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            previous.add(new MockItem(i + 1));
        }

        final List<Item> current = new ArrayList<>(size);
        current.addAll(previous.subList(0, size - 1));
        current.add(0, previous.get(size - 1));

        // verify test input
        assertEquals(current.get(0), previous.get(size - 1));

        final Parent parent = mock(Parent.class);

        impl.diff(parent, previous, current);

        // failing condition
//        verify(parent, times(100)).removeAt(anyInt());

        verify(parent, never()).removeAt(anyInt());
        verify(parent, never()).insertAt(anyInt(), any(Item.class));

        verify(parent, times(1)).move(eq(size - 1), eq(0));

        verify(parent, times(size)).render(anyInt(), any(Item.class));
    }

    @Test
    public void same_id_different_types_same_position() {
        // items share the same id, but actually of different types -> old removed, new inserted

        final List<Item> previous = Collections.singletonList((Item) new MockItem(1));

        // anonymous inner class will have different type
        final List<Item> current = Collections.singletonList((Item) new MockItem(1) {
        });

        assertEquals(previous.get(0), current.get(0));

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

        final List<Item> previous = Arrays.asList((Item) new MockItem(1), new MockItem(2));

        final List<Item> current = Arrays.asList(
                (Item) new MockItem(2) {
                },
                new MockItem(1));

        assertEquals(previous.get(1), current.get(0));

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
        public void render(@NonNull Holder holder) {
            throw new RuntimeException();
        }
    }
}