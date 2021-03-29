package io.noties.adapt;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import java.lang.ref.WeakReference;
import java.util.Map;

import io.noties.adapt.util.ExceptionUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class CachedHolderTest {

    private View view;
    private CachingHolder holder;
    private Map<Integer, WeakReference<View>> cache;

    @Before
    public void before() {
        view = mock(View.class);
        //noinspection unchecked
        cache = mock(Map.class);

        holder = new CachingHolder(view, cache);
    }

    @Test
    public void find_view_no_cache() {
        // cache.get
        // if null, or $.get == null, then super.findView (which triggers view.findViewById) + put in cache

        final int id = 13;
        final View target = mock(View.class);

        when(view.findViewById(eq(id)))
                .thenReturn(target);

        final View v = holder.findView(id);

        Assert.assertEquals(target, v);

        verify(cache, times(1)).get(eq(id));
        verify(view, times(1)).findViewById(eq(id));

        //noinspection unchecked
        final ArgumentCaptor<WeakReference<View>> captor = ArgumentCaptor.forClass(WeakReference.class);
        verify(cache, times(1)).put(eq(id), captor.capture());

        final WeakReference<View> reference = captor.getValue();
        Assert.assertNotNull(reference);
        Assert.assertNotNull(reference.get());
        Assert.assertEquals(target, reference.get());
    }

    @Test
    public void find_view_cached() {
        // returns cached instance

        final int id = 99;

        final View target = mock(View.class);
        final WeakReference<View> reference = new WeakReference<>(target);

        when(cache.get(eq(id))).thenReturn(reference);

        final View v = holder.findView(id);
        Assert.assertEquals(target, v);

        verify(cache, times(1)).get(eq(id));
        verify(view, never()).findViewById(anyInt());
        //noinspection unchecked
        verify(cache, never()).put(anyInt(), any(WeakReference.class));
    }

    @Test
    public void find_view_cached_dereferenced() {
        // value (weakReference) in cache, but null

        final int id = 123;
        final View target = mock(View.class);
        when(view.findViewById(eq(id))).thenReturn(target);
        final WeakReference<View> reference = new WeakReference<>(null);
        when(cache.get(eq(id))).thenReturn(reference);

        final View v = holder.findView(id);
        Assert.assertEquals(target, v);

        verify(cache, times(1)).get(eq(id));
        verify(view, times(1)).findViewById(eq(id));

        //noinspection unchecked
        final ArgumentCaptor<WeakReference<View>> captor = ArgumentCaptor.forClass(WeakReference.class);

        // times(2) because we prepopulate the cache with initial value
        verify(cache, times(1)).put(eq(id), captor.capture());

        // it will be different reference
        Assert.assertNotEquals(captor.getValue(), reference);
        Assert.assertEquals(target, captor.getValue().get());
    }

    @Test
    public void require_no_cache() {

        final int id = 529;

        try {
            holder.requireView(id);
            Assert.fail();
        } catch (AdaptException e) {
            ExceptionUtil.assertContains(e, "not found in specified layout");
        }

        verify(cache, times(1)).get(eq(id));

        // `never` because it should not cache if view.findViewById returns null
        //noinspection unchecked
        verify(cache, never()).put(anyInt(), any(WeakReference.class));
    }

    @Test
    public void require_cached() {
        // view will be queried and cached, not throw

        final int id = 987;
        final View target = mock(View.class);
        when(view.findViewById(eq(id))).thenReturn(target);

        final View v = holder.requireView(id);

        Assert.assertEquals(target, v);

        //noinspection unchecked
        final ArgumentCaptor<WeakReference<View>> captor = ArgumentCaptor.forClass(WeakReference.class);

        verify(cache, times(1)).get(eq(id));
        verify(view, times(1)).findViewById(eq(id));
        verify(cache, times(1)).put(eq(id), captor.capture());

        Assert.assertNotNull(captor.getValue().get());
        Assert.assertEquals(target, captor.getValue().get());
    }
}