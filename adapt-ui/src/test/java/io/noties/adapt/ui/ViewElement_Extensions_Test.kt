package io.noties.adapt.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.ShapeDrawable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyFloat
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.verification.VerificationMode
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ViewElement_Extensions_Test {

//    @Before
//    fun before() {
//        // there is no way to set density explicitly?
//        Shadows.shadowOf(Resources.getSystem()).also {
//
//        }
//    }

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
    fun clipToPadding() {
        listOf(true, false)
            .forEach {
                newElementOfType<ViewGroup>()
                    .clipToPadding(it)
                    .renderView {
                        verify(this).clipToPadding = eq(it)
                    }
            }

    }

    @Test
    fun clipChildren() {
        listOf(true, false)
            .forEach {
                newElementOfType<ViewGroup>()
                    .clipChildren(it)
                    .renderView {
                        verify(this).clipChildren = eq(it)
                    }
            }
    }

    // a combination, creates 2 view blocks
    @Test
    fun noClip() {
        newElementOfType<ViewGroup>()
            .noClip()
            .renderView {
                verify(this).clipChildren = eq(false)
                verify(this).clipToPadding = eq(false)
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
                    verify(this, mode(x)).translationX = x?.toFloat()?.let { eq(it) } ?: anyFloat()
                    verify(this, mode(y)).translationY = y?.toFloat()?.let { eq(it) } ?: anyFloat()
                    verify(this, mode(z)).translationZ = z?.toFloat()?.let { eq(it) } ?: anyFloat()
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
        newElement()
            .padding(812)
            .renderView {
                verify(this).setPaddingRelative(
                    eq(812),
                    eq(812),
                    eq(812),
                    eq(812)
                )
            }
    }

    @Test
    fun `foreground - drawable`() {
        val inputs = listOf(
            null to null,
            mock(Drawable::class.java) to null,
            null to Gravity.CENTER,
            mock(Drawable::class.java) to Gravity.BOTTOM
        )

        for ((drawable, gravity) in inputs) {
            newElement()
                .foreground(drawable, gravity)
                .renderView {
                    verify(this).foreground = eq(drawable)
                    verify(this, mode(gravity)).foregroundGravity = value(gravity)
                }
        }
    }

    @Test
    fun `foreground - shape`() {
        val shape = mock(Shape::class.java)

        val inputs = listOf(
            shape to null,
            shape to Gravity.END
        )

        for ((s, gravity) in inputs) {
            newElement()
                .foreground(s, gravity)
                .renderView {
                    val captor = ArgumentCaptor.forClass(Drawable::class.java)
                    verify(this).foreground = captor.capture()
                    verify(this, mode(gravity)).foregroundGravity = value(gravity)

                    Assert.assertTrue(
                        "Foreground is instance of ShapeDrawable, class:${captor.value::class.java.name}",
                        captor.value is ShapeDrawable
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
                    captor.value is ShapeDrawable
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

    private fun mode(value: Int?): VerificationMode = if (value == null) never() else times(1)
    private fun value(value: Int?): Int = if (value == null) anyInt() else eq(value)
}