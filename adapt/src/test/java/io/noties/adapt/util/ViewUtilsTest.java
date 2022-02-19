package io.noties.adapt.util;

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

import io.noties.adapt.AdaptException;

@RunWith(RobolectricTestRunner.class)
public class ViewUtilsTest {

    @Test
    public void found() {
        final View view = mock(View.class, RETURNS_MOCKS);
        final View found = ViewUtils.requireView(view, 778);
        Assert.assertNotNull(found);
        verify(view, times(1)).findViewById(eq(778));
    }

    @Test
    public void not_found() {
        final View view = mock(View.class);
        try {
            ViewUtils.requireView(view, 887);
            fail();
        } catch (AdaptException e) {
            verify(view, times(1)).findViewById(eq(887));
            ExceptionUtil.assertContains(e, "not found in specified layout");
        }
    }
}
