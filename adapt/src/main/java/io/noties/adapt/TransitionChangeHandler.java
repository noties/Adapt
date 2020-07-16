package io.noties.adapt;

import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * @see io.noties.adapt.AdaptViewGroup.ChangeHandler
 * @see io.noties.adapt.AdaptViewGroup.ChangeHandlerDef
 * @since 2.3.0-SNAPSHOT
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
public class TransitionChangeHandler implements AdaptViewGroup.ChangeHandler {

    public interface TransitionProvider {
        @Nullable
        Transition provide(@NonNull ViewGroup group);
    }

    @NonNull
    public static TransitionChangeHandler create() {
        return create(new AdaptViewGroup.ChangeHandlerDef(), null);
    }

    @NonNull
    public static TransitionChangeHandler create(@NonNull AdaptViewGroup.ChangeHandler parent) {
        return create(parent, null);
    }

    @NonNull
    public static TransitionChangeHandler create(@Nullable TransitionProvider transitionProvider) {
        return create(new AdaptViewGroup.ChangeHandlerDef(), transitionProvider);
    }

    @NonNull
    public static TransitionChangeHandler create(
            @NonNull AdaptViewGroup.ChangeHandler parent,
            @Nullable TransitionProvider transitionProvider) {
        return new TransitionChangeHandler(parent, transitionProvider);
    }

    private final AdaptViewGroup.ChangeHandler parent;
    private final TransitionProvider transitionProvider;

    protected TransitionChangeHandler(
            @NonNull AdaptViewGroup.ChangeHandler parent,
            @Nullable TransitionProvider transitionProvider) {
        this.parent = parent;
        this.transitionProvider = transitionProvider;
    }

    @Override
    public void begin(@NonNull ViewGroup group) {

        final Transition transition = transitionProvider != null
                ? transitionProvider.provide(group)
                : null;

        if (transition != null) {
            TransitionManager.beginDelayedTransition(group, transition);
        } else {
            TransitionManager.beginDelayedTransition(group);
        }
    }

    @Override
    public void removeAll(@NonNull ViewGroup group) {
        parent.removeAll(group);
    }

    @Override
    public void removeAt(@NonNull ViewGroup group, int position) {
        parent.removeAt(group, position);
    }

    @Override
    public void move(@NonNull ViewGroup group, int from, int to) {
        parent.move(group, from, to);
    }

    @Override
    public void insertAt(@NonNull ViewGroup group, @NonNull View view, int position) {
        parent.insertAt(group, view, position);
    }

    @Override
    public void end(@NonNull ViewGroup group) {
        parent.end(group);
    }
}
