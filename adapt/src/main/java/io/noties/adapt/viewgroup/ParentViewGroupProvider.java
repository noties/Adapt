package io.noties.adapt.viewgroup;

import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ParentViewGroupProvider implements TransitionChangeHandler.ViewGroupProvider {
    @Nullable
    @Override
    public ViewGroup provide(@NonNull ViewGroup group) {
        final ViewParent parent = group.getParent();
        if (parent instanceof ViewGroup) {
            return (ViewGroup) parent;
        }
        return null;
    }
}
