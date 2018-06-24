package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * An instance of this class can be used to _post_-process a view after it was bound by {@link ItemView}
 *
 * @see OnClickViewProcessor
 * @see OnLongClickViewProcessor
 * @see AdaptBuilder#include(Class, ItemView, ViewProcessor)
 */
public interface ViewProcessor<T> {

    void process(@NonNull T item, @NonNull View view);
}
