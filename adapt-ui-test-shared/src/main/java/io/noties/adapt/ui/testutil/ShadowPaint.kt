package io.noties.adapt.ui.testutil

import android.graphics.Color
import android.graphics.Paint
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowPaint

// Robolectric default implementation does not update internal alpha when color is set
@Implements(Paint::class)
class ShadowPaint : ShadowPaint() {

    @Implementation
    public override fun setColor(color: Int) {
        super.setColor(color)
        super.setAlpha(Color.alpha(color))
    }

    @Implementation
    public override fun setAlpha(alpha: Int) {
        super.setAlpha(alpha)

        // if setAlpha is called, update inner color
        color = Color.argb(
            alpha,
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    public override fun getColor(): Int {
        return super.getColor()
    }

    public override fun getAlpha(): Int {
        return super.getAlpha()
    }
}