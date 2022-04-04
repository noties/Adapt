package io.noties.adapt.wrapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import io.noties.adapt.Item;

@RunWith(RobolectricTestRunner.class)
public class OnClickWrapperTest {

    @Test
    public void test() {
        final OnClickWrapper.Callbacks callbacks = mock(OnClickWrapper.Callbacks.class);
        final OnClickWrapper wrapper = new OnClickWrapper(
                mock(Item.class),
                callbacks
        );

        final View view = mock(View.class);
        final Item.Holder holder = mock(Item.Holder.class);
        final ArgumentCaptor<View.OnClickListener> captor =
                ArgumentCaptor.forClass(View.OnClickListener.class);

        when(holder.itemView()).thenReturn(view);

        wrapper.bind(holder);

        verify(view, times(1)).setOnClickListener(captor.capture());
        verify(callbacks, never()).onClick(any(Item.class));

        final View.OnClickListener onClickListener = captor.getValue();
        Assert.assertNotNull(onClickListener);

        onClickListener.onClick(view);

        verify(callbacks, times(1)).onClick(eq(wrapper));
    }
}