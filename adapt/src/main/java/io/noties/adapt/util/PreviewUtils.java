package io.noties.adapt.util;

import android.annotation.SuppressLint;

public abstract class PreviewUtils {
    private PreviewUtils() {
    }

    public static boolean isInPreview() {
        return Holder.isInPreview;
    }

    @SuppressLint("PrivateApi")
    private static class Holder {
        static final boolean isInPreview;

        static {
            boolean value;
            try {
                Class.forName("com.android.ide.common.rendering.api.RenderSession");
                value = true;
            } catch (Throwable t) {
                value = false;
            }
            isInPreview = value;
        }
    }
}
