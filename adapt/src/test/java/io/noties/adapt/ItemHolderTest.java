package io.noties.adapt;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.view.View;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.noties.adapt.util.ExceptionUtil;

@RunWith(RobolectricTestRunner.class)
public class ItemHolderTest {

    @Test
    public void adapt_not_initialized() {
        final Item.Holder holder = new Item.Holder(mock(View.class));
        try {
            holder.adapt();
            fail();
        } catch (AdaptException e) {
            ExceptionUtil.assertContains(e, "Adapt is not yet initialized");
        }
    }

    @Test
    public void adapt_initialized() {
        final Item.Holder holder = new Item.Holder(mock(View.class));
        final Adapt adapt = mock(Adapt.class);
        holder.setAdapt(adapt);
        Assert.assertEquals(adapt, holder.adapt());
    }

    @Test
    public void itemView() {
        final View view = mock(View.class);
        final Item.Holder holder = new Item.Holder(view);
        Assert.assertEquals(view, holder.itemView());
    }

    @Test
    public void findView() {
        final View view = mock(View.class, RETURNS_MOCKS);
        final Item.Holder holder = new Item.Holder(view);
        final View mocked = holder.findView(12);
        verify(view, times(1)).findViewById(eq(12));
    }

    @Test
    public void requireView() {
        final View view = mock(View.class, RETURNS_MOCKS);
        final Item.Holder holder = new Item.Holder(view);
        final View mocked = holder.requireView(42);
        verify(view, times(1)).findViewById(eq(42));
    }
}
