package io.noties.adapt.sample.samples.getter

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Adapt
import io.noties.adapt.kt.getter
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.CollectionItem
import io.noties.adapt.sample.items.PlainItem
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.text.body
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.adaptViewGroup
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.element.textStyle
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.reference
import io.noties.debug.Debug

@AdaptSample(
    id = "20240924213029",
    title = "AdaptGetter usage",
    tags = ["adapt"]
)
class AdaptGetterSample : AdaptUISampleView() {

    private lateinit var adapt: Adapt

    private val textOnly get() = adapt.getter { this.filterIsInstance<TextItem>() }

    private val textOnlyStartingWithA
        get() = adapt.getter {
            this.filterIsInstance<TextItem>()
                .filter { it.text.startsWith("A") }
        }

    private val textCast
        get() = adapt.getter {
            this.cast(TextItem::class.java)
        }

    override fun ViewFactory<LayoutParams>.body() {
        VScroll {
            VStack {}
                .adaptViewGroup()
                .reference(::adapt)
        }.indent()
            .layoutFill()
            .onView {
                fun print(label: String) {
                    Debug.i("$label " +
                            "textOnlyStartingWithA:${textOnlyStartingWithA.items()} " +
                            "textOnly:${textOnly.items()} " +
                            "cast:${try { textCast.items().map { it.text } } catch (t: Throwable) { t }}")
                }

                print("#1")

                adapt.setItems(listOf(
                    TextItem("ABC"),
                    TextItem("ZZZ"),
                    TextItem("AOU"),
                    TextItem("APM")
                ))

                print("#2")

                adapt.setItems(
                    adapt.items() + PlainItem("G", Colors.hex("#0f0"), "GOGOGO"),
                )

                print("#3")
            }
    }

    open class TextItem(val text: String) : ElementItem<TextItem.Ref>(hash(text), { Ref() }) {
        class Ref {
            lateinit var textView: TextView
        }

        override fun bind(holder: Holder<Ref>) {
            with(holder.ref) {
                textView.text = text
            }
        }

        override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
            Text()
                .reference(ref::textView)
                .textStyle { body }
        }

        override fun toString(): String {
            return "TextItem(text='$text')"
        }
    }
}

@Preview
private class PreviewAdaptGetterSample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptGetterSample()
}