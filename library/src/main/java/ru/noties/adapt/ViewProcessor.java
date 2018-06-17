package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.view.View;

public interface ViewProcessor<T> {

    void process(@NonNull T item, @NonNull View view);
}
