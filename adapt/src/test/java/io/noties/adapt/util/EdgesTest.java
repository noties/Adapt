package io.noties.adapt.util;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;

public class EdgesTest {

    @Test
    public void clear() {
        assertEdges(new Edges(0), Edges.clear());
    }

    @Test
    public void all() {
        assertEdges(new Edges(1), Edges.all(1));
    }

    @Test
    public void vertical() {
        assertEdges(new Edges(0, 3, 0, 3), Edges.vertical(3));
    }

    @Test
    public void horizontal() {
        assertEdges(new Edges(4, 0, 4, 0), Edges.horizontal(4));
    }

    @Test
    public void verticalHorizontal() {
        assertEdges(
                new Edges(5, 6, 5, 6),
                Edges.init(6, 5)
        );
    }

    @Test
    public void init() {
        assertEdges(new Edges(7), Edges.init(7, 7, 7, 7));
    }

    @Test
    public void constructor_1() {
        final Edges edges = new Edges(8);
        Assert.assertEquals(edges.toString(), 8, edges.leading);
        Assert.assertEquals(edges.toString(), 8, edges.top);
        Assert.assertEquals(edges.toString(), 8, edges.trailing);
        Assert.assertEquals(edges.toString(), 8, edges.bottom);
    }

    @Test
    public void constructor_2() {
        final Edges edges = new Edges(9, 10);
        Assert.assertEquals(edges.toString(), 10, edges.leading);
        Assert.assertEquals(edges.toString(), 9, edges.top);
        Assert.assertEquals(edges.toString(), 10, edges.trailing);
        Assert.assertEquals(edges.toString(), 9, edges.bottom);
    }

    @Test
    public void constructor_3() {
        final Edges edges = new Edges(11, 12, 13, 14);
        Assert.assertEquals(edges.toString(), 11, edges.leading);
        Assert.assertEquals(edges.toString(), 12, edges.top);
        Assert.assertEquals(edges.toString(), 13, edges.trailing);
        Assert.assertEquals(edges.toString(), 14, edges.bottom);
    }

    @Test
    public void plus() {
        final Edges edges = new Edges(15, 16, 17, 18);
        final Edges sum = edges.plus(Edges.all(1));
        Assert.assertEquals(sum.toString(), 16, sum.leading);
        Assert.assertEquals(sum.toString(), 17, sum.top);
        Assert.assertEquals(sum.toString(), 18, sum.trailing);
        Assert.assertEquals(sum.toString(), 19, sum.bottom);
    }

    private static void assertEdges(@NonNull Edges expected, @NonNull Edges actual) {
        Assert.assertEquals(expected.toString(), expected, actual);
    }
}