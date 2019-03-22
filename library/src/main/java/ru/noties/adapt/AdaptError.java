package ru.noties.adapt;

import android.support.annotation.NonNull;

import java.util.Locale;

class AdaptError extends IllegalStateException {

    @NonNull
    static AdaptError halt(@NonNull String message, Object... args) {
        return new AdaptError(message(message, args));
    }

    @NonNull
    static AdaptError halt(@NonNull Throwable cause, @NonNull String message, Object... args) {
        return new AdaptError(message(message, args), cause);
    }

    private AdaptError(@NonNull String message) {
        super(message);
    }

    private AdaptError(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }

    private static final String MESSAGE_PREFIX = "Adapt[v" + BuildConfig.VERSION_NAME + "] ";

    @NonNull
    private static String message(@NonNull String message, Object... args) {
        return MESSAGE_PREFIX + String.format(Locale.US, message, args);
    }
}
