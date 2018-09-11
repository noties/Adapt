package ru.noties.adapt;

import android.support.annotation.NonNull;

import static org.junit.Assert.assertTrue;

abstract class TestUtils {

    static void assertThrows(@NonNull String message, Runnable... actions) {

        assertTrue(message, actions.length > 0);

        for (Runnable action : actions) {
            try {
                action.run();
                assertTrue(false);
            } catch (AdaptError e) {
                assertTrue(String.format("expected: %s, actual: %s", message, e.getMessage()), e.getMessage().contains(message));
            }
        }
    }

    private TestUtils() {
    }
}
