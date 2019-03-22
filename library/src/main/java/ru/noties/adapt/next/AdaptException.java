package ru.noties.adapt.next;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;

import ru.noties.adapt.BuildConfig;

class AdaptException extends IllegalStateException {

    @NonNull
    static AdaptException create(@NonNull Throwable cause) {
        return new AdaptException(cause);
    }

    @NonNull
    static AdaptException create(@NonNull Throwable cause, @NonNull String message) {
        return new AdaptException(message, cause);
    }

    @NonNull
    static AdaptException create(@NonNull Throwable cause, @NonNull String message, Object... args) {
        return new AdaptException(String.format(Locale.US, message, args), cause);
    }

    @NonNull
    static AdaptException create(@NonNull String message) {
        return new AdaptException(message);
    }

    @NonNull
    static AdaptException create(@NonNull String message, Object... args) {
        return new AdaptException(String.format(Locale.US, message, args));
    }

    private AdaptException(@NonNull String s) {
        super(appendVersion(s));
    }

    private AdaptException(String message, Throwable cause) {
        super(appendVersion(message), cause);
    }

    private AdaptException(Throwable cause) {
        super(appendVersion(null), cause);
    }

    @NonNull
    private static String appendVersion(@Nullable String input) {

        final String version = "[v" + BuildConfig.VERSION_NAME + "]";
        if (input == null
                || input.length() == 0) {
            return version;
        }

        return version + " " + input;
    }
}
