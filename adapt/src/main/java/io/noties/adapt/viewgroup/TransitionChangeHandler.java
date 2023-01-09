package io.noties.adapt.viewgroup;

import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * @see AdaptViewGroup.ChangeHandler
 * @see ViewGroupChangeHandler
 */
public class TransitionChangeHandler implements AdaptViewGroup.ChangeHandler {

    /**
     * Determines which {@code ViewGroup} is used to <em>begin</em> transition
     *
     * @see ParentViewGroupProvider
     */
    public interface ViewGroupProvider {
        /**
         * @return null to use supplied {@code group}
         */
        @Nullable
        ViewGroup provide(@NonNull ViewGroup group);
    }

    /**
     * Create {@code Transition} for given {@code ViewGroup}. Can be different from
     * <em>real</em> {@code ViewGroup} parent (which {@link AdaptViewGroup} is initialized with)
     * when {@link ViewGroupChangeHandler} returns different {@code ViewGroup}
     */
    public interface TransitionProvider {
        @Nullable
        Transition provide(@NonNull ViewGroup group);
    }

    public interface Configuration {

        @NonNull
        Configuration changeHandler(@NonNull AdaptViewGroup.ChangeHandler changeHandler);

        @NonNull
        Configuration transitionProvider(@NonNull TransitionProvider transitionProvider);

        @NonNull
        Configuration viewGroupProvider(@NonNull ViewGroupProvider viewGroupProvider);
    }

    public interface Configurator {
        void configure(@NonNull Configuration configuration);
    }

    @NonNull
    public static TransitionChangeHandler create() {
        return new TransitionChangeHandler(new ConfigurationImpl());
    }

    @NonNull
    public static TransitionChangeHandler create(@NonNull Configurator configurator) {
        final ConfigurationImpl configuration = new ConfigurationImpl();
        configurator.configure(configuration);
        return new TransitionChangeHandler(configuration);
    }

    // @since $UNRELEASED;
    @NonNull
    public static TransitionChangeHandler create(@NonNull ViewGroupProvider provider) {
        final ConfigurationImpl configuration = new ConfigurationImpl();
        configuration.viewGroupProvider(provider);
        return new TransitionChangeHandler(configuration);
    }

    // @since $UNRELEASED;
    @NonNull
    public static TransitionChangeHandler createTransitionOnParent() {
        return create(new ParentViewGroupProvider());
    }

    @NonNull
    private final AdaptViewGroup.ChangeHandler changeHandler;

    @Nullable
    private final ViewGroupProvider viewGroupProvider;

    @Nullable
    private final TransitionProvider transitionProvider;


    protected TransitionChangeHandler(ConfigurationImpl configuration) {
        this.changeHandler = configuration.changeHandler;
        this.viewGroupProvider = configuration.viewGroupProvider;
        this.transitionProvider = configuration.transitionProvider;
    }

    @Override
    public void begin(@NonNull ViewGroup group) {

        final ViewGroup parent = viewGroup(group);
        final Transition transition = transition(parent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TransitionManager.endTransitions(parent);
        }

        if (transition != null) {
            TransitionManager.beginDelayedTransition(parent, transition);
        } else {
            TransitionManager.beginDelayedTransition(parent);
        }

        // Note that original ViewGroup is delivered to wrapped ChangeHandler
        changeHandler.begin(group);
    }

    @Override
    public void removeAll(@NonNull ViewGroup group) {
        changeHandler.removeAll(group);
    }

    @Override
    public void removeAt(@NonNull ViewGroup group, int position) {
        changeHandler.removeAt(group, position);
    }

    @Override
    public void move(@NonNull ViewGroup group, int from, int to) {
        changeHandler.move(group, from, to);
    }

    @Override
    public void insertAt(@NonNull ViewGroup group, @NonNull View view, int position) {
        changeHandler.insertAt(group, view, position);
    }

    @Override
    public void end(@NonNull ViewGroup group) {
        changeHandler.end(group);
    }

    @NonNull
    private ViewGroup viewGroup(@NonNull ViewGroup group) {
        final ViewGroup parent = viewGroupProvider != null
                ? viewGroupProvider.provide(group)
                : null;
        return parent != null
                ? parent
                : group;
    }

    @Nullable
    private Transition transition(@NonNull ViewGroup group) {
        return transitionProvider != null
                ? transitionProvider.provide(group)
                : null;
    }

    private static class ConfigurationImpl implements Configuration {

        AdaptViewGroup.ChangeHandler changeHandler = new ViewGroupChangeHandler();
        TransitionProvider transitionProvider;
        ViewGroupProvider viewGroupProvider;

        @NonNull
        @Override
        public Configuration changeHandler(@NonNull AdaptViewGroup.ChangeHandler changeHandler) {
            this.changeHandler = changeHandler;
            return this;
        }

        @NonNull
        @Override
        public Configuration transitionProvider(@NonNull TransitionProvider transitionProvider) {
            this.transitionProvider = transitionProvider;
            return this;
        }

        @NonNull
        @Override
        public Configuration viewGroupProvider(@NonNull ViewGroupProvider viewGroupProvider) {
            this.viewGroupProvider = viewGroupProvider;
            return this;
        }
    }
}
