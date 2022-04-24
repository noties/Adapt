package io.noties.adapt.util;

import io.noties.adapt.Item;

/**
 * <strong>Experimental</strong>.
 *
 * @since 4.0.0
 */
public abstract class LineNumberId {

    /**
     * <em>Attempts</em> obtaining unique id based on source code line number.
     * <strong>NB</strong> if proguard is used, line numbers information must be preserved.
     * In case, if stack trace could not be obtained {@code -1} (aka {@code Item.NO_ID})
     * would be returned.
     * Possible downside is when no line number is available, then NO_ID is returned
     * and an Item\'s view would be recreated instead of properly identifying change.
     * <p>
     * Primary usage - inside a single class that creates a list of items
     */
    public static long line() {
        final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        //noinspection ConstantConditions
        if (elements != null && elements.length > 1) {
            return elements[2].getLineNumber();
        }
        return Item.NO_ID;
    }

    private LineNumberId() {
    }
}
