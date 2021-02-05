package io.noties.adapt;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class AdaptStore {

    private static final int ID = R.id.adapt_internal;

    // TODO: can we have a provider be set manually (for example when there is not view available,
    //  in case of an AlertDialog, so we can provide it manually)
    public static void assign(@NonNull View view, @NonNull Adapt adapt) {
        view.setTag(ID, adapt);
    }

    @Nullable
    public static Adapt find(@NonNull View view) {
        return findAdapt(view);
    }

    @Nullable
    private static Adapt findAdapt(@NonNull View v) {

        View view = v;

        Adapt adapt;
        ViewParent parent;

        while (true) {

            adapt = (Adapt) view.getTag(ID);
            if (adapt != null) {
                break;
            }

            parent = view.getParent();
            if (parent != null) {
                view = (View) parent;
            } else {
                break;
            }
        }

        return adapt;
    }

    private AdaptStore() {
    }
}
