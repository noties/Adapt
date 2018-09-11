package ru.noties.adapt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static ru.noties.adapt.TestUtils.assertThrows;

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

        assertThrows(
                "AdaptBuilder: Cannot include an interface or an abstract class: " + I.class.getName(),
                new Runnable() {
                    @Override
                    public void run() {
                        new AdaptBuilder<>(I.class).include(I.class, mock(ItemView.class));
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        new AdaptBuilder<>(I.class)
                                .include(I.class, mock(ItemView.class), mock(ViewProcessor.class));
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void include_abstract_throws() {

        assertThrows(
                "AdaptBuilder: Cannot include an interface or an abstract class: " + A.class.getName(),
                new Runnable() {
                    @Override
                    public void run() {
                        new AdaptBuilder<>(A.class)
                                .include(A.class, mock(ItemView.class));
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        new AdaptBuilder<>(A.class)
                                .include(A.class, mock(ItemView.class), mock(ViewProcessor.class));
                    }
                }
        );
    }

    @Test
    public void build_nothing_added_throws() {

        assertThrows(
                "AdaptSource.Builder: No entries were added",
                new Runnable() {
                    @Override
                    public void run() {
                        new AdaptBuilder<>(A.class).build();
                    }
                }
        );
    }

    private static class O {
    }

    @SuppressWarnings("unchecked")
    @Test
    public void duplicate_throws() {
        assertThrows(
                "AdaptBuilder: Provided type has been added already: " + O.class.getName(),
                new Runnable() {
                    @Override
                    public void run() {
                        new AdaptBuilder<>(O.class)
                                .include(O.class, mock(ItemView.class))
                                .include(O.class, mock(ItemView.class));
                    }
                }
        );
    }
}