package io.noties.adapt.ui

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ViewFactoryConstantsTest {

    @Test
    fun assertConstants() {
        assert(ViewFactoryConstants.Impl)
        // assert factory
        assert(ViewFactory<LayoutParams>(RuntimeEnvironment.getApplication()))
    }

    private fun assert(constants: ViewFactoryConstants) {
        val inputs = listOf(
            LayoutParams.MATCH_PARENT to constants.FILL,
            LayoutParams.WRAP_CONTENT to constants.WRAP
        )
        for ((expected, actual) in inputs) {
            Assert.assertEquals(constants.toString(), expected, actual)
        }
    }
}