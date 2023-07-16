package io.noties.adapt.ui.testutil

import android.content.res.Resources
import org.junit.Assert

fun assertDensity(expected: Float) {
    val density = Resources.getSystem().displayMetrics.density
    Assert.assertEquals("density", expected, density, 0.01F)
}