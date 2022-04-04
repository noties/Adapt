package io.noties.adapt.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LineNumberIdTest {

    @Test
    public void test() {
        final List<Long> list = new ArrayList<>();
        list.add(LineNumberId.line());
        list.add(LineNumberId.line());
        list.add(LineNumberId.line());
        list.add(LineNumberId.line());
        list.add(LineNumberId.line());
        Assert.assertEquals(list.toString(), 5, list.size());

        // validate all unique numbers (no duplicates)
        Assert.assertEquals(5, new HashSet<>(list).size());
    }
}