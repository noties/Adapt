package io.noties.adapt.sample.ui.element

import io.noties.adapt.sample.R
import io.noties.adapt.sample.ui.dimen.appBarHeight
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.ColorsBuilder
import io.noties.adapt.ui.app.dimen.Dimens
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.imageScaleType
import io.noties.adapt.ui.element.imageTint
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.padding

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.SampleAppBar(
    tint: ColorsBuilder? = null
) = ZStack {
    Image(R.drawable.adapt_logo)
        .layoutFill()
        .imageScaleType { centerInside }
        .padding(top = 14, bottom = 6)
        .let {
            if (tint != null) {
                it.imageTint(tint)
            } else {
                it
            }
        }
}.indent()
    .layout(fill, Dimens.appBarHeight)
//    .backgroundColor { backgroundSecondary }