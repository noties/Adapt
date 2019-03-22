package ru.noties.adapt.next;

import android.support.annotation.NonNull;

abstract class TypeUtils {

    static boolean sameClass(@NonNull Object left, @NonNull Object right) {
        return left.getClass().equals(right.getClass());
    }

    private TypeUtils() {
    }
}
