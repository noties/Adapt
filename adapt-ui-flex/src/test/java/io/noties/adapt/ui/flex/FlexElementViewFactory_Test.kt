package io.noties.adapt.ui.flex

import com.google.android.flexbox.FlexboxLayout
import io.noties.adapt.ui.assertViewFactory
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName", "TestFunctionName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class FlexElementViewFactory_Test {

    @After
    fun after() {
        FlexElementViewFactory.reset()
    }

    @Test
    fun `element - Flex`() {
        assertViewFactory(
            FlexboxLayout::class.java,
            FlexElementViewFactory::Flex
        ) {
            Flex { }
        }
    }
}