package io.noties.adapt.wrapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import io.noties.adapt.Item;

@RunWith(RobolectricTestRunner.class)
public class OnBindWrapperTest {

    @Test
    public void test() {
        final OnBindWrapper.OnBind onBind = mock(OnBindWrapper.OnBind.class);
        final OnBindWrapper wrapper = new OnBindWrapper(
                mock(Item.class),
                onBind
        );
        final Item.Holder holder = mock(Item.Holder.class);
        wrapper.bind(holder);

        final ArgumentCaptor<Item.Holder> captor = ArgumentCaptor.forClass(Item.Holder.class);
        verify(onBind, times(1)).onBind(captor.capture());

        Assert.assertEquals(holder, captor.getValue());
    }
}