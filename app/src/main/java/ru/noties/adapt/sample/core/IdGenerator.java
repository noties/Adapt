package ru.noties.adapt.sample.core;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicLong;

public abstract class IdGenerator {

    @NonNull
    public static IdGenerator create() {
        return new Impl();
    }

    public abstract long next();

    static class Impl extends IdGenerator {

        private final AtomicLong atomicLong = new AtomicLong();

        @Override
        public long next() {
            return atomicLong.incrementAndGet();
        }
    }
}
