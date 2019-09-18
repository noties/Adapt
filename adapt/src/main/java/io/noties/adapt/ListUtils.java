package io.noties.adapt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

// @since 2.3.0-SNAPSHOT
abstract class ListUtils {

    @NonNull
    static <T> List<T> safeList(@Nullable List<T> list) {
        return list != null
                ? Collections.unmodifiableList(list)
                : Collections.<T>emptyList();
    }

    private ListUtils() {
    }
}
