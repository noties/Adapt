package io.noties.adapt.wrapper;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.noties.adapt.Item;

@RunWith(RobolectricTestRunner.class)
public class OnBindItemWrapperTest {

    @Test
    public void test() {
        //noinspection unchecked
        final Item<Item.Holder> item = mock(Item.class);
        //noinspection unchecked
        final OnBindItemWrapper.Action<Item.Holder, Item<Item.Holder>> action =
                mock(OnBindItemWrapper.Action.class);

        final Item<?> wrapped = OnBindItemWrapper.wrap(item, action);

        // wrapped, different class
        assertNotEquals(
                item.getClass(),
                wrapped.getClass()
        );

        final Item.Holder holder = mock(Item.Holder.class);
        //noinspection unchecked
        ((Item<Item.Holder>) wrapped).bind(holder);

        verify(action).apply(eq(item), eq(holder));
    }
}
