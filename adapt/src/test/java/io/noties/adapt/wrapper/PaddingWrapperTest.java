package io.noties.adapt.wrapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.noties.adapt.Item;
import io.noties.adapt.Item.Holder;
import io.noties.adapt.util.Edges;

@RunWith(RobolectricTestRunner.class)
public class PaddingWrapperTest {

    @Test
    public void test() {
        final Edges edges = Edges.init(1, 2, 3, 4);
        final PaddingWrapper wrapper = new PaddingWrapper(
                mock(Item.class),
                edges
        );
        final View view = mock(View.class);
        final Holder holder = mock(Holder.class);
        when(holder.itemView()).thenReturn(view);

        wrapper.bind(holder);

        verify(view, times(1)).setPaddingRelative(
                1, 2, 3, 4
        );
    }
}