package io.noties.adapt.wrapper;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import io.noties.adapt.Item;

@RunWith(RobolectricTestRunner.class)
public class BackgroundWrapperTest {

    @Test
    public void clear() {
        final BackgroundWrapper wrapper = new BackgroundWrapper(
                mock(Item.class),
                null
        );
        final View view = mock(View.class);
        final Item.Holder holder = mock(Item.Holder.class);
        when(holder.itemView()).thenReturn(view);

        wrapper.bind(holder);

        verify(view, times(1))
                .setBackground(eq(null));
    }

    @Test
    public void init() {
        final Drawable drawable = mock(Drawable.class);
        final BackgroundWrapper wrapper = new BackgroundWrapper(
                mock(Item.class),
                drawable
        );
        final View view = mock(View.class);
        final Item.Holder holder = mock(Item.Holder.class);
        when(holder.itemView()).thenReturn(view);

        wrapper.bind(holder);

        verify(view, times(1))
                .setBackground(eq(drawable));
    }

    @Test
    public void initColor() {
        final int color = -1;

        final BackgroundWrapper wrapper = (BackgroundWrapper) BackgroundWrapper.init(color)
                .build(mock(Item.class));
        final View view = mock(View.class);
        final Item.Holder holder = mock(Item.Holder.class);
        when(holder.itemView()).thenReturn(view);

        wrapper.bind(holder);

        final ArgumentCaptor<Drawable> captor = ArgumentCaptor.forClass(Drawable.class);

        verify(view, times(1)).setBackground(captor.capture());

        final Drawable drawable = captor.getValue();
        Assert.assertTrue(drawable.getClass().getName(), drawable instanceof ColorDrawable);

        final ColorDrawable colorDrawable = (ColorDrawable) drawable;

        Assert.assertEquals(Integer.toHexString(colorDrawable.getColor()), color, colorDrawable.getColor());
    }
}