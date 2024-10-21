package io.noties.adapt.sample.explore

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.AdaptUIPreviewLayout
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.util.withAlphaComponent

object ExploreShapeStroke {

}

private class PreviewStroke(context: Context, attrs: AttributeSet?) :
    AdaptUIPreviewLayout(context, attrs) {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {
            Text("This is text")
                .textGravity { center }
                .padding(8)
                .background {
                    Capsule {
                        Capsule {
                            padding(2)
                            stroke(Colors.black, 2)
                            fill { hex("#ff0") }
                        }
                    }
                }
                .foregroundDefaultSelectable()
                .also { el ->
                    var isClip = false
                    el.onClick {
                        isClip = !isClip
                        el.clipToOutline(isClip).render()
                    }
                }
//                .clipToOutline()
        }.indent()
            .padding(16)
            .backgroundColor { white }
    }
}