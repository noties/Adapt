package io.noties.adapt.ui.shape

import android.graphics.drawable.Drawable
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ShapeDrawable_Extensions_Test {

    @Test
    fun invalidate() {
        class Ref

        val ref = Ref()

        val drawable = ShapeDrawable(Rectangle(), ref) {}
        val callback = mockt<Drawable.Callback>()
        drawable.callback = callback

        val called = AtomicBoolean()
        drawable.invalidate {
            called.set(true)
            assertEquals(ref, this)
        }
        // verify callback after `invalidate` block (invalidation happens after invoking it)
        verify(callback).invalidateDrawable(eq(drawable))
        assertEquals("called", true, called.get())
    }
}