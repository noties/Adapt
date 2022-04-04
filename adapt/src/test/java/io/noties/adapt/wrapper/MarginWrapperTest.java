package io.noties.adapt.wrapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.noties.adapt.Item;
import io.noties.adapt.Item.Holder;
import io.noties.adapt.util.Edges;

@RunWith(RobolectricTestRunner.class)
public class MarginWrapperTest {

    @Test
    public void notMarginLayoutParams() {
        // nothing happens, parent does not support margins

        final MarginWrapper wrapper = new MarginWrapper(
                mock(Item.class),
                new Edges(99)
        );
        final View view = mock(View.class);
        final Holder holder = mock(Holder.class);
        when(holder.itemView()).thenReturn(view);
        when(view.getLayoutParams()).thenReturn(null);

        wrapper.bind(holder);

        verify(view, never()).setLayoutParams(any(ViewGroup.LayoutParams.class));
    }

    @Test
    public void test() {
        final MarginWrapper wrapper = new MarginWrapper(
                mock(Item.class),
                new Edges(3, 4, 5, 6)
        );

        final View view = mock(View.class);
        final Holder holder = mock(Holder.class);
        final MarginLayoutParams layoutParams = mock(MarginLayoutParams.class);
        when(view.getLayoutParams()).thenReturn(layoutParams);
        when(holder.itemView()).thenReturn(view);

        wrapper.bind(holder);

        verify(layoutParams, times(1)).setMarginStart(3);
        verify(layoutParams, times(1)).setMarginEnd(5);

        Assert.assertEquals(4, layoutParams.topMargin);
        Assert.assertEquals(6, layoutParams.bottomMargin);

        verify(view, times(1)).setLayoutParams(eq(layoutParams));
    }
}