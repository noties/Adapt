package io.noties.adapt.ui.flex

import android.graphics.drawable.Drawable
import com.google.android.flexbox.FlexboxLayout
import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.renderView
import io.noties.adapt.ui.util.dip
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.google.android.flexbox.AlignContent as _AlignContent
import com.google.android.flexbox.AlignItems as _AlignItems
import com.google.android.flexbox.FlexDirection as _FlexDirection
import com.google.android.flexbox.FlexWrap as _FlexWrap
import com.google.android.flexbox.JustifyContent as _JustifyContent

@Suppress("ClassName", "TestFunctionName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Flex_Test {

    @Test
    fun `flexDirection - values`() {
        class Input(
            val name: String,
            val flexDirection: FlexDirection,
            @_FlexDirection val value: Int
        )

        val inputs = listOf(
            Input("row", FlexDirection.row, _FlexDirection.ROW),
            Input("column", FlexDirection.column, _FlexDirection.COLUMN),
            Input("row.reverse", FlexDirection.row.reverse, _FlexDirection.ROW_REVERSE),
            Input("column.reverse", FlexDirection.column.reverse, _FlexDirection.COLUMN_REVERSE),
        )

        for (input in inputs) {
            Assert.assertEquals(
                input.name,
                input.flexDirection.value,
                input.value
            )
        }
    }

    @Test
    fun `flexDirection - unknown`() {
        Assert.assertEquals(
            _FlexDirection.ROW_REVERSE,
            FlexDirection(777).value
        )
    }

    @Test
    fun `flexDirection - element`() {
        val inputs = listOf(
            FlexDirection.row,
            FlexDirection.column,
            FlexDirection.row.reverse,
            FlexDirection.column.reverse,
            FlexDirection(999)
        )
        for (input in inputs) {
            newElementOfType<FlexboxLayout>()
                .flexDirection(input)
                .renderView {
                    verify(this).flexDirection = eq(input.value)
                }
        }
    }

    @Test
    fun `flexWrap - values`() {
        val inputs = listOf(
            FlexWrap.Companion::wrap to _FlexWrap.WRAP,
            FlexWrap.Companion::nowrap to _FlexWrap.NOWRAP,
            FlexWrap.Companion::wrapReverse to _FlexWrap.WRAP_REVERSE
        )

        for ((flexWrap, value) in inputs) {
            Assert.assertEquals(
                flexWrap.name,
                flexWrap.get().value,
                value
            )
        }
    }

    @Test
    fun `flexWrap - element`() {
        val inputs = listOf(
            FlexWrap.nowrap,
            FlexWrap.wrap,
            FlexWrap.wrapReverse,
            FlexWrap(876)
        )

        for (input in inputs) {
            newElementOfType<FlexboxLayout>()
                .flexWrap(input)
                .renderView {
                    verify(this).flexWrap = eq(input.value)
                }
        }
    }

    @Test
    fun `flexJustifyContent - values`() {
        val inputs = listOf(
            JustifyContent.Companion::flexStart to _JustifyContent.FLEX_START,
            JustifyContent.Companion::flexEnd to _JustifyContent.FLEX_END,
            JustifyContent.Companion::center to _JustifyContent.CENTER,
            JustifyContent.Companion::spaceBetween to _JustifyContent.SPACE_BETWEEN,
            JustifyContent.Companion::spaceAround to _JustifyContent.SPACE_AROUND,
            JustifyContent.Companion::spaceEvenly to _JustifyContent.SPACE_EVENLY
        )

        for ((prop, value) in inputs) {
            Assert.assertEquals(
                prop.name,
                prop.get().value,
                value
            )
        }
    }

    @Test
    fun `flexJustifyContent - element`() {
        val inputs = listOf(
            JustifyContent.flexStart,
            JustifyContent.flexEnd,
            JustifyContent.center,
            JustifyContent.spaceBetween,
            JustifyContent.spaceAround,
            JustifyContent.spaceEvenly,
            JustifyContent(874)
        )

        for (input in inputs) {
            newElementOfType<FlexboxLayout>()
                .flexJustifyContent(input)
                .renderView {
                    verify(this).justifyContent = eq(input.value)
                }
        }
    }

    @Test
    fun `flexAlignItems - values`() {
        val inputs = listOf(
            AlignItems.Companion::flexStart to _AlignItems.FLEX_START,
            AlignItems.Companion::flexEnd to _AlignItems.FLEX_END,
            AlignItems.Companion::center to _AlignItems.CENTER,
            AlignItems.Companion::baseline to _AlignItems.BASELINE,
            AlignItems.Companion::stretch to _AlignItems.STRETCH,
        )

        for ((prop, value) in inputs) {
            Assert.assertEquals(
                prop.name,
                prop.get().value,
                value
            )
        }
    }

    @Test
    fun `flexAlignItems - element`() {
        val inputs = listOf(
            AlignItems.flexStart,
            AlignItems.flexEnd,
            AlignItems.center,
            AlignItems.baseline,
            AlignItems.stretch,
            AlignItems(1252)
        )

        for (input in inputs) {
            newElementOfType<FlexboxLayout>()
                .flexAlignItems(input)
                .renderView {
                    verify(this).alignItems = eq(input.value)
                }
        }
    }

    @Test
    fun `flexAlignContent - values`() {
        val inputs = listOf(
            AlignContent.Companion::flexStart to _AlignContent.FLEX_START,
            AlignContent.Companion::flexEnd to _AlignContent.FLEX_END,
            AlignContent.Companion::center to _AlignContent.CENTER,
            AlignContent.Companion::spaceBetween to _AlignContent.SPACE_BETWEEN,
            AlignContent.Companion::spaceAround to _AlignContent.SPACE_AROUND,
            AlignContent.Companion::stretch to _AlignContent.STRETCH,
        )

        for ((prop, value) in inputs) {
            Assert.assertEquals(
                prop.name,
                prop.get().value,
                value
            )
        }
    }

    @Test
    fun `flexAlignContent - element`() {
        val inputs = listOf(
            AlignContent.flexStart,
            AlignContent.flexEnd,
            AlignContent.center,
            AlignContent.spaceBetween,
            AlignContent.spaceAround,
            AlignContent.stretch,
            AlignContent(12821)
        )

        for (input in inputs) {
            newElementOfType<FlexboxLayout>()
                .flexAlignContent(input)
                .renderView {
                    verify(this).alignContent = eq(input.value)
                }
        }
    }

    @Test
    fun flexGap() {
        val gap = 12

        // verify density in test
        Assert.assertEquals(12, 12.dip)

        newElementOfType<FlexboxLayout>()
            .flexGap(gap)
            .renderView {
                val captor = ArgumentCaptor.forClass(Drawable::class.java)
                verify(this).setDividerDrawable(captor.capture())
                val drawable = captor.value
                Assert.assertEquals(gap, drawable.intrinsicWidth)
                Assert.assertEquals(gap, drawable.intrinsicHeight)

                verify(this).setShowDivider(eq(FlexboxLayout.SHOW_DIVIDER_MIDDLE))
            }
    }
}