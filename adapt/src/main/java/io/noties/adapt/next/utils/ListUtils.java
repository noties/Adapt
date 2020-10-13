package io.noties.adapt.next.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class ListUtils {

    @NonNull
    public static <T> List<T> freeze(@Nullable List<T> list) {
        return isEmpty(list)
                ? Collections.<T>emptyList()
                : Collections.unmodifiableList(list);
    }

    public static <T> boolean isEmpty(@Nullable List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> int size(@Nullable List<T> list) {
        return isEmpty(list) ? 0 : list.size();
    }

    private ListUtils() {
    }
}
