package io.noties.adapt.ui.element

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.obtainView
import io.noties.adapt.ui.renderView
import io.noties.adapt.ui.testutil.mockt
import io.noties.adapt.ui.testutil.value
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.ImeOptions
import io.noties.adapt.ui.util.InputType
import io.noties.adapt.ui.util.TypefaceStyle
import io.noties.adapt.ui.util.dip
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.RETURNS_MOCKS
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.whenever
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
    fun `textTypeface - default`() {
        newTextElement()
            .textTypeface()
            .renderView {
                verify(this).setTypeface(eq(null), eq(Typeface.NORMAL))
            }
    }

    @Test
    fun textTypeface() {
        val inputs = listOf(
            null to TypefaceStyle.normal,
            Typeface.MONOSPACE to TypefaceStyle.bold.italic
        )
        for ((typeface, style) in inputs) {
            newTextElement()
                .textTypeface(typeface, style)
                .renderView {
                    verify(this).setTypeface(
                        eq(typeface),
                        eq(style.value)
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
    fun textHintColor() {
        val input = 8912
        newTextElement()
            .textHintColor(input)
            .renderView {
                verify(this).setHintTextColor(eq(input))
            }
    }

    @Test
    fun `textHintColor - colorStateList`() {
        val input = ColorStateList.valueOf(21314)
        newTextElement()
            .textHintColor(input)
            .renderView {
                verify(this).setHintTextColor(eq(input))
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
    fun `textHyphenationFrequency - values`() {
        val inputs = listOf(
            Layout.HYPHENATION_FREQUENCY_NONE to HyphenationFrequency.none,
            Layout.HYPHENATION_FREQUENCY_NORMAL to HyphenationFrequency.normal,
            Layout.HYPHENATION_FREQUENCY_FULL to HyphenationFrequency.full
        )
        for ((expected, input) in inputs) {
            assertEquals(expected, input.value)
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
    fun `textBreakStrategy - values`() {
        val inputs = listOf(
            Layout.BREAK_STRATEGY_SIMPLE to BreakStrategy.simple,
            Layout.BREAK_STRATEGY_BALANCED to BreakStrategy.balanced,
            Layout.BREAK_STRATEGY_HIGH_QUALITY to BreakStrategy.highQuality
        )
        for ((expected, input) in inputs) {
            assertEquals(expected, input.value)
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
    fun `textJustificationMode - values`() {
        val inputs = listOf(
            Layout.JUSTIFICATION_MODE_NONE to JustificationMode.none,
            Layout.JUSTIFICATION_MODE_INTER_WORD to JustificationMode.interWord
        )

        for ((expected, input) in inputs) {
            assertEquals(expected, input.value)
        }
    }

    @Test
    fun textJustificationMode() {
        val inputs = listOf(
            JustificationMode.none,
            JustificationMode.interWord,
            JustificationMode(-2813)
        )

        for (input in inputs) {
            newTextElement()
                .textJustificationMode(input)
                .renderView {
                    verify(this).justificationMode = eq(input.value)
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

    @Test
    fun textGradient() {
        val gradient = mockt<Gradient>()
        val paint = mockt<TextPaint>()

        newTextElement()
            .textGradient(gradient)
            .also {
                whenever(it.view.paint).thenReturn(paint)
            }
            .renderView {
                // at this point nothing happens, as view has no dimensions
                verify(gradient, never()).createShader(any())

                val captor = argumentCaptor<View.OnLayoutChangeListener>()
                verify(this).addOnLayoutChangeListener(captor.capture())

                whenever(this.paddingLeft).thenReturn(1)
                whenever(this.paddingTop).thenReturn(2)
                whenever(this.paddingRight).thenReturn(3)
                whenever(this.paddingBottom).thenReturn(4)

                whenever(this.width).thenReturn(50)
                whenever(this.height).thenReturn(60)

                val listener = captor.value
                listener.onLayoutChange(this, 0, 0, 0, 0, 0, 0, 0, 0)

                val boundsCaptor = argumentCaptor<Rect>()
                verify(gradient).createShader(boundsCaptor.capture())
                assertEquals(
                    Rect(0, 0, 50, 60).also {
                        it.left += paddingLeft
                        it.top += paddingTop
                        it.right -= paddingRight
                        it.bottom -= paddingBottom
                    },
                    boundsCaptor.value
                )
            }
    }

    @Test
    fun textShadow() {
        class Input(
            val radius: Int,
            val color: Int?,
            val dx: Int?,
            val dy: Int?
        )

        val inputs = listOf(
            Input(1, null, null, null),
            Input(2, 3, null, null),
            Input(4, 5, 6, null),
            Input(7, 8, 9, 10)
        )

        for (input in inputs) {
            newTextElement()
                .textShadow(input.radius, input.color, input.dx, input.dy)
                .renderView {
                    val xy = argumentCaptor<Float>()
                    val c = argumentCaptor<Int>()

                    // fails with InvalidUseOfMatchersException if eq is used..
                    verify(this).setShadowLayer(
                        eq(input.radius.dip.toFloat()),
                        xy.capture(),
                        xy.capture(),
                        c.capture(),
                    )
                    assertEquals(
                        listOf(input.dx ?: 0, input.dy ?: 0).map { it.dip.toFloat() },
                        xy.allValues
                    )
                    assertEquals(
                        input.color ?: currentHintTextColor,
                        c.value
                    )
                }
        }
    }

    @Test
    fun textBold() {
        val inputs = listOf(
            null to null,
            mockt<Typeface>() to Typeface.ITALIC
        )

        for ((typeface, style) in inputs) {
            newTextElement()
                .also {
                    if (typeface != null) {
                        whenever(typeface.style).thenReturn(style)
                    }
                    whenever(it.view.typeface).thenReturn(typeface)
                }
                .textBold()
                .renderView {
                    val expectedStyle = (style ?: 0) or Typeface.BOLD
                    verify(this).setTypeface(
                        eq(typeface),
                        eq(expectedStyle)
                    )
                }
        }
    }

    @Test
    fun textItalic() {
        val inputs = listOf(
            null to null,
            mockt<Typeface>() to Typeface.BOLD
        )

        for ((typeface, style) in inputs) {
            newTextElement()
                .also {
                    if (typeface != null) {
                        whenever(typeface.style).thenReturn(style)
                    }
                    whenever(it.view.typeface).thenReturn(typeface)
                }
                .textItalic()
                .renderView {
                    val expectedStyle = (style ?: 0) or Typeface.ITALIC
                    verify(this).setTypeface(
                        eq(typeface),
                        eq(expectedStyle)
                    )
                }
        }
    }

    @Test
    fun textUnderline() {
        val inputs = listOf(true, false)
        for (input in inputs) {
            val paint = mockt<TextPaint>()
            newTextElement()
                .also {
                    whenever(it.view.paint).thenReturn(paint)
                }
                .textUnderline(input)
                .renderView {
                    verify(paint).isUnderlineText = eq(input)
                }
        }
    }

    @Test
    fun textStrikeThrough() {
        val inputs = listOf(true, false)
        for (input in inputs) {
            val paint = mockt<TextPaint>()
            newTextElement()
                .also {
                    whenever(it.view.paint).thenReturn(paint)
                }
                .textStrikeThrough(input)
                .renderView {
                    verify(paint).isStrikeThruText = eq(input)
                }
        }
    }

    @Test
    fun textSelectable() {
        val inputs = listOf(true, false)
        for (input in inputs) {
            newTextElement()
                .textSelectable(input)
                .renderView {
                    verify(this).setTextIsSelectable(eq(input))
                }
        }
    }

    @Test
    fun textLetterSpacing() {
        val inputs = listOf(
            0F,
            0.25F,
            -0.5F
        )

        for (em in inputs) {
            newTextElement()
                .textLetterSpacing(em)
                .renderView {
                    verify(this).letterSpacing = eq(em)
                }
        }
    }

    @Test
    fun textInputType() {
        val inputs = listOf(
            InputType.none,
            InputType.text.capWords.uri.multiline.autoCorrect,
            InputType.phone,
            InputType.number.signed.password,
            InputType.dateTime.time
        )

        for (input in inputs) {
            newTextElement()
                .textInputType(input)
                .renderView {
                    verify(this).inputType = eq(input.value)
                }
        }
    }

    @Test
    fun textImeOptions() {
        val inputs = listOf(
            ImeOptions.none,
            ImeOptions.actionDone.noEnterAction.noExactUi.forceAscii,
            ImeOptions.navigatePrevious
        )

        for (input in inputs) {
            newTextElement()
                .textImeOptions(input)
                .renderView {
                    verify(this).imeOptions = eq(input.value)
                }
        }
    }

    @Test
    fun `textImeOptions - action`() {
        // none triggers all
        // action would be delivered if it is requested

        fun mockAction(): (TextView, ImeOptions) -> Boolean {
            val action: (TextView, ImeOptions) -> Boolean = mockt()
            whenever(action.invoke(any(), any())).thenReturn(true)
            return action
        }

        val inputs = listOf(
            ImeOptions.none to mockAction(),
            ImeOptions.actionDone to mockAction(),
            ImeOptions.actionNext.forceAscii.noExactUi to mockAction()
        )

        for ((options, action) in inputs) {
            newTextElement()
                .textImeOptions(options, action)
                .renderView {

                    val listener = kotlin.run {
                        val captor = ArgumentCaptor.forClass(OnEditorActionListener::class.java)
                        verify(this).setOnEditorActionListener(captor.capture())
                        captor.value
                    }

                    val expectedActionId = options.value and EditorInfo.IME_MASK_ACTION
                    if (expectedActionId == 0) {
                        // all events must be delivered
                        listOf(
                            ImeOptions.actionSearch,
                            ImeOptions.actionDone,
                            ImeOptions.actionSend
                        ).forEach {
                            assertEquals(
                                options.toString(),
                                true,
                                listener.onEditorAction(this, it.value, null)
                            )
                            verify(action).invoke(eq(this), eq(ImeOptions(it.value)))
                        }
                    } else {
                        // just our action must be delivered
                        listOf(
                            ImeOptions.none,
                            ImeOptions.actionDone,
                            ImeOptions.actionSend,
                            ImeOptions.actionSearch,
                            ImeOptions.actionNext,
                            ImeOptions.actionGo,
                            ImeOptions.actionPrevious,
                            ImeOptions.actionUnspecified,
                        )
                            .filter {
                                it.value != expectedActionId
                            }
                            .forEach {
                                assertEquals(
                                    options.toString(),
                                    false,
                                    listener.onEditorAction(this, it.value, null)
                                )
                                verify(action, never()).invoke(any(), any())
                            }

                        listener.onEditorAction(this, expectedActionId, null)
                        verify(action).invoke(eq(this), eq(ImeOptions(expectedActionId)))
                    }
                }
        }
    }

    private fun newTextElement() = newElementOfType<TextView>()
}