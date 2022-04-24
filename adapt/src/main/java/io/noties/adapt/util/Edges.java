package io.noties.adapt.util;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * @since 4.0.0
 */
public class Edges {

    @NonNull
    public static Edges clear() {
        return all(0);
    }

    @NonNull
    public static Edges all(int all) {
        return new Edges(all);
    }

    @NonNull
    public static Edges vertical(int vertical) {
        return new Edges(vertical, 0);
    }

    @NonNull
    public static Edges horizontal(int horizontal) {
        return new Edges(0, horizontal);
    }

    @NonNull
    public static Edges init(int vertical, int horizontal) {
        return new Edges(vertical, horizontal);
    }

    @NonNull
    public static Edges init(int leading, int top, int trailing, int bottom) {
        return new Edges(leading, top, trailing, bottom);
    }


    public final int leading;
    public final int top;
    public final int trailing;
    public final int bottom;

    public Edges(int all) {
        this(all, all, all, all);
    }

    public Edges(int vertical, int horizontal) {
        this(horizontal, vertical, horizontal, vertical);
    }

    public Edges(int leading, int top, int trailing, int bottom) {
        this.leading = leading;
        this.top = top;
        this.trailing = trailing;
        this.bottom = bottom;
    }

    @NonNull
    public Edges plus(@NonNull Edges other) {
        return new Edges(
                leading + other.leading,
                top + other.top,
                trailing + other.trailing,
                bottom + other.bottom
        );
    }

    @NonNull
    @Override
    public String toString() {
        return "Edges{" +
                "leading=" + leading +
                ", top=" + top +
                ", trailing=" + trailing +
                ", bottom=" + bottom +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edges edges = (Edges) o;
        return leading == edges.leading && top == edges.top && trailing == edges.trailing && bottom == edges.bottom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leading, top, trailing, bottom);
    }
}
