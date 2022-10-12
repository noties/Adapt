package io.noties.adapt.ui.element

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.createView
import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.obtainView
import io.noties.adapt.ui.renderView
import io.noties.adapt.ui.util.Gravity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.RETURNS_MOCKS
import org.mockito.Mockito.`when`
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.math.roundToInt

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Text_Test {

    private val context: Context get() = RuntimeEnvironment.getApplication()

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun factory() {
        val mocked = mock(TextView::class.java, RETURNS_MOCKS)
        ElementViewFactory.Text = { mocked }
        assertEquals(mocked, obtainView { Text() })
    }

    @Test
    fun init() {
        val inputs = listOf(
            null,
            "a string",
            SpannableString("hello span").also {
                it.setSpan(ForegroundColorSpan(0), 0, it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        )

        for (input in inputs) {
            val view = ViewFactory.createView(context) {
                Text(input)
            }
            assertEquals(TextView::class.java, view::class.java)
            assertEquals(
                input.toString(),
                input ?: "",
                (view as TextView).text
            )
        }
    }

    @Test
    fun textSize() {
        val input = 56
        newTextElement()
            .textSize(input)
            .renderView {
                verify(this).setTextSize(
                    eq(TypedValue.COMPLEX_UNIT_SP),
                    eq(input.toFloat())
                )
            }
    }

    @Test
    fun `textColor - color`() {
        val input = 0xFF001122.toInt()
        newTextElement()
            .textColor(input)
            .renderView {
                verify(this).setTextColor(eq(input))
            }
    }

    @Test
    fun `textColor - colorStateList`() {
        val input = ColorStateList.valueOf(98765)
        newTextElement()
            .textColor(input)
            .renderView {
                verify(this).setTextColor(eq(input))
            }
    }

    @Test
    fun textGravity() {
        val input = Gravity.bottom.trailing
        newTextElement()
            .textGravity(input)
            .renderView {
                verify(this).gravity = eq(input.value)
            }
    }

    @Test
    fun `textFont - default`() {
        newTextElement()
            .textFont()
            .renderView {
                verify(this).setTypeface(eq(null), eq(Typeface.NORMAL))
            }
    }

    @Test
    fun textFont() {
        val inputs = listOf(
            null to Typeface.NORMAL,
            Typeface.MONOSPACE to (Typeface.BOLD or Typeface.ITALIC)
        )
        for ((typeface, style) in inputs) {
            newTextElement()
                .textFont(typeface, style)
                .renderView {
                    verify(this).setTypeface(
                        eq(typeface),
                        eq(style)
                    )
                }
        }
    }

    @Test
    fun `textAllCaps - default`() {
        newTextElement()
            .textAllCaps()
            .renderView {
                verify(this).isAllCaps = eq(true)
            }
    }

    @Test
    fun textAllCaps() {
        val input = false
        newTextElement()
            .textAllCaps(input)
            .renderView {
                verify(this).isAllCaps = eq(input)
            }
    }

    @Test
    fun `textHideIfEmpty - default`() {
        newTextElement()
            .textHideIfEmpty()
            .renderView {
                val captor = ArgumentCaptor.forClass(TextWatcher::class.java)
                verify(this).addTextChangedListener(captor.capture())
                assertNotNull(captor.value)

                captor.value.afterTextChanged(null)
                verify(this).visibility = eq(View.GONE)
                captor.value.afterTextChanged(SpannableStringBuilder("not-empty"))
                verify(this).visibility = eq(View.VISIBLE)
            }
    }

    @Test
    fun textHideIfEmpty() {
        newTextElement()
            .textHideIfEmpty(true)
            .textHideIfEmpty(false)
            .renderView {
                // after watcher is removed, visibility is restored
                verify(this).visibility = eq(View.VISIBLE)
            }
    }

    @Test
    fun `text - charSequence`() {
        val input = SpannableStringBuilder("hello").also {
            it.setSpan(BackgroundColorSpan(1234), 1, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        newTextElement()
            .text(input)
            .renderView {
                verify(this).text = eq(input)
            }
    }

    @Test
    fun `text - resId`() {
        val input = 9876
        newTextElement()
            .text(input)
            .renderView {
                verify(this).setText(eq(input))
            }
    }

    @Test
    fun textHint() {
        val input = "HIIIYNFYT!"
        newTextElement()
            .textHint(input)
            .renderView {
                verify(this).hint = eq(input)
            }
    }

    @Test
    fun textEllipsize() {
        val inputs = TextUtils.TruncateAt.values()
        for (input in inputs) {
            newTextElement()
                .textEllipsize(input)
                .renderView {
                    verify(this).ellipsize = eq(input)
                }
        }
    }

    @Test
    fun textMaxLines() {
        val input = 99
        newTextElement()
            .textMaxLines(input)
            .renderView {
                verify(this).maxLines = eq(input)
            }
    }

    @Test
    fun `textSingleLine - default`() {
        newTextElement()
            .textSingleLine()
            .renderView {
                verify(this).isSingleLine = eq(true)
            }
    }

    @Test
    fun textSingleLine() {
        val input = false
        newTextElement()
            .textSingleLine(input)
            .renderView {
                verify(this).isSingleLine = eq(input)
            }
    }

    @Test
    fun textHyphenationFrequency() {
        val inputs = listOf(
            HyphenationFrequency.none,
            HyphenationFrequency.normal,
            HyphenationFrequency.full,
            HyphenationFrequency(-98765)
        )
        for (input in inputs) {
            newTextElement()
                .textHyphenationFrequency(input)
                .renderView {
                    verify(this).hyphenationFrequency = eq(input.value)
                }
        }
    }

    @Test
    fun textBreakStrategy() {
        val inputs = listOf(
            BreakStrategy.simple,
            BreakStrategy.balanced,
            BreakStrategy.highQuality,
            BreakStrategy(-91293921)
        )
        for (input in inputs) {
            newTextElement()
                .textBreakStrategy(input)
                .renderView {
                    verify(this).breakStrategy = eq(input.value)
                }
        }
    }

    @Test
    fun `textAutoSize - sizes`() {
        val input = intArrayOf(12, 14, 16, 18)
        newTextElement()
            .textAutoSize(input)
            .renderView {
                verify(this).setAutoSizeTextTypeUniformWithPresetSizes(
                    eq(input),
                    eq(TypedValue.COMPLEX_UNIT_SP)
                )
            }
    }

    @Test
    fun `textAutoSize - minMax`() {

        val inputs = listOf(
            Triple(8, null, null),
            Triple(7, 19, null),
            Triple(6, null, 3),
            Triple(5, 6, 2)
        )

        val scaledDensity = 4F

        for ((min, max, step) in inputs) {
            newTextElement()
                .textAutoSize(min, max, step)
                .also {
                    val resources = mock(Resources::class.java)
                    val displayMetrics = DisplayMetrics()
                    displayMetrics.scaledDensity = scaledDensity
                    `when`(resources.displayMetrics).thenReturn(displayMetrics)
                    `when`(it.view.resources).thenReturn(resources)
                }
                .renderView {
                    val maxValue = max ?: (textSize / scaledDensity).roundToInt()
                    val stepValue = step ?: 1
                    verify(this).setAutoSizeTextTypeUniformWithConfiguration(
                        eq(min),
                        eq(maxValue),
                        eq(stepValue),
                        eq(TypedValue.COMPLEX_UNIT_SP)
                    )
                }
        }
    }

    @Test
    fun textOnTextChanged() {

        var text: CharSequence? = null

        newTextElement()
            .textOnTextChanged {
                text = it
            }
            .renderView {
                val captor = ArgumentCaptor.forClass(TextWatcher::class.java)
                verify(this).addTextChangedListener(captor.capture())

                val value = captor.value
                assertNotNull(value)

                assertNull(text)

                val input = SpannableStringBuilder("NOT NULL")
                value.afterTextChanged(input)

                assertNotNull(text)
                assertEquals(input, text)
            }
    }

    private fun newTextElement() = newElementOfType<TextView>()
}