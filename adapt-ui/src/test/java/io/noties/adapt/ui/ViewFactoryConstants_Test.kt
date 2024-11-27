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
class ViewFactoryConstants_Test {

    @Test
    fun assertConstants() {
        assert(ViewFactoryConstants)
        // assert factory
        assert(ViewFactory<LayoutParams>(RuntimeEnvironment.getApplication()))
    }

    private fun assert(constants: ViewFactoryConstants) {
        val inputs = listOf(
            LayoutParams.MATCH_PARENT to constants.fill,
            LayoutParams.WRAP_CONTENT to constants.wrap
        )
        for ((expected, actual) in inputs) {
            Assert.assertEquals(constants.toString(), expected, actual)
        }
    }
}