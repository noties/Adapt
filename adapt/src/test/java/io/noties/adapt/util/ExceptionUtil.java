package io.noties.adapt.util;

import androidx.annotation.NonNull;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class ExceptionUtil {

    public static void assertContains(@NonNull Throwable throwable, @NonNull String message) {
        final String actual;
        {
            final String m = throwable.getMessage();
            if (m == null) {
                actual = "";
            } else {
                actual = m;
            }
        }
        if (!actual.contains(message)) {
            fail("`" + message + "` is not contained in: `" + actual + "`");
        } else {
            assertTrue(true);
        }
    }

    private ExceptionUtil() {
    }
}
