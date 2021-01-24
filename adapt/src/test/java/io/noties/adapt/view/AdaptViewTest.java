package io.noties.adapt.view;

import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.R;

import static io.noties.adapt.util.ExceptionUtil.assertContains;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AdaptViewTest {

    private View view;
    private AdaptView<Item<?>> adaptView;

    @Before
    public void before() {
        view = mock(View.class);
        adaptView = new AdaptView<>(view);
    }

    @Test
    public void get_item_throws() {
        // `item()` call can throw if there is no item info, it is an internal error

        try {
            adaptView.item();
            fail();
        } catch (AdaptException e) {
            assertContains(e, "Unexpected state, there is no item bound");
        }
    }

    @Test
    public void bind_no_previous_holder() {
        // when bind is called, but item has not been rendered -> exception

        when(view.getTag(eq(R.id.adapt_internal_item))).thenReturn(mock(Item.class));

        assertNull(view.getTag(R.id.adapt_internal_holder));

        try {
            adaptView.setItem(mock(Item.class));
            fail();
        } catch (AdaptException e) {
            assertContains(e, "Unexpected state, there is no Holder associated");
        }
    }

    @Test
    public void bind() {
        // normal flow (no exceptions)

        final Item.Holder holder = mock(Item.Holder.class);
        when(view.getTag(eq(R.id.adapt_internal_item))).thenReturn(mock(Item.class));
        when(view.getTag(eq(R.id.adapt_internal_holder))).thenReturn(holder);

        //noinspection rawtypes
        final Item item = mock(Item.class);

        adaptView.setItem(item);

        verify(item, times(1)).bind(eq(holder));
        verify(view, times(1)).setTag(eq(R.id.adapt_internal_item), eq(item));
    }
}