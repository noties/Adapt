package io.noties.adapt.ui.shape

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import io.noties.adapt.ui.testutil.mockt
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.toHexString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.roundToInt

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK], qualifiers = "xxhdpi")
class Asset_Test {

    @Test
    fun clone() {
        val drawable = mockt<Drawable>()
        val asset: Shape = AssetShape(drawable)
        val clone = asset.clone()
        assertEquals(AssetShape::class.java, clone::class.java)
        assertEquals(drawable, (clone as AssetShape).drawable)
    }

    @Test
    fun `clone - tries to create new drawable`() {
        val mockNewDrawable = mockt<Drawable> {
            on { mutate() } doReturn mock
        }
        val drawable = mockt<Drawable> {
            val cs = mockt<Drawable.ConstantState> {
                on { newDrawable() } doReturn mockNewDrawable
            }
            on { constantState } doReturn cs
        }
        val asset = AssetShape(drawable)
        val cloned = asset.clone()

        verify(drawable).constantState
        assertEquals(mockNewDrawable, cloned.drawable)
        verify(mockNewDrawable).mutate()
    }

    @Test
    fun init() {
        // defaultFillColor required to draw
        val asset = AssetShape(mockt())
        assertEquals(
            Shape.defaultFillColor.toHexString(),
            asset.fill?.color?.toHexString()
        )
    }

    @Test
    fun `init - intrinsic`() {

        val density = Resources.getSystem().displayMetrics.density

        // just to make sure calculation happen and not values are taken directly (as in density = 1)
        assertNotEquals(1F, density)

        val wpx = 100
        val hpx = 149

        // if drawable has intrinsic bounds it would be converted to dp and size set
        val drawable = mockt<Drawable> {
            on { intrinsicWidth } doReturn wpx
            on { intrinsicHeight } doReturn hpx
        }
        val asset = AssetShape(drawable)
        assertEquals(
            Shape.defaultFillColor.toHexString(),
            asset.fill?.color?.toHexString()
        )

        fun Int.toDp(): Int = (this / density).roundToInt()

        assertEquals(
            Dimension.Exact(wpx.toDp()),
            asset.width
        )
        assertEquals(
            Dimension.Exact(hpx.toDp()),
            asset.height
        )
    }

    @Test
    fun draw() {
        val drawable = mockt<Drawable>()
        val asset = AssetShape(drawable)
        val canvas = mockt<Canvas>()
        val rect = Rect(0, 0, 10, 88)
        val alpha = (255 * 0.721F).roundToInt()
        val paint = mockt<Paint> {
            on { this.alpha } doReturn alpha
        }
        asset.drawShape(
            canvas,
            rect,
            paint
        )
        verify(drawable).bounds = eq(rect)
        verify(drawable).alpha = eq(alpha)
        verify(drawable).draw(eq(canvas))
    }

    @Test
    fun `factory - tinted - color`() {
        val drawable = mockt<Drawable> {
            on { mutate() } doReturn mock
        }
        val color = 97271
        val asset = AssetShape(drawable) {
            tint(color)
        }
        assertEquals(drawable, asset.drawable)
        verify(drawable).mutate()
        verify(drawable).setTint(eq(color))
    }

    @Test
    fun `factory - tinted - colorStateList`() {
        val csl = mockt<ColorStateList>()
        val drawable = mockt<Drawable> {
            on { mutate() } doReturn mock
        }
        val asset = AssetShape(drawable) {
            tint(csl)
        }
        assertEquals(drawable, asset.drawable)
        verify(drawable).mutate()
        verify(drawable).setTintList(eq(csl))
    }

    @Test
    fun `init - block applied after`() {
        // verify that customization block is called after own init
        //  (asset sets intrinsic size automatically)

        val (w, h) = 2 to 3

        // NB! intrinsic size is represented by platform pixels
        val drawable = mockt<Drawable> {
            on { this.intrinsicWidth } doReturn w.dip
            on { this.intrinsicHeight } doReturn h.dip
        }

        val assetNoBlock = AssetShape(drawable)
        assertEquals(w, (assetNoBlock.width as Dimension.Exact).value)
        assertEquals(h, (assetNoBlock.height as Dimension.Exact).value)

        val asset = AssetShape(drawable) {
            size(4, 5)
        }
        assertEquals(4, (asset.width as Dimension.Exact).value)
        assertEquals(5, (asset.height as Dimension.Exact).value)
    }

    @Test
    fun newDrawable() {
        val drawable = mockt<Drawable> {
            whenever(mock.isStateful).thenReturn(true)
        }

        val asset = AssetShape(drawable)
        val shapeDrawable = asset.newDrawable()

        assertEquals(true, shapeDrawable.isStateful)

        val state = intArrayOf(65)
        shapeDrawable.state = state

        verify(drawable).state = eq(state)

        shapeDrawable.setHotspot(3F, 6F)
        verify(drawable).setHotspot(eq(3F), eq(6F))
    }
}