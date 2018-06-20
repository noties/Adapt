package ru.noties.adapt.sample.core;

import android.support.annotation.NonNull;

import java.util.Random;

public abstract class ShapeRandom {

    @NonNull
    public static ShapeRandom create() {
        return new Impl(new Random(13L));
    }

    @NonNull
    public abstract ShapeType next();


    static class Impl extends ShapeRandom {

        private static final ShapeType[] TYPES = ShapeType.values();

        private final Random random;

        Impl(@NonNull Random random) {
            this.random = random;
        }

        @NonNull
        @Override
        public ShapeType next() {
            return TYPES[random.nextInt(TYPES.length)];
        }
    }
}
