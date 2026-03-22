package io.noties.adapt.sample.explore

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.ColorsBuilder
import io.noties.adapt.ui.element.Text

object ExploreTextFontWeight {

    @JvmInline
    value class TextWeight(val rawValue: Int) {
        companion object {
            // also _normal_ in android terms
            val regular get() = TextWeight(400)
            val medium get() = TextWeight(500)

            fun raw(value: Int) = TextWeight(value)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textWeight(
        weight: TextWeight.Companion.() -> TextWeight
    ) = onView {
        // weight should
        val oldTypeface = it.typeface
        val newTypeface = Typeface.create(
            oldTypeface,
            weight(TextWeight).rawValue,
            true == oldTypeface?.isItalic
        )
        it.typeface = newTypeface
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun hey(context: Context) = ViewFactory.createView(context) {
        Text("")
            .textWeight { medium }
    }

//    inline fun color(builder: ColorsBuilder) {
//        val color = builder()
//    }
}