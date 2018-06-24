package ru.noties.adapt;

import android.content.res.Resources;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DynamicHolderTest {

    @Test
    public void find_view_called_once() {

        final View view = mock(View.class);
        final DynamicHolder holder = new DynamicHolder(view);

        holder.findView(1);
        holder.findView(1);

        verify(view, times(1)).findViewById(anyInt());
    }

    @Test
    public void require_view_called_once() {

        final View view = mock(View.class);
        when(view.findViewById(anyInt())).thenReturn(mock(View.class));

        final DynamicHolder holder = new DynamicHolder(view);

        holder.requireView(1);
        holder.requireView(1);

        verify(view, times(1)).findViewById(anyInt());
    }

    @Test
    public void require_view_throws_if_null() {

        final View view = mock(View.class);
        final Resources resources = mock(Resources.class);
        when(resources.getResourceName(anyInt())).thenReturn("mocked-resource-name");
        when(view.getResources()).thenReturn(resources);

        final DynamicHolder holder = new DynamicHolder(view);

        try {
            holder.requireView(1);
        } catch (NullPointerException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("View with specified id is not found"));
        }
    }

    @Test
    public void require_view_throws_if_cached_null_by_find_view_call() {

        final View view = mock(View.class);
        final Resources resources = mock(Resources.class);
        when(resources.getResourceName(anyInt())).thenReturn("mocked-resource-name");
        when(view.getResources()).thenReturn(resources);

        final DynamicHolder holder = new DynamicHolder(view);
        assertNull(holder.findView(1));

        try {
            holder.requireView(1);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Requested view is not found in layout and"));
        }
    }
}