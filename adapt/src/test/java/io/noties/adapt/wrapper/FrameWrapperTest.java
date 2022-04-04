package io.noties.adapt.wrapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.noties.adapt.Item;
import io.noties.adapt.Item.Holder;

@RunWith(RobolectricTestRunner.class)
public class FrameWrapperTest {

    @Test
    public void createHolder() {
        final Item<?> item = mock(Item.class);
        final Holder itemHolder = mock(Holder.class);

        when(item.createHolder(any(LayoutInflater.class), any(ViewGroup.class))).then($ -> itemHolder);
        when(itemHolder.itemView()).thenReturn(mock(View.class));

        final FrameWrapper wrapper = new FrameWrapper(
                item,
                1, 2, -777
        );

        final LayoutInflater inflater = mock(LayoutInflater.class);
        when(inflater.getContext()).thenReturn(mock(Context.class, RETURNS_MOCKS));

        final Holder holder = wrapper.createHolder(
                inflater,
                mock(ViewGroup.class)
        );

        final ViewGroup group = (ViewGroup) holder.itemView();
        Assert.assertTrue(group.getClass().getName(), group instanceof FrameLayout);

        final ViewGroup.LayoutParams params = group.getLayoutParams();
        Assert.assertEquals(1, params.width);
        Assert.assertEquals(2, params.height);

        Assert.assertEquals(1, group.getChildCount());
    }

    @Test
    public void bind() {
        final Item<?> item = mock(Item.class);
        final Holder itemHolder = mock(Holder.class);
        final View itemView = mock(View.class);
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(0, 0, 0);

        when(itemView.getLayoutParams()).thenReturn(layoutParams);
        when(itemHolder.itemView()).thenReturn(itemView);
        when(item.createHolder(any(LayoutInflater.class), any(ViewGroup.class))).then($ -> itemHolder);

        final FrameWrapper wrapper = new FrameWrapper(
                item,
                4, 5, 6
        );

        final Holder holder = wrapper.createHolder(
                mock(LayoutInflater.class, RETURNS_MOCKS),
                mock(ViewGroup.class)
        );

        wrapper.bind(holder);

        final ViewGroup group = (ViewGroup) holder.itemView();
        Assert.assertTrue(group.getClass().getName(), group instanceof FrameLayout);

        final FrameLayout frameLayout = (FrameLayout) group;
        Assert.assertEquals(1, frameLayout.getChildCount());
        Assert.assertEquals(itemView, frameLayout.getChildAt(0));

        final ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
        Assert.assertEquals(4, params.width);
        Assert.assertEquals(5, params.height);

        Assert.assertEquals(itemView, frameLayout.getChildAt(0));

        // 2 times because one time during creation by FrameLayout
        //  and 2nd after on bind
        verify(itemView, times(2))
                .setLayoutParams(any(FrameLayout.LayoutParams.class));

        Assert.assertEquals(6, layoutParams.gravity);
    }
}