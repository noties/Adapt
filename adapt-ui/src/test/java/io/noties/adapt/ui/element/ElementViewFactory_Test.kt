package io.noties.adapt.ui.element

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.assertViewFactory
import io.noties.adapt.ui.assertViewFactory2
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Suppress("ClassName", "TestFunctionName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ElementViewFactory_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun `element - HScroll`() {
        assertViewFactory(
            HorizontalScrollView::class.java,
            ElementViewFactory::HScroll
        ) { HScroll { } }
    }

    @Test
    fun `element - HStack`() {
        assertViewFactory(
            LinearLayout::class.java,
            ElementViewFactory::HStack
        ) { HStack { } }
    }

    @Test
    fun `element - Image`() {
        assertViewFactory(
            ImageView::class.java,
            ElementViewFactory::Image
        ) { Image() }
    }

    @Test
    fun `element - Pager`() {
        assertViewFactory(
            ViewPager::class.java,
            ElementViewFactory::Pager
        ) { Pager { } }
    }

    @Test
    fun `element - Progress`() {
        assertViewFactory(
            ProgressBar::class.java,
            ElementViewFactory::Progress
        ) { Progress() }
    }

    @Test
    fun `element - Recycler`() {
        assertViewFactory(
            RecyclerView::class.java,
            ElementViewFactory::Recycler
        ) {
            Recycler()
        }
    }

    @Test
    fun `element - Spacer`() {
        val block: ViewFactory<LinearLayout.LayoutParams>.() -> Unit = {
            Spacer()
        }
        assertViewFactory2(
            View::class.java,
            ElementViewFactory::Spacer,
            block
        )
    }

    @Test
    fun `element - Text`() {
        assertViewFactory(
            TextView::class.java,
            ElementViewFactory::Text
        ) { Text() }
    }

    @Test
    fun `element - TextInput`() {
        assertViewFactory(
            EditText::class.java,
            ElementViewFactory::TextInput
        ) { TextInput() }
    }

    @Test
    fun `element - View`() {
        assertViewFactory(
            View::class.java,
            ElementViewFactory::View
        ) { View() }
    }

    @Test
    fun `element - VScroll`() {
        assertViewFactory(
            ScrollView::class.java,
            ElementViewFactory::VScroll
        ) { VScroll { } }
    }

    @Test
    fun `element - VStack`() {
        assertViewFactory(
            LinearLayout::class.java,
            ElementViewFactory::VStack
        ) { VStack { } }
    }

    @Test
    fun `element - ZStack`() {
        assertViewFactory(
            FrameLayout::class.java,
            ElementViewFactory::ZStack
        ) { ZStack { } }
    }

    @Test
    fun `default - image`() {
        val imageView = ElementViewFactory.Image(RuntimeEnvironment.getApplication())
        Assert.assertEquals(ImageView.ScaleType.FIT_CENTER, imageView.scaleType)

        val layoutParams = imageView.layoutParams
        Assert.assertNotNull(layoutParams)
        Assert.assertEquals(LayoutParams.WRAP_CONTENT, layoutParams.width)
        Assert.assertEquals(LayoutParams.WRAP_CONTENT, layoutParams.height)
    }

    @Test
    fun `default - progress`() {
        val progressBar = ElementViewFactory.Progress(RuntimeEnvironment.getApplication())
        val layoutParams = progressBar.layoutParams
        Assert.assertNotNull(layoutParams)
        Assert.assertEquals(LayoutParams.WRAP_CONTENT, layoutParams.width)
        Assert.assertEquals(LayoutParams.WRAP_CONTENT, layoutParams.height)
    }

    @Test
    fun `contextWrapper - default`() {
        // by default just returns supplied argument
        val input = mock<Context>()
        val actual = ElementViewFactory.contextWrapper(input)
        Assert.assertEquals(input, actual)
    }

    @Test
    fun `contextWrapper - elements`() {
        // that all elements are using context from contextWrapper

        val supplied = RuntimeEnvironment.getApplication()
        val wrapped = ContextWrapper(supplied)

        ElementViewFactory.contextWrapper = { wrapped }

        val factories = listOf<Pair<String, ViewFactory<LinearLayout.LayoutParams>.(Unit) -> Unit>>(
            "HScroll" to { HScroll {} },
            "HStack" to { HStack {} },
            "Image" to { Image() },
            "Pager" to { Pager {} },
            "Progress" to { Progress() },
            "Recycler" to { Recycler() },
            "Spacer" to { Spacer() },
            "Text" to { Text() },
            "TextInput" to { TextInput() },
            "View" to { View() },
            "VScroll" to { VScroll {} },
            "VStack" to { VStack {} },
            "ZStack" to { ZStack {} },
        )

        for ((name, factory) in factories) {
            val view = ViewFactory.ViewCreator(supplied, null)
                .layoutParams(LinearLayout.LayoutParams(0, 0))
                .create(factory)
            Assert.assertTrue("factory:$name expected:$wrapped actual:${view.context}", wrapped === view.context)
        }
    }
}