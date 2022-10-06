package io.noties.adapt.ui

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import io.noties.adapt.ui.util.dip
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// qualifier is used, so resources would indicate a density greater than 1
//  so we can validate the MATCH_PARENT and WRAP_CONTENT are kept as-is
@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK], qualifiers = "xxxhdpi")
class ViewElement_Layout_Test {

    @Test
    fun layout() {

        // assert we have a different density, so we can validate that MATCH_PARENT or WRAP_CONTENT
        //  are kept as-is, but other values are converted to dip
        Assert.assertNotEquals(1, 1.dip)

        // special FILL and WRAP are kept as-is, over values are converted to dip
        val inputs = listOf(
            (MATCH_PARENT to MATCH_PARENT) to (MATCH_PARENT to MATCH_PARENT),
            (WRAP_CONTENT to WRAP_CONTENT) to (WRAP_CONTENT to WRAP_CONTENT),
            (1 to 2) to (1.dip to 2.dip),
            (MATCH_PARENT to 3) to (MATCH_PARENT to 3.dip),
            (4 to WRAP_CONTENT) to (4.dip to WRAP_CONTENT)
        )

        for ((call, actual) in inputs) {
            newElement()
                .mockLayoutParams()
                .layout(call.first, call.second)
                .renderView {
                    Assert.assertEquals(actual.first, layoutParams.width)
                    Assert.assertEquals(actual.second, layoutParams.height)
                }
        }
    }

    @Test
    fun layoutFill() {
        newElement()
            .mockLayoutParams()
            .layoutFill()
            .renderView {
                Assert.assertEquals(MATCH_PARENT, layoutParams.width)
                Assert.assertEquals(MATCH_PARENT, layoutParams.height)
            }
    }

    @Test
    fun layoutWeight() {
        newElementOfTypeLayout<View, LinearLayout.LayoutParams>()
            .mockLayoutParams(LinearLayout.LayoutParams(0, 0))
            .layoutWeight(43F)
            .renderView {
                Assert.assertEquals(43F, (layoutParams as LinearLayout.LayoutParams).weight)
            }
    }

    @Test
    fun `layoutGravity - linear`() {
        val input = Gravity.BOTTOM or Gravity.END
        newElementOfTypeLayout<View, LinearLayout.LayoutParams>()
            .mockLayoutParams(LinearLayout.LayoutParams(0, 0))
            .layoutGravity(input)
            .renderView {
                Assert.assertEquals(input, (layoutParams as LinearLayout.LayoutParams).gravity)
            }
    }

    @Test
    fun `layoutGravity - frame`() {
        val input = Gravity.BOTTOM or Gravity.END
        newElementOfTypeLayout<View, FrameLayout.LayoutParams>()
            .mockLayoutParams(FrameLayout.LayoutParams(0, 0))
            .layoutGravity(input)
            .renderView {
                Assert.assertEquals(input, (layoutParams as FrameLayout.LayoutParams).gravity)
            }
    }

    @Test
    fun `layoutMargin - all`() {
        val input = 76
        newElementOfTypeLayout<View, ViewGroup.MarginLayoutParams>()
            .mockLayoutParams(ViewGroup.MarginLayoutParams(0, 0))
            .layoutMargin(input)
            .renderView {
                val lp = layoutParams as ViewGroup.MarginLayoutParams
                Assert.assertEquals(input.dip, lp.marginStart)
                Assert.assertEquals(input.dip, lp.topMargin)
                Assert.assertEquals(input.dip, lp.marginEnd)
                Assert.assertEquals(input.dip, lp.bottomMargin)
            }
    }

    @Test
    fun `layoutMargin - hv`() {
        val inputs = listOf(
            null to null,
            1 to null,
            null to 2,
            3 to 4
        )
        for ((h, v) in inputs) {
            newElementOfTypeLayout<View, ViewGroup.MarginLayoutParams>()
                .mockLayoutParams(ViewGroup.MarginLayoutParams(0, 0))
                .layoutMargin(h, v)
                .renderView {
                    val lp = layoutParams as ViewGroup.MarginLayoutParams
                    Assert.assertEquals(h?.dip ?: 0, lp.marginStart)
                    Assert.assertEquals(v?.dip ?: 0, lp.topMargin)
                    Assert.assertEquals(h?.dip ?: 0, lp.marginEnd)
                    Assert.assertEquals(v?.dip ?: 0, lp.bottomMargin)
                }
        }
    }

    @Test
    fun `layoutMargin - individual`() {
        class Margin(
            val start: Int? = null,
            val top: Int? = null,
            val end: Int? = null,
            val bottom: Int? = null
        )

        val inputs = listOf(
            Margin(),
            Margin(1),
            Margin(top = 2),
            Margin(end = 3),
            Margin(bottom = 4),
            Margin(5, 6, 7, 8)
        )

        for (input in inputs) {
            newElementOfTypeLayout<View, ViewGroup.MarginLayoutParams>()
                .mockLayoutParams(ViewGroup.MarginLayoutParams(0, 0))
                .layoutMargin(input.start, input.top, input.end, input.bottom)
                .renderView {
                    val lp = layoutParams as ViewGroup.MarginLayoutParams
                    Assert.assertEquals(input.start?.dip ?: 0, lp.marginStart)
                    Assert.assertEquals(input.top?.dip ?: 0, lp.topMargin)
                    Assert.assertEquals(input.end?.dip ?: 0, lp.marginEnd)
                    Assert.assertEquals(input.bottom?.dip ?: 0, lp.bottomMargin)
                }
        }
    }

    private fun <V : View> ViewElement<V, LayoutParams>.mockLayoutParams() = mockLayoutParams(
        LayoutParams(0, 0)
    )

    private fun <V : View, LP : LayoutParams> ViewElement<V, LP>.mockLayoutParams(params: LP) =
        this.also {
            Mockito.`when`(view.layoutParams).thenReturn(params)
        }
}