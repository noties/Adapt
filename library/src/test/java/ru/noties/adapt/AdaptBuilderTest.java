package ru.noties.adapt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AdaptBuilderTest {

    private interface I {
    }

    private static abstract class A {
    }

    @SuppressWarnings("unchecked")
    @Test
    public void include_interface_throws() {
        try {
            new AdaptBuilder<>(I.class)
                    .include(I.class, mock(ItemView.class));
            assertTrue(false);
        } catch (AdaptConfigurationError e) {
            assertTrue(true);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void include_abstract_throws() {
        try {
            new AdaptBuilder<>(A.class)
                    .include(A.class, mock(ItemView.class));
            assertTrue(false);
        } catch (AdaptConfigurationError e) {
            assertTrue(true);
        }
    }
}