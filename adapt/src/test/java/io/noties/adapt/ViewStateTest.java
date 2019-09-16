package io.noties.adapt;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.noties.adapt.ViewState.Cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ViewStateTest {

    private static final int ID_CACHE = R.id.adapt_internal_view_state_cache;
    private static final int ID_LISTENER = R.id.adapt_internal_view_state_attach_listener;

    @Test
    public void cache_created() {
        // when called on a view without cache associated -> cache will be created and stored

        final View parent = mock(View.class);

        // get is not creating a cache
        assertNull(Cache.get(parent));

        final Cache cache = Cache.of(parent);
        assertNotNull(cache);

        verify(parent, times(1)).setTag(eq(ID_CACHE), eq(cache));
    }

    @Test
    public void save_no_parent() {
        // when #save is called on a view without parent -> nothing will happen (no state will be saved)
        // we will validate that `saveHierarchyState` is not called

        final View view = mock(View.class);

        ViewState.save(1, view);

        // getParent called
        verify(view, times(1)).getParent();

        //noinspection unchecked
        verify(view, never()).saveHierarchyState(any(SparseArray.class));
    }

    @Test
    public void save_with_parent() {

        final ViewGroup parent = mock(ViewGroup.class);
        final View view = mock(View.class);

        when(view.getParent()).thenReturn(parent);

        ViewState.save(1L, view);

        final ArgumentCaptor<Cache> captor = ArgumentCaptor.forClass(Cache.class);

        verify(view, times(1)).getParent();
        verify(parent, times(1)).setTag(eq(ID_CACHE), captor.capture());

        //noinspection unchecked
        verify(view, times(1)).saveHierarchyState(any(SparseArray.class));

        final Cache cache = captor.getValue();
        assertEquals(1, cache.cache.size());
        assertNotNull(cache.cache.get(1L));
    }

    @Test
    public void cache_clear() {

        final View parent = mock(View.class);
        final Cache cache = Cache.of(parent);

        cache.save(1, mock(View.class));
        cache.save(2, mock(View.class));

        assertEquals(2, cache.cache.size());

        cache.clear();

        assertEquals(0, cache.cache.size());
    }

    @Test
    public void cache_clear_id() {

        final View parent = mock(View.class);
        final Cache cache = Cache.of(parent);

        cache.save(3, mock(View.class));
        cache.save(4, mock(View.class));

        assertEquals(2, cache.cache.size());

        cache.clear(3L);
        assertEquals(1, cache.cache.size());
        assertNull(cache.cache.get(3L));

        cache.clear(4L);
        assertEquals(0, cache.cache.size());
    }

    @Test
    public void cache_parent_detached() {
        // when parent is detached cache is cleared and removed from parent tag

        final View parent = mock(View.class);
        final ArgumentCaptor<View.OnAttachStateChangeListener> captor =
                ArgumentCaptor.forClass(View.OnAttachStateChangeListener.class);

        final Cache cache = Cache.of(parent);
        verify(parent, times(1)).setTag(eq(ID_CACHE), eq(cache));
        verify(parent, times(1)).addOnAttachStateChangeListener(captor.capture());

        cache.save(5L, mock(View.class));
        assertEquals(1, cache.cache.size());

        final View.OnAttachStateChangeListener listener = captor.getValue();
        assertNotNull(listener);

        listener.onViewDetachedFromWindow(parent);

        // cleared
        assertEquals(0, cache.cache.size());

        // listener unregistered
        verify(parent, times(1)).removeOnAttachStateChangeListener(eq(listener));

        // tag is set to NULL
        verify(parent, times(1)).setTag(eq(ID_CACHE), eq(null));
    }

    @Test
    public void cache_get() {
        // get is not creating cache if it's not initialized, but instead returns null

        final View parent = mock(View.class);
        final Cache cache = Cache.get(parent);
        assertNull(cache);

        verify(parent, times(1)).getTag(eq(ID_CACHE));

        verify(parent, never()).setTag(eq(ID_CACHE), any());
        verify(parent, never()).addOnAttachStateChangeListener(any(View.OnAttachStateChangeListener.class));
    }

    @Test
    public void process_parent_present() {
        // when process is called with a view with parent, state restoration will happen right away

        final ViewGroup parent = mock(ViewGroup.class);
        final View view = mock(View.class);
        final Cache cache = mock(Cache.class);

        when(view.getParent()).thenReturn(parent);
        when(parent.getTag(eq(ID_CACHE))).thenReturn(cache);

        ViewState.process(7L, view);

        // validate that cache is obtained
        verify(parent, times(1)).getTag(eq(ID_CACHE));
        verify(cache, times(1)).restore(eq(7L), eq(view));
    }

    @Test
    public void process() {

        final View view = mock(View.class);
        final ArgumentCaptor<View.OnAttachStateChangeListener> captor =
                ArgumentCaptor.forClass(View.OnAttachStateChangeListener.class);

        ViewState.process(8L, view);
        verify(view, times(1)).getParent();

        verify(view, times(1))
                .addOnAttachStateChangeListener(captor.capture());

        final View.OnAttachStateChangeListener listener = captor.getValue();
        assertNotNull(listener);

        final ViewGroup parent = mock(ViewGroup.class);
        final Cache cache = mock(Cache.class);
        when(parent.getTag(eq(ID_CACHE))).thenReturn(cache);

        when(view.getParent()).thenReturn(parent);

        // state will be restored on attach
        listener.onViewAttachedToWindow(view);

        verify(view, times(2)).getParent();
        verify(parent, times(1)).getTag(eq(ID_CACHE));
        verify(cache, times(1)).restore(eq(8L), eq(view));

        // state will be saved on detach (plus listener is removed)
        listener.onViewDetachedFromWindow(view);
        verify(view, times(3)).getParent();
        verify(parent, times(2)).getTag(eq(ID_CACHE));
        verify(cache, times(1)).save(eq(8L), eq(view));

        verify(view, times(1)).removeOnAttachStateChangeListener(eq(listener));
    }

    @Test
    public void process_attach_state_listener() {
        // only a single attach state listener must be registered

        final View view = mock(View.class);

        final List<View.OnAttachStateChangeListener> added = new ArrayList<>();
        final List<View.OnAttachStateChangeListener> removed = new ArrayList<>();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                added.add((View.OnAttachStateChangeListener) invocation.getArgument(0));
                return null;
            }
        }).when(view).addOnAttachStateChangeListener(any(View.OnAttachStateChangeListener.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                final View.OnAttachStateChangeListener listener = invocation.getArgument(0);
                removed.add(listener);
                return null;
            }
        }).when(view).removeOnAttachStateChangeListener(any(View.OnAttachStateChangeListener.class));

        final Tagged<Object> tagged = new Tagged<>();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                tagged.value = invocation.getArgument(1);
                return null;
            }
        }).when(view).setTag(eq(ID_LISTENER), any());

        when(view.getTag(eq(ID_LISTENER))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                return tagged.value;
            }
        });

        ViewState.process(9L, view);
        ViewState.process(9L, view);
        ViewState.process(9L, view);
        ViewState.process(9L, view);

        assertEquals(4, added.size());
        assertEquals(3, removed.size());

        for (View.OnAttachStateChangeListener listener : added) {
            verify(view, times(1)).addOnAttachStateChangeListener(eq(listener));
        }

        for (View.OnAttachStateChangeListener listener : removed) {
            verify(view, times(1)).removeOnAttachStateChangeListener(eq(listener));
        }

        verify(view, times(4))
                .setTag(eq(ID_LISTENER), any(View.OnAttachStateChangeListener.class));

        assertNotNull(tagged.value);
        assertTrue(tagged.value instanceof View.OnAttachStateChangeListener);

        final View.OnAttachStateChangeListener listener = (View.OnAttachStateChangeListener) tagged.value;
        listener.onViewDetachedFromWindow(view);

        // now, listener is removed
        // tag is set to null

        assertEquals(4, removed.size());
        assertNull(tagged.value);
    }

    private static class Tagged<T> {
        T value;
    }

    @Test
    public void restore_no_parent() {
        // nothing will be done if there is no parent

        final View view = mock(View.class);

        ViewState.restore(10L, view);

        verify(view, times(1))
                .getParent();

        //noinspection unchecked
        verify(view, never()).restoreHierarchyState(any(SparseArray.class));
    }

    @Test
    public void restore() {

        final ViewGroup parent = mock(ViewGroup.class);
        final View view = mock(View.class);
        final Cache cache = mock(Cache.class);

        when(view.getParent()).thenReturn(parent);
        when(parent.getTag(eq(ID_CACHE))).thenReturn(cache);

        ViewState.restore(11L, view);

        verify(view, times(1)).getParent();
        verify(parent, times(1)).getTag(eq(ID_CACHE));
        verify(cache, times(1)).restore(eq(11L), eq(view));
    }

    @Test
    public void clear() {
        final View parent = mock(View.class);
        final Cache cache = mock(Cache.class);
        when(parent.getTag(ID_CACHE)).thenReturn(cache);
        ViewState.clear(parent);
        verify(cache, times(1)).clear();
    }

    @Test
    public void clear_with_id() {
        final View parent = mock(View.class);
        final Cache cache = mock(Cache.class);
        when(parent.getTag(ID_CACHE)).thenReturn(cache);
        ViewState.clear(parent, 12L);
        verify(cache, times(1)).clear(eq(12L));
    }

    @Test
    public void on_save_instance_state_no_cache() {
        // no cache associated with container -> nothing is saved (null is returned)
        // no cache is created at this point

        final View parent = mock(View.class);
        final Bundle bundle = ViewState.onSaveInstanceState(parent);
        assertNull(bundle);

        verify(parent, never()).setTag(eq(ID_CACHE), any());
    }

    @Test
    public void on_restore_instance_state_null() {
        // no bundle -> nothing is restored
        // cache is automatically created

        final View parent = mock(View.class);
        final boolean result = ViewState.onRestoreInstanceState(parent, null);
        assertFalse(result);

        verify(parent, times(1)).setTag(eq(ID_CACHE), any(Cache.class));
    }

    @Test
    public void on_save_instance_state() {

        final View parent = mock(View.class);
        final Cache cache = new Cache(); // cannot mock, fields are not initialized

        //noinspection unchecked
        cache.cache.put(13L, mock(SparseArray.class));
        //noinspection unchecked
        cache.cache.put(14L, mock(SparseArray.class));
        //noinspection unchecked
        cache.cache.put(15L, mock(SparseArray.class));

        when(parent.getTag(eq(ID_CACHE))).thenReturn(cache);

        final Bundle bundle = ViewState.onSaveInstanceState(parent);
        assertNotNull(bundle);

        assertEquals(3, bundle.size());
        assertNotNull(bundle.getSparseParcelableArray("13"));
        assertNotNull(bundle.getSparseParcelableArray("14"));
        assertNotNull(bundle.getSparseParcelableArray("15"));
    }

    @Test
    public void on_restore_instance_state() {

        @SuppressWarnings("unchecked") final Map<Long, SparseArray<Parcelable>> state = new HashMap<Long, SparseArray<Parcelable>>() {{
            put(16L, mock(SparseArray.class));
            put(17L, mock(SparseArray.class));
            put(18L, mock(SparseArray.class));
            put(19L, mock(SparseArray.class));
        }};

        final Bundle bundle = new Bundle();
        for (Map.Entry<Long, SparseArray<Parcelable>> entry : state.entrySet()) {
            bundle.putSparseParcelableArray(Long.toString(entry.getKey()), entry.getValue());
        }

        final View parent = mock(View.class);
        final ArgumentCaptor<Cache> captor = ArgumentCaptor.forClass(Cache.class);

        final boolean result = ViewState.onRestoreInstanceState(parent, bundle);
        assertTrue(result);

        verify(parent, times(1)).setTag(eq(ID_CACHE), captor.capture());
        final Cache cache = captor.getValue();
        assertNotNull(cache);

        final Map<Long, SparseArray<Parcelable>> cached = cache.cache;
        assertEquals(state.size(), cached.size());

        for (Map.Entry<Long, SparseArray<Parcelable>> entry : state.entrySet()) {
            assertEquals(entry.getValue(), cached.get(entry.getKey()));
        }
    }

    @Test
    public void on_restore_instance_state_invalid_keys() {
        // invalid keys are ignored (must be longs)

        final View parent = mock(View.class);
        final Cache cache = new Cache();
        when(parent.getTag(eq(ID_CACHE))).thenReturn(cache);

        final Bundle bundle = new Bundle();
        bundle.putByte("hello!", (byte) 0);
        bundle.putLong("!@#$%^&*()", 42L);
        //noinspection unchecked
        bundle.putSparseParcelableArray("array", mock(SparseArray.class));

        final boolean result = ViewState.onRestoreInstanceState(parent, bundle);
        // result is still true
        assertTrue(result);

        assertEquals(0, cache.cache.size());
    }
}