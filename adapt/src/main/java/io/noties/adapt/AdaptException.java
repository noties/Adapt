package io.noties.adapt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

public class AdaptException extends IllegalStateException {

    @NonNull
    public static AdaptException create(@NonNull Throwable cause, @NonNull String message, Object... args) {
        return new AdaptException(String.format(Locale.US, message, args), cause);
    }

    @NonNull
    public static AdaptException create(@NonNull String message) {
        return new AdaptException(message);
    }

    @NonNull
    public static AdaptException create(@NonNull String message, Object... args) {
        return new AdaptException(String.format(Locale.US, message, args));
    }

    private AdaptException(@NonNull String s) {
        super(appendVersion(s));
    }

    private AdaptException(String message, Throwable cause) {
        super(appendVersion(message), cause);
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
