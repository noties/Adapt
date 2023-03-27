package io.noties.adapt.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.ShapeDrawable
import io.noties.adapt.ui.testutil.mockt
import io.noties.adapt.ui.testutil.value
import io.noties.adapt.ui.util.Gravity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.anyFloat
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.argumentCaptor
import org.mockito.verification.VerificationMode
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import org.robolectric.util.ReflectionHelpers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ViewElement_Extensions_Test {

    @Test
    fun minimumSize() {

        val inputs = listOf(
            null to null,
            33 to null,
            null to 34,
            33 to 34
        )

        for ((width, height) in inputs) {
            newElement()
                .minimumSize(width = width, height = height)
                .renderView {
                    verify(this, mode(width)).minimumWidth = value(width)
                    verify(this, mode(height)).minimumHeight = value(height)
                }
        }
    }

    @Test
    fun scrollBarStyle() {
        newElement()
            .scrollBarStyle(88)
            .renderView {
                verify(this).scrollBarStyle = eq(88)
            }
    }

    @Test
    fun overScrollMode() {
        newElement()
            .overScrollMode(77)
            .renderView {
                verify(this).overScrollMode = eq(77)
            }
    }

    @Test
    fun translation() {
        val inputs = listOf(
            Triple(null, null, null),
            Triple(1, null, null),
            Triple(null, 2, null),
            Triple(null, null, 3),
            Triple(4, 5, 6)
        )

        for ((x, y, z) in inputs) {
            newElement()
                .translation(x, y, z)
                .renderView {
                    verify(this, mode(x)).translationX = value(x?.toFloat())
                    verify(this, mode(y)).translationY = value(y?.toFloat())
                    verify(this, mode(z)).translationZ = value(z?.toFloat())
                }
        }
    }

    @Test
    fun elevation() {
        newElement()
            .elevation(99)
            .renderView {
                verify(this).elevation = eq(99F)
            }
    }

    @Test
    fun onClick() {
        val inputs = listOf(
            null,
            {}
        )
        for (input in inputs) {
            newElement()
                .onClick(input)
                .renderView {
                    val captor = ArgumentCaptor.forClass(View.OnClickListener::class.java)
                    verify(this).setOnClickListener(captor.capture())
                    if (input == null) {
                        Assert.assertNull("null input", captor.value)
                    } else {
                        Assert.assertNotNull("not null input", captor.value)
                    }
                }
        }
    }

    @Test
    fun `onClick - debounce`() {
        class Action(val counter: AtomicInteger = AtomicInteger(0)) {
            val count: Int get() = counter.get()
            fun click() {
                counter.incrementAndGet()
            }
        }

        val inputs: List<Triple<Boolean, Long, Action?>> = listOf(
            Triple(true, 100L, null),
            Triple(false, 1000L, Action()),
            Triple(true, 10L, Action())
        )

        for ((debounce, debounceMillis, action) in inputs) {
            newElement()
                .onClick(debounce, debounceMillis, action?.let { it::click })
                .renderView {
                    if (action == null) {
                        verify(this).setOnClickListener(org.mockito.kotlin.eq(null))
                    } else {
                        val captor = argumentCaptor<View.OnClickListener>()
                        verify(this).setOnClickListener(captor.capture())

                        val listener = captor.value
                        if (!debounce) {
                            // each trigger would call action - no logic
                            listener.onClick(this)
                            listener.onClick(this)
                            listener.onClick(this)

                            Assert.assertEquals(3, action.count)
                        } else {

                            listener.onClick(this)
                            listener.onClick(this)
                            Assert.assertEquals(1, action.count)

                            // thread.sleep does not affect SystemClock,
                            //  but this is even better (no code execution delya)
                            ShadowSystemClock.advanceBy(debounceMillis + 1, TimeUnit.MILLISECONDS)

                            listener.onClick(this)
                            listener.onClick(this)
                            // at this point only 2 actions should be delivered
                            Assert.assertEquals(2, action.count)
                        }
                    }
                }
        }
    }

    @Test
    fun alpha() {
        newElement()
            .alpha(76F)
            .renderView {
                verify(this).alpha = eq(76F)
            }
    }

    @Test
    fun visible() {
        val inputs = listOf(
            false to View.GONE,
            true to View.VISIBLE
        )

        for ((value, visibility) in inputs) {
            newElement()
                .visible(value)
                .renderView {
                    verify(this).visibility = eq(visibility)
                }
        }
    }

    @Test
    fun selected() {
        newElement()
            .selected(true)
            .renderView {
                verify(this).isSelected = eq(true)
            }
    }

    @Test
    fun activated() {
        newElement()
            .activated(true)
            .renderView {
                verify(this).isActivated = eq(true)
            }
    }

    @Test
    fun enabled() {
        newElement()
            .enabled(false)
            .renderView {
                verify(this).isEnabled = eq(false)
            }
    }

    @Test
    fun `padding - individual`() {
        class Padding(val leading: Int?, val top: Int?, val trailing: Int?, val bottom: Int?)

        val inputs = listOf(
            Padding(null, null, null, null),
            Padding(1, null, null, null),
            Padding(null, 2, null, null),
            Padding(null, null, 3, null),
            Padding(null, null, null, 4),
            Padding(5, 6, 7, 8),
        )

        for (input in inputs) {
            newElement()
                .padding(input.leading, input.top, input.trailing, input.bottom)
                .renderView {

                    // if used directly in the `eq` matcher, invalid value
                    //  is being recorder (mockito expects actual call to view.getPadding*) instead of value
                    val originalStart = paddingStart
                    val originalTop = paddingTop
                    val originalEnd = paddingEnd
                    val originalBottom = paddingBottom

                    verify(this).setPaddingRelative(
                        eq(input.leading ?: originalStart),
                        eq(input.top ?: originalTop),
                        eq(input.trailing ?: originalEnd),
                        eq(input.bottom ?: originalBottom)
                    )
                }
        }
    }

    @Test
    fun `padding - horizontalVertical`() {
        val inputs = listOf(
            null to null,
            1 to null,
            null to 2,
            3 to 4
        )

        for ((h, v) in inputs) {
            newElement()
                .padding(horizontal = h, vertical = v)
                .renderView {

                    val originalStart = paddingStart
                    val originalTop = paddingTop
                    val originalEnd = paddingEnd
                    val originalBottom = paddingBottom

                    verify(this, times(1)).setPaddingRelative(
                        eq(h ?: originalStart),
                        eq(v ?: originalTop),
                        eq(h ?: originalEnd),
                        eq(v ?: originalBottom)
                    )
                }
        }
    }

    @Test
    fun `padding - all`() {
        val input = 812
        newElement()
            .padding(input)
            .renderView {
                verify(this).setPaddingRelative(
                    eq(input),
                    eq(input),
                    eq(input),
                    eq(input)
                )
            }
    }

    @Test
    fun `foreground - drawable`() {
        val inputs = listOf(
            null to null,
            mock(Drawable::class.java) to null,
            null to Gravity.center,
            mock(Drawable::class.java) to Gravity.bottom
        )

        for ((drawable, gravity) in inputs) {
            newElement()
                .foreground(drawable, gravity)
                .renderView {
                    verify(this).foreground = eq(drawable)
                    verify(this, gravity?.let { times(1) } ?: never()).foregroundGravity =
                        gravity?.let { eq(it.value) } ?: anyInt()
                }
        }
    }

    @Test
    fun `foreground - shape`() {
        val shape = mock(Shape::class.java)

        val inputs = listOf(
            shape to null,
            shape to Gravity.trailing
        )

        for ((s, gravity) in inputs) {
            newElement()
                .foreground(s, gravity)
                .renderView {
                    val captor = ArgumentCaptor.forClass(Drawable::class.java)
                    verify(this).foreground = captor.capture()
                    verify(this, gravity?.let { times(1) } ?: never()).foregroundGravity =
                        gravity?.let { eq(it.value) } ?: anyInt()

                    Assert.assertTrue(
                        "Foreground is instance of ShapeDrawable, class:${captor.value::class.java.name}",
                        captor.value is ShapeDrawable<*>
                    )
                }
        }
    }

    @Test
    fun foregroundDefaultSelectable() {
        val context = mock(Context::class.java)
        val drawable = mock(Drawable::class.java)
        val typedArray = mock(TypedArray::class.java)
        val view = mock(View::class.java)
        `when`(typedArray.getDrawable(eq(0))).thenReturn(drawable)
        `when`(context.obtainStyledAttributes(eq(intArrayOf(android.R.attr.selectableItemBackground)))).thenReturn(
            typedArray
        )
        `when`(view.context).thenReturn(context)

        val element = ViewElement<View, ViewGroup.LayoutParams> { view }.also {
            it.init(context)
        }
        element.foregroundDefaultSelectable()
            .renderView {
                verify(this).foreground = eq(drawable)
            }
    }

    @Test
    fun `background - drawable`() {
        val inputs = listOf(
            null,
            mock(Drawable::class.java)
        )
        for (input in inputs) {
            newElement()
                .background(input)
                .renderView {
                    verify(this).background = eq(input)
                }
        }
    }

    @Test
    fun `background - shape`() {
        val shape = Rectangle()
        newElement()
            .background(shape)
            .renderView {
                val captor = ArgumentCaptor.forClass(Drawable::class.java)
                verify(this).background = captor.capture()

                Assert.assertTrue(
                    "Background drawable is instance of ShapeDrawable, class:${captor.value::class.java.name}",
                    captor.value is ShapeDrawable<*>
                )
            }
    }

    @Test
    fun `background - color`() {
        val input = 0xFFff0000.toInt()
        newElement()
            .background(input)
            .renderView {
                verify(this).setBackgroundColor(eq(input))
            }
    }

    @Test
    fun backgroundDefaultSelectable() {
        val context = mock(Context::class.java)
        val drawable = mock(Drawable::class.java)
        val typedArray = mock(TypedArray::class.java)
        val view = mock(View::class.java)
        `when`(typedArray.getDrawable(eq(0))).thenReturn(drawable)
        `when`(context.obtainStyledAttributes(eq(intArrayOf(android.R.attr.selectableItemBackground)))).thenReturn(
            typedArray
        )
        `when`(view.context).thenReturn(context)

        val element = ViewElement<View, ViewGroup.LayoutParams> { view }.also {
            it.init(context)
        }
        element
            .backgroundDefaultSelectable()
            .renderView {
                verify(this).background = eq(drawable)
            }
    }

    @Test
    fun id() {
        val input = R.id.adapt_internal
        newElement()
            .id(input)
            .renderView {
                verify(this).id = eq(input)
            }
    }

    @Test
    fun tag() {
        // without key just setTag
        // with key setTag(key, object)
        val inputs = listOf(
            null to "no-key",
            3 to "with-key"
        )

        for (input in inputs) {
            newElement()
                .tag(input.second, input.first)
                .renderView {
                    val key = input.first
                    if (key == null) {
                        verify(this).tag = eq(input.second)
                    } else {
                        verify(this).setTag(eq(key), eq(input.second))
                    }
                }
        }
    }

    @Test
    fun `reference - view - lateinit`() {
        class Ref {
            lateinit var view: View
        }

        val ref = Ref()

        newElement()
            .reference(ref::view)
            .render()

        Assert.assertNotNull(ref.view)
    }

    @Test
    fun `reference - view - nullable`() {
        class Ref {
            var view: View? = null
        }

        val ref = Ref()

        newElement()
            .reference(ref::view)
            .render()

        Assert.assertNotNull(ref.view)
    }

    @Test
    fun `reference - view - subtypes`() {
        class Ref {
            lateinit var view: View
            lateinit var textView: TextView
        }

        val ref = Ref()

        newElementOfType<TextView>()
            .reference(ref::view)
            .reference(ref::textView)
            .render()

        Assert.assertNotNull(ref.view)
        Assert.assertNotNull(ref.textView)
        Assert.assertEquals(ref.view, ref.textView)
    }

    @Test
    fun `reference - element - lateinit`() {
        class Ref {
            lateinit var element: ViewElement<View, *>
        }

        val ref = Ref()

        newElement()
            .reference(ref::element)

        Assert.assertNotNull(ref.element)
    }

    @Test
    fun `reference - element - nullable`() {
        class Ref {
            var element: ViewElement<View, *>? = null
        }

        val ref = Ref()

        newElement()
            .reference(ref::element)

        Assert.assertNotNull(ref.element)
    }

    @Test
    fun `reference - element - subtype`() {
        class Ref {
            lateinit var element: ViewElement<out View, *>
            lateinit var textElement: ViewElement<TextView, *>
        }

        val ref = Ref()

        newElementOfType<TextView>()
            .reference(ref::textElement)
            .reference(ref::element)

        Assert.assertNotNull(ref.element)
        Assert.assertNotNull(ref.textElement)
        Assert.assertEquals(ref.element, ref.textElement)
    }

    @Test
    fun `scale - xy`() {
        val input = 543F
        newElement()
            .scale(input)
            .renderView {
                verify(this).scaleX = eq(input)
                verify(this).scaleY = eq(input)
            }
    }

    @Test
    fun `scale - x & y`() {
        val inputs = listOf(
            null to null,
            2F to null,
            null to 3F,
            4F to 5F
        )

        for ((x, y) in inputs) {
            newElement()
                .scale(x, y)
                .renderView {
                    verify(this, mode(x)).scaleX = value(x)
                    verify(this, mode(y)).scaleY = value(y)
                }
        }
    }

    @Test
    fun rotation() {
        val input = 82F
        newElement()
            .rotate(input)
            .renderView {
                verify(this).rotation = eq(input)
            }
    }

    @Test
    fun onLongClick() {
        val inputs = listOf(
            null,
            {}
        )

        for (input in inputs) {
            newElement()
                .onLongClick(input)
                .renderView {
                    verify(this).setOnLongClickListener(
                        if (input == null) eq(null) else any(View.OnLongClickListener::class.java)
                    )
                }
        }
    }

    @Test
    fun onScrollChanged() {
        val inputs: List<((view: View, x: Int, y: Int) -> Unit)?> = listOf(
            null,
            { _, _, _ -> }
        )
        for (input in inputs) {
            newElement()
                .onViewScrollChanged(input)
                .renderView {
                    verify(this).setOnScrollChangeListener(
                        if (input == null) eq(null) else any(View.OnScrollChangeListener::class.java)
                    )
                }
        }
    }

    @Test
    fun `focusable - single`() {
        // when focusableInTouch mode is omitted
        // by default it uses the same value as focusable
        val inputs = listOf(
            false to (false to false),
            true to (true to true)
        )

        for ((focusable, values) in inputs) {
            newElement()
                .focusable(focusable)
                .renderView {
                    val (f, fitm) = values
                    verify(this).isFocusable = eq(f)
                    verify(this).isFocusableInTouchMode = eq(fitm)
                }
        }
    }

    @Test
    fun `focusable - both`() {
        val inputs = listOf(
            false to false,
            false to true,
            true to false,
            true to true
        )

        for ((f, fitm) in inputs) {
            newElement()
                .focusable(f, fitm)
                .renderView {
                    verify(this).isFocusable = eq(f)
                    verify(this).isFocusableInTouchMode = eq(fitm)
                }
        }
    }

    @Test
    fun clipToOutline() {
        val input = true
        newElement()
            .clipToOutline(input)
            .renderView {
                verify(this).clipToOutline = eq(input)
            }
    }

    @Test
    fun `onViewPreDraw - vto is not alive`() {
        val vto = mock(ViewTreeObserver::class.java)
        `when`(vto.isAlive).thenReturn(false)

        val flag = AtomicBoolean()

        newElement()
            .also {
                `when`(it.view.viewTreeObserver).thenReturn(vto)
            }
            .onViewPreDraw { flag.set(true) }
            .renderView {
                verify(
                    vto,
                    never()
                ).addOnPreDrawListener(any(ViewTreeObserver.OnPreDrawListener::class.java))
                Assert.assertFalse(flag.get())
            }
    }

    @Test
    fun onViewPreDraw() {
        val vto = mock(ViewTreeObserver::class.java)
        `when`(vto.isAlive).thenReturn(true)

        val flag = AtomicBoolean()
        val captor = ArgumentCaptor.forClass(ViewTreeObserver.OnPreDrawListener::class.java)

        newElement()
            .also {
                `when`(it.view.viewTreeObserver).thenReturn(vto)
            }
            .onViewPreDraw { flag.set(true) }
            .renderView {
                verify(vto).addOnPreDrawListener(captor.capture())

                val value = captor.value
                Assert.assertNotNull(value)

                val result = value.onPreDraw()
                // listener should not block drawing
                Assert.assertTrue(result)
                // our supplied callback should be triggered
                Assert.assertTrue(flag.get())

                // listener must be unregistered
                verify(vto).removeOnPreDrawListener(eq(value))
            }
    }

    @Test
    fun onViewAttachedStateChanged() {
        class Ref(
            var view: View? = null,
            var attached: Boolean? = null,
        )

        lateinit var ref: Ref

        newElement()
            .onViewAttachedStateChanged { view, attached ->
                ref = Ref(view, attached)
            }
            .renderView {
                val captor = ArgumentCaptor.forClass(View.OnAttachStateChangeListener::class.java)
                verify(this).addOnAttachStateChangeListener(captor.capture())

                val value = captor.value
                Assert.assertNotNull(value)

                value.onViewAttachedToWindow(this)
                Assert.assertEquals(this, ref.view)
                Assert.assertEquals(true, ref.attached)

                val refId = System.identityHashCode(ref)

                value.onViewDetachedFromWindow(this)

                Assert.assertNotEquals(refId, System.identityHashCode(ref))
                Assert.assertEquals(this, ref.view)
                Assert.assertEquals(false, ref.attached)
            }
    }

    @Test
    fun ifAvailable() {
        val sdk = Build.VERSION_CODES.M
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, "SDK_INT", sdk)
        Assert.assertEquals(sdk, Build.VERSION.SDK_INT)

        val inputs = listOf(
            22 to true, // represents value less than device has (available)
            23 to true, // represents the same value as device (available)
            24 to false // represents value above device sdk int (not available)
        )

        for ((version, available) in inputs) {
            val flag = AtomicBoolean()
            newElement()
                // run immediately, do not wait for view
                .ifAvailable(version) {
                    flag.set(true)
                }

            Assert.assertEquals(version.toString(), available, flag.get())
        }
    }

    @Test
    fun onElementView() {
        val callbacks: (ViewElement<View, LayoutParams>) -> Unit = mockt()
        val element = newElement()
            .onElementView(callbacks)
        verify(callbacks, org.mockito.kotlin.never()).invoke(org.mockito.kotlin.any())
        element.render()
        verify(callbacks, times(1)).invoke(org.mockito.kotlin.eq(element))
    }

    @Test
    fun onViewLayout() {
        val callbacks: (View, Int, Int) -> Unit = mockt()
        val element = newElement()
            .onViewLayout(callbacks)
        verify(callbacks, org.mockito.kotlin.never()).invoke(
            org.mockito.kotlin.any(),
            anyInt(),
            anyInt()
        )
        element.render()
        val captor = argumentCaptor<View.OnLayoutChangeListener>()
        verify(element.view).addOnLayoutChangeListener(captor.capture())

        verify(callbacks, org.mockito.kotlin.never()).invoke(
            org.mockito.kotlin.any(),
            anyInt(),
            anyInt()
        )

        captor.value.onLayoutChange(element.view, 10, 20, 100, 50, 0, 0, 0, 0)
        verify(callbacks).invoke(
            org.mockito.kotlin.eq(element.view),
            org.mockito.kotlin.eq(100 - 10),
            org.mockito.kotlin.eq(50 - 20),
        )
    }

    private fun <T> mode(value: T?): VerificationMode = if (value == null) never() else times(1)

    private fun value(value: Int?): Int = if (value == null) anyInt() else eq(value)
    private fun value(value: Float?): Float = if (value == null) anyFloat() else eq(value)
}