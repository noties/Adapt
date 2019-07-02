package io.noties.adapt;

import android.content.res.Resources;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

abstract class ViewUtils {

    @NonNull
    static <V extends View> V requireView(@NonNull View view, @IdRes int id) {

        final View v = view.findViewById(id);

        if (v == null) {
            final String message = "View with id: `%s` not found in specified layout: %s";
            try {
                final Resources resources = view.getResources();
                final String idResourceName = resources != null
                        ? resources.getResourceName(id)
                        : null;
                throw AdaptException.create(message, idResourceName, view);
            } catch (Resources.NotFoundException e) {
                // throw AdaptException with NotFound exception as the cause
                throw AdaptException.create(e, message, "null", view);
            }
        }

        //noinspection unchecked
        return (V) v;
    }

    private ViewUtils() {
    }
}
