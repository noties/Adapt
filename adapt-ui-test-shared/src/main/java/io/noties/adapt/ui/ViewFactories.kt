package io.noties.adapt.ui

import android.content.Context
import android.view.View
import org.junit.Assert
import org.mockito.Mockito
import org.robolectric.RuntimeEnvironment
import kotlin.reflect.KMutableProperty0

inline fun <reified V : View> assertViewFactory(
    expected: Class<out V>,
    property: KMutableProperty0<(Context) -> V>,
    block: ViewFactory<LayoutParams>.() -> Unit
) {
    assertViewFactory2(expected, property, block)
}

inline fun <reified V : View, reified LP : LayoutParams> assertViewFactory2(
    expected: Class<out V>,
    property: KMutableProperty0<(Context) -> V>,
    block: ViewFactory<LP>.() -> Unit
) {
    val context = RuntimeEnvironment.getApplication()

    Assert.assertEquals(
        expected,
        property.get()(context)::class.java
    )

    val mocked = Mockito.mock(expected)
    Mockito.`when`(mocked.context).thenReturn(context)
    property.set { mocked }
    Assert.assertEquals(mocked, obtainView2(block))
}