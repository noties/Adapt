package io.noties.adapt.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK], qualifiers = "xxhdpi")
class ViewFactory_Extensions_Test {

    private lateinit var context: Context

    @Before
    fun before() {
        context = RuntimeEnvironment.getApplication()
    }

    @Test
    fun `createView - empty`() {
        try {
            ViewFactory.createView(context) {
                // no calls
            }
            Assert.fail()
        } catch (t: Throwable) {
            Assert.assertEquals(
                "Unexpected state, view must contain exactly one root element",
                t.message
            )
        }
    }

    @Test
    fun `createView - multiple`() {
        try {
            ViewFactory.createView(context) {
                Text()
                Text()
            }
            Assert.fail()
        } catch (t: Throwable) {
            Assert.assertEquals(
                "Unexpected state, view must contain exactly one root element",
                t.message
            )
        }
    }

    @Test
    fun `createView - references`() {
        class Ref {
            var textView: TextView? = null
        }

        val ref = Ref()
        val view = ViewFactory.createView(context, ref) {
            Text()
                .reference(it::textView)
        }

        Assert.assertNotNull(ref.textView)
        Assert.assertEquals(view, ref.textView)
    }

    @Test
    fun `createView - single`() {
        val called = AtomicBoolean()
        val element = ViewElement<TextView, LayoutParams> {
            called.set(true)
            TextView(it)
        }

        val view = ViewFactory.createView(context) {
            element.also { this.elements.add(it) }
        }

        Assert.assertTrue(called.get())
        Assert.assertNotNull(view)
        Assert.assertEquals(TextView::class.java, view::class.java)

        // validate default layout params
        val lp = view.layoutParams
        Assert.assertEquals(LayoutParams.MATCH_PARENT, lp.width)
        Assert.assertEquals(LayoutParams.WRAP_CONTENT, lp.height)
    }

    @Test
    fun addChildren() {
        val group = FrameLayout(context)

        ViewFactory.addChildren<ViewGroup, LayoutParams>(group) {
            Text()
            Image()
        }

        Assert.assertEquals(2, group.childCount)

        Assert.assertEquals(TextView::class.java, group.getChildAt(0)::class.java)
        Assert.assertEquals(ImageView::class.java, group.getChildAt(1)::class.java)
    }
}