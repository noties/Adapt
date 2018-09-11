package ru.noties.adapt;

import android.content.res.Resources;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class HolderTest {

    @Test
    public void find_view_allows_null() {
        final View view = mock(View.class);
        final Holder holder = new Holder(view);
        assertNull(holder.findView(1));
        assertNull(holder.findView(2));
    }

    @Test
    public void find_view_returns() {
        final View view = mock(View.class);
        when(view.findViewById(anyInt())).thenReturn(mock(View.class));
        final Holder holder = new Holder(view);
        assertNotNull(holder.findView(1));
        assertNotNull(holder.findView(2));
    }

    @Test
    public void require_view_null_throws_npe() {

        final View view = mock(View.class);
        final Holder holder = new Holder(view);

        final Resources resources = mock(Resources.class);
        when(resources.getResourceName(anyInt())).thenReturn("mocked-resource-name");
        when(view.getResources()).thenReturn(resources);

        TestUtils.assertThrows(
                "Holder: View with specified id is not found: R.id.mocked-resource-name",
                new Runnable() {
                    @Override
                    public void run() {
                        holder.requireView(1);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        holder.requireView(2);
                    }
                }
        );
    }

    @Test
    public void require_view_returns() {

        final View view = mock(View.class);

        when(view.findViewById(anyInt())).thenReturn(mock(View.class));

        final Holder holder = new Holder(view);

        assertNotNull(holder.requireView(1));
        assertNotNull(holder.requireView(2));
    }
}