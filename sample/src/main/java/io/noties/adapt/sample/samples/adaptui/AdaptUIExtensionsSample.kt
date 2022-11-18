package io.noties.adapt.sample.samples.adaptui

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.TextStyles.heyHey
import io.noties.adapt.sample.samples.adaptui.TextStyles.heyHo
import io.noties.adapt.sample.samples.adaptui.TextStyles.textStylePrimary
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutWeight
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20221016113609",
    title = "AdaptUI - extensions to style or customize elements",
    tags = ["adapt-ui"]
)
class AdaptUIExtensionsSample : SampleView() {

    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {
        ViewFactory.addChildren(view as ViewGroup) {
            VScroll {
                VStack {

                    Text()
                        .layout(FILL, WRAP)
                        .textStylePrimary()
                        .heyHey() // as it does not return ViewElement is not possible to further customize

                    Text("heyHo")
                        .heyHo() // after this no longer ViewElement<TextView, LinearLayout.LayoutParams>
                        // only generic functions are available to all views and all layout params
                        .background(Colors.orange)
                }
            }
        }
    }
}

object TextStyles {

    // becomes available to all subclasses of TextView with any LayoutParams
    fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textStylePrimary() = this
        .textSize(16)
        .textColor(Colors.primary)
        .textGravity(Gravity.top)
        .textFont(fontStyle = Typeface.BOLD)

    // becomes available to only TextView elements (no subclasses would be allowed,
    //  no AppCompatTextView, EditText, no Button, no CheckBox, etc)
    fun <LP : LayoutParams> ViewElement<TextView, LP>.textStyleSecondary() = this
        .textSize(17)
        .textColor(Colors.primary)

    // becomes available to only TextView inside a LinearLayout
    fun <LP : LinearLayout.LayoutParams> ViewElement<TextView, LP>.textStyleAdditional() = this
        .textColor(Colors.primary)
        .textGravity(Gravity.center)
        .layoutWeight(1F)

    // LP in most cases could be omitted if further customization is not required
    // Extension also does not need to return ViewElement, but in this case
    //  no further calls for customization would be available
    fun ViewElement<*, *>.heyHey() {
        background(Colors.primary)
        padding(48)
    }

    /*
    VStack {
      Text()
        .heyHey() // it is called here, but does not return anything, so nothing can called after it
    }
     */

    // it is possible to return this, but this would have types erased and thus
    //  type information missing
    fun ViewElement<*, *>.heyHo() = this
        .background(Colors.accent)
}
