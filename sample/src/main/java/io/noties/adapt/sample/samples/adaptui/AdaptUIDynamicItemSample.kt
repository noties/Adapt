package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Item
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.UpdateItem
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.referenceUpdate
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.shape.RoundedRectangleShape
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.util.Gravity
import java.util.Date

@AdaptSample(
    id = "20230715105856",
    title = "[Explore] Dynamic Item",
    description = "Direct usage of an <em>Item</em> inside ViewFactory builder",
    tags = ["adapt-ui"]
)
class AdaptUIDynamicItemSample : AdaptUISampleView() {

    private lateinit var updateItem: UpdateItem<MyMutableItem>

    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            Text("Some text")
                .textSize(21)
                .textGravity(Gravity.center)
                .textBold()
                .padding(16)

            Item(MyMutableItem(1L, "D", "This is item in layout", Colors.orange))
                .referenceUpdate(::updateItem)
                // customize item view
                .padding(48)
                .layoutMargin(48)

            val (_, update) = Item(
                MyMutableItem(
                    2L,
                    "UP",
                    "This is item that we can update",
                    Colors.accent
                )
            ) {
                it.changeHandlerTransitionSelf()
            }

            Text("Some other text. Click me to update last item")
                .padding(16)
                .onClick {
                    update {
                        it.text = Date().toString()
                    }
                    updateItem {
                        it.text = Date().toString()
                    }
                }
        }.layoutFill()
            .noClip()
    }

    class MyMutableItem(
        val id: Long,
        var letter: String,
        var text: String,
        var color: Int
    ) : ElementItem<MyMutableItem.Ref>(id, { Ref() }) {
        class Ref {
            lateinit var letterView: TextView
            lateinit var textView: TextView
        }

        override fun bind(holder: Holder<Ref>) {
            with(holder.ref) {
                letterView.text = letter
                letterView.setBackgroundColor(color)
                textView.text = text
            }
        }

        override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
            val base = RoundedRectangleShape(12) {
                fill(Colors.white)
            }
            ZStack {
                // container that drops shadow
                ZStack {
                    // actual content that has clipToOutline, so content
                    //  does not expand beyond rounded bounds
                    HStack {
                        Text()
                            .reference(ref::letterView)
                            .layout(96, 96)
                            .textGravity(Gravity.center)
                            .textColor(Colors.white)
                            .textBold()
                            .textSize(24)
                        Text()
                            .reference(ref::textView)
                            .layoutGravity(Gravity.center.vertical)
                            .layoutMargin(leading = 8)
                            .textColor(Colors.black)
                            .textSize(17)
                    }.indent()
                        .background(base.copy())
                        .ifAvailable(Build.VERSION_CODES.M) {
                            it.foregroundDefaultSelectable()
                        }
                        .clipToOutline()
                }.indent()
                    // unfortunately does not work on older platforms :'(
//                    .background(base.copy { shadow(8, Colors.primary.withAlphaComponent(0.3F)) })
                    .background(base)
                    .elevation(8)
                    .noClip()
            }.indent()
                .padding(16, 8)
                .noClip()
                .onClick { }
        }
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIDynamicItemSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIDynamicItemSample()
}