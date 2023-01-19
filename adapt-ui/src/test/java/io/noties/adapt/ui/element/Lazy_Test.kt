package io.noties.adapt.ui.element

import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.renderView
import io.noties.adapt.ui.util.LazyView
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Lazy_Test {

    @Test
    fun inject() {
        newElementOfType<LazyView>()
            .lazyInject()
            .renderView {
                verify(this).inject()
            }
    }
}