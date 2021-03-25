package io.noties.adapt.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.R;

import static io.noties.adapt.util.ExceptionUtil.assertContains;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(RobolectricTestRunner.class)
public class AdaptViewTest {

    private View view;
    private AdaptView adaptView;

    @Before
    public void before() {
        view = mock(View.class);
        adaptView = AdaptView.init(
                mock(ViewGroup.class, RETURNS_MOCKS),
                new AdaptView.Configurator() {
                    @Override
                    public void configure(@NonNull AdaptView.Configuration configuration) {
                        configuration.layoutInflater(mock(LayoutInflater.class));
                    }
                }
        );
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