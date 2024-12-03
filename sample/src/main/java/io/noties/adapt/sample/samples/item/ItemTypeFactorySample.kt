package io.noties.adapt.sample.samples.item

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import io.noties.adapt.Adapt
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.cyan
import io.noties.adapt.sample.ui.color.emeraldGreen
import io.noties.adapt.sample.ui.color.naplesYellow
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.sample.ui.color.purpureus
import io.noties.adapt.sample.ui.color.salmonRed
import io.noties.adapt.sample.ui.color.steelBlue
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.sample.ui.element.SampleHint
import io.noties.adapt.sample.ui.text.body
import io.noties.adapt.sample.ui.text.title3
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.adaptViewGroup
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.app.color.ColorsBuilder
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Item
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.item.ItemTypeFactory
import io.noties.adapt.ui.item.build
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.onElementView
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.setItems
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.element
import io.noties.adapt.ui.util.pxToDip
import io.noties.adapt.ui.util.withAlphaComponent
import io.noties.adapt.ui.windowinset.onWindowInsetsChanged
import io.noties.adapt.viewgroup.TransitionChangeHandler
import java.util.Date

@AdaptSample(
    id = "20241130014533",
    title = "ItemTypeFactory usage",
    // TODO: add factory tag
    tags = [Tags.adaptUi]
)
class ItemTypeFactorySample : SampleViewUI() {

    private lateinit var scrollView: ScrollView
    private lateinit var adapt: Adapt

    override fun ViewFactory<LayoutParams>.body() {
        ZStack {

            VScroll {
                VStack {}
                    .noClip()
                    .adaptViewGroup(TransitionChangeHandler.createTransitionOnParent())
                    .reference(::adapt)
                    .setItems(listOf(HintItem))
            }.indent()
                .layoutFill()
                .reference(::scrollView)
                .padding(bottom = 142)
                .noClip()

            Item(ControlItem())
                .layoutGravity { bottom }
                .backgroundColor { naplesYellow.withAlphaComponent(0.1F) }
                .onElementView {
                    val padding = it.view.paddingBottom.pxToDip
                    it.onWindowInsetsChanged {
                        it.padding(bottom = insetsBottom.pxToDip + padding)
                    }
                }

        }.layoutFill()
    }

    @Suppress("PrivatePropertyName")
    private val ControlItem = ItemTypeFactory.builder()
        .ref {
            class Ref {
                lateinit var removeView: View
                lateinit var addView: View
            }
            Ref()
        }
        .view {
            HStack {
                Text("Clear items")
                    .reference(it::removeView)
                    .layout(0, wrap, 1F)
                    .styleControlButton { salmonRed }

                View().layout(8, 0)

                Text("Add item")
                    .reference(it::addView)
                    .layout(0, wrap, 1F)
                    .styleControlButton { steelBlue }
            }.indent()
                .padding(8)
        }
        .bind {
            ref.removeView.element
                .onClick {
                    val items = adapt.items()
                    if (items.size > 1) {
                        adapt.setItems(listOf(HintItem))
                    }
                }
                .render()
            ref.addView.element
                .onClick {
                    val items =
                        adapt.items()
                            .let {
                                if (it.size == 1 && it[0] === HintItem) {
                                    // should be only hint
                                    // return empty list, so hint is hidden
                                    mutableListOf()
                                } else {
                                    it.toMutableList()
                                }
                            }
                            .also {
                                it.add(StringItem(Date().toString()))
                            }
                    adapt.setItems(items)
                    scrollView.postDelayed({
//                        scrollView.smoothScrollTo(0, scrollView.getChildAt(0).height)
                        scrollView.fullScroll(View.FOCUS_DOWN)
                    }, 1000L)
                }
                .render()
        }
        .build(0L)

    private fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.styleControlButton(
        color: ColorsBuilder
    ) = this
        .padding(horizontal = 12, vertical = 8)
        .textSize { body }
        .textColor { white }
        .textGravity { center }
        .background {
            RoundedRectangle(8)
                .fill { color(Colors) }
        }
        .foregroundDefaultSelectable()
        .clipToOutline()

    // this is factory function that returns instances of Item specified here
    // (String) -> Item<*>
    @Suppress("PrivatePropertyName")
    private val StringItem = ItemTypeFactory.builder()
        .input(String::class)
        .id { System.nanoTime() }
        .ref {
            class Ref {
                lateinit var textView: TextView
            }
            Ref()
        }
        .view {
            Text()
                .reference(it::textView)
                .textSize { title3 }
                .textColor { white }
                .textGravity { center }
                .padding(16)
                .backgroundColor {
                    listOf(naplesYellow, emeraldGreen, purpureus, orange, cyan, primary).random()
                }
        }
        .bind {
            ref.textView.text = it
        }
        .onRefReady {
            System.out.println("ref ready:$this")
        }
        .build()

    @Suppress("PrivatePropertyName")
    private val HintItem = ItemTypeFactory.builder()
        .view {
            SampleHint("Click \"Add item\" to start\nbuilding random list of items")
        }
        .build(0L)
        // build immediately, create a singleton item
        .invoke()
}

@Preview
private class PreviewItemTypeFactorySample(context: Context, attrs: AttributeSet?) :
    PreviewSampleView(context, attrs) {
    override val sampleView: SampleView get() = ItemTypeFactorySample()
}