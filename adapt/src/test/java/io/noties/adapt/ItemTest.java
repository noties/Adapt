package io.noties.adapt;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ItemTest {

    @Test
    public void wrap() {
        final Item<?> item = mock(Item.class, CALLS_REAL_METHODS);
        final Item.Wrapper wrapper = mock(Item.Wrapper.class);
        item.wrap(wrapper);
        verify(wrapper, times(1)).build(eq(item));
    }
}
