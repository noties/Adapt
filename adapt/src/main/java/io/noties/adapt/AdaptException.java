package io.noties.adapt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdaptException extends IllegalStateException {

    @NonNull
    public static AdaptException create(@NonNull Throwable cause, @NonNull String message) {
        return new AdaptException(message, cause);
    }

    @NonNull
    public static AdaptException create(@NonNull String message) {
        return new AdaptException(message);
    }

    private AdaptException(@NonNull String s) {
        super(appendVersion(s));
    }

    private AdaptException(String message, Throwable cause) {
        super(appendVersion(message), cause);
    }

    @NonNull
    private static String appendVersion(@Nullable String input) {

        if (input == null
                || input.length() == 0) {
            return PREFIX;
        }

        return PREFIX + " " + input;
    }

    private static final String PREFIX = "[v" + BuildConfig.VERSION_NAME + "]";
}
