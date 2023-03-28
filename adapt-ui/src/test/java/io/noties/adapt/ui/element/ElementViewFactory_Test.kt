package io.noties.adapt.ui.element

import android.content.Context
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
import io.noties.adapt.ui.obtainView2
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.reflect.KMutableProperty0

@Suppress("ClassName", "TestFunctionName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ElementViewFactory_Test {

    private val context: Context get() = RuntimeEnvironment.getApplication()

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

    private inline fun <reified V : View> assertViewFactory(
        expected: Class<out V>,
        property: KMutableProperty0<(Context) -> V>,
        block: ViewFactory<LayoutParams>.() -> Unit
    ) {
        assertViewFactory2(expected, property, block)
    }

    private inline fun <reified V : View, reified LP : LayoutParams> assertViewFactory2(
        expected: Class<out V>,
        property: KMutableProperty0<(Context) -> V>,
        block: ViewFactory<LP>.() -> Unit
    ) {
        assertEquals(
            expected,
            property.get()(context)::class.java
        )

        val mocked = mock(expected)
        `when`(mocked.context).thenReturn(context)
        property.set { mocked }
        assertEquals(mocked, obtainView2(block))
    }
}