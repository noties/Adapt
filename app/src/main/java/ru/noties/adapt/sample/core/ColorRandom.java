package ru.noties.adapt.sample.core;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import java.util.Random;

public abstract class ColorRandom {

    @NonNull
    public static ColorRandom create() {
        return new Impl(new Random(42L));
    }

    @ColorInt
    public abstract int next();

    static class Impl extends ColorRandom {

        private final Random random;

        Impl(@NonNull Random random) {
            this.random = random;
        }

        @Override
        public int next() {
            final int r = (int) (255 * random.nextDouble() + .5D);
            final int g = (int) (255 * random.nextDouble() + .5D);
            final int b = (int) (255 * random.nextDouble() + .5D);
            return Color.rgb(r, g, b);
        }
    }
}
