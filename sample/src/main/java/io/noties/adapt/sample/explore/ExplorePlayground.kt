package io.noties.adapt.sample.explore

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.samples.getter.AdaptGetterSample
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.color.textSecondary
import io.noties.adapt.sample.ui.text.footnote
import io.noties.adapt.sample.ui.text.subHeadline
import io.noties.adapt.sample.ui.text.title3
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.adaptViewGroup
import io.noties.adapt.ui.app.App
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textHideIfEmpty
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.element.textTypeface
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.onAdapt
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.PreviewLayout

// the purpose of this class is to hold a set of items that could be applied to certain
//  set and screenshot made in order to compare with previous versions.
object ExplorePlayground {

    data class AdaptPlaygroundInfo(
        // by default name of the implementation class
        val name: String,
        val description: String?
    )

    // TODO: playground preview
    abstract class AdaptPlayground {
        // but what  if i just want to customize a little the container, maybe background
        //  or small change, like orientation. it should be possible from here, but not required
        //  only the `items` should be required

        // playground in general could have additional parameters, so we must make sure we allow that
        //  maybe set to companion a factory? how it is different from normal constructor?

        // each item should be available to provide title (and description), maybe something else
        // maybe provide custom title and description via wrapper, so:
        //  MyUten(),plavound(name, description)
        abstract fun items(): List<Item<*>>

        open val title: String get() = this::class.simpleName ?: "<none>"
        open val description: String? get() = null

        open val properties: Map<String, Any?> get() = emptyMap()

        open val context: Context get() = App.context

        open fun start(adapt: Adapt) {
            adapt.setItems(items())
        }

        open fun createView(context: Context): View {
            // all defaults
            //  - scroll-view, vertical stack, which is adapt container without any special configs
            return ViewFactory.createView(context) {
                createViewUi {
                    // when adapt is ready just immediately set the items
                    start(it)
                }
            }
        }

        open fun ViewFactory<LayoutParams>.createViewUi(onAdaptReady: (Adapt) -> Unit) {
            VStack {

                createInfoViewUi()

                createAdaptViewUi(onAdaptReady = onAdaptReady)
            }
        }

        open fun ViewFactory<LinearLayout.LayoutParams>.createInfoViewUi(): ViewElement<out View, LinearLayout.LayoutParams> {
            return VStack {
                Text(title)
                    .textSize { title3 }
                    .textColor { text }
                    .textBold()
                Text(this@AdaptPlayground::class.qualifiedName)
                    .textSize { footnote }
                Text(description)
                    .textSize { subHeadline }
                    .textColor { textSecondary }
                    .textHideIfEmpty()
                    .layoutMargin(top = 4)

                properties
                    .map {
                        "${it.key}:${if (it.value is String) "'${it.value}'" else "${it.value}"}"
                    }
                    .joinToString(separator = "  ")
                    .takeIf { it.isNotEmpty() }
                    ?.let {
                        Text(it)
                            .textTypeface(Typeface.MONOSPACE)
                            .textSize { footnote }
                            .textColor { text }
                            .layoutMargin(top = 8)
                    }
            }.indent()
                .padding(16)
        }

        open fun ViewFactory<LinearLayout.LayoutParams>.createAdaptViewUi(
            onAdaptReady: (Adapt) -> Unit
        ) {
            VStack {}
                .adaptViewGroup()
                .onAdapt(onAdaptReady)
        }
    }
}

@Preview
private class PreviewExplorePlayground(
    context: Context,
    attrs: AttributeSet?
) : PreviewLayout(context, attrs) {

    // anyway, needs to be abstract class instead, as to allow wrapping call to super
    object Impl : ExplorePlayground.AdaptPlayground() {
        override fun items(): List<Item<*>> {
            return listOf(
                AdaptGetterSample.TextItem("HELLO")
            )
        }

        override val description: String?
            get() = "Hello there! This is a simple description of what should happen"

        override val properties: Map<String, Any?>
            get() = mapOf(
                "hello" to "world",
                "is-enough" to true,
                "value" to 4.2F,
                "something_null" to null,
                "some_optional" to description
            )

        override fun ViewFactory<LinearLayout.LayoutParams>.createInfoViewUi(): ViewElement<out View, LinearLayout.LayoutParams> {
            val vf = this
            with(vf) {
                this.createInfoViewUi()
            }
            return super.createInfoViewUi()
        }
    }

    override fun createView(context: Context, parent: PreviewLayout): View {
        val pg = Impl
        return ViewFactory.createView(context, parent) {
            ZStack {

                Element {
                    pg.createView(context)
                }

            }.indent()
                .layoutFill()
        }
    }
}