package io.noties.adapt.util;

import android.content.res.Resources;
import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import java.util.Locale;

import io.noties.adapt.AdaptException;


public abstract class ViewUtils {

    @NonNull
    @CheckResult
    public static <V extends View> V requireView(@NonNull View view, @IdRes int id) {

        final View v = view.findViewById(id);

        if (v == null) {
            throw notFoundException(view, id);
        }

        //noinspection unchecked
        return (V) v;
    }

    @NonNull
    @CheckResult
    public static AdaptException notFoundException(@NonNull View view, @IdRes int id) {
        final String message = "View with id: `%s` not found in specified layout: %s";
        try {
            final Resources resources = view.getResources();
            final String idResourceName = resources != null
                    ? resources.getResourceName(id)
                    : null;
            throw AdaptException.create(String.format(Locale.ROOT, message, idResourceName, view));
        } catch (Resources.NotFoundException e) {
            // throw AdaptException with NotFound exception as the cause
            throw AdaptException.create(e, String.format(Locale.ROOT, message, "null", view));
        }
    }

    private ViewUtils() {
    }
}
