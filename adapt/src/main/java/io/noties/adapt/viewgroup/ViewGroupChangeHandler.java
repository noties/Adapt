package io.noties.adapt.viewgroup;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class ViewGroupChangeHandler implements AdaptViewGroup.ChangeHandler {

    @Override
    public void begin(@NonNull ViewGroup group) {
        // no op
    }

    @Override
    public void removeAll(@NonNull ViewGroup group) {
        group.removeAllViews();
    }

    @Override
    public void removeAt(@NonNull ViewGroup group, int position) {
        group.removeViewAt(position);
    }

    @Override
    public void move(@NonNull ViewGroup group, int from, int to) {
        final View child = group.getChildAt(from);
        group.removeViewAt(from);
        group.addView(child, to);
    }

    @Override
    public void insertAt(@NonNull ViewGroup group, @NonNull View view, int position) {
        group.addView(view, position);
    }

    @Override
    public void end(@NonNull ViewGroup group) {
        // no op
    }
}
