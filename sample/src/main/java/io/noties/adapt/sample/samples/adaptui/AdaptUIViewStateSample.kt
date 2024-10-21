package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.sample.ui.color.textSecondary
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.sample.ui.text.body
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.overlay
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.shape.Label
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.state.backgroundWithState
import io.noties.adapt.ui.state.imageTintWithState
import io.noties.adapt.ui.state.onViewStateChange
import io.noties.adapt.ui.state.textColorWithState
import io.noties.adapt.ui.state.viewState
import io.noties.adapt.ui.util.withAlphaComponent
import io.noties.debug.Debug

@AdaptSample(
    id = "20241017013604",
    title = "AdaptUI ViewState",
    description = "Usage of ViewState (also known as &quot;drawable state&quot; on native Android layer), " +
            "<tt>pressed</tt>, <tt>focused</tt> and other android.R.attr attributes"
)
class AdaptUIViewStateSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VScroll {
            VStack {

                PreviewOnBackground()

                Text("This is text with state [click me]")
                    .padding(16)
                    .layoutMargin(top = 24, bottom = 8)
                    .layoutMargin(horizontal = 16)
                    .textColorWithState {
                        pressed = black.withAlphaComponent(0.2F)
                        default = black
                    }
                    .backgroundWithState {
                        val base = RoundedRectangle(9)
                        pressed = base.copy { fill { accent } }
                        default = base.copy().fill { black.withAlphaComponent(0.2F) }
                    }
                    .onClick { }

                Image(R.drawable.ic_code_24)
                    .imageTintWithState {
                        pressed = primary
                        activated = orange
                        default = white
                    }
                    .layoutMargin(8)
                    .padding(12)
                    .background {
                        RoundedRectangle(10) {
                            fill { textSecondary }
                        }
                    }
                    .foregroundDefaultSelectable()
                    .clipToOutline()
                    .also { el ->
                        el.onClick {
                            el.view.isActivated = !el.view.isActivated
                        }
                    }

                Text("CLICK ME")
                    .layoutMargin(horizontal = 16)
                    .layoutMargin(top = 36)
                    .padding(horizontal = 16, vertical = 12)
                    .textSize { body }
                    .textColor { white }
                    .textGravity { center }
                    .background { Capsule { fill { accent } } }
                    .foregroundDefaultSelectable()
                    .onViewStateChange { view, viewState ->
                        if (viewState.isPressed) {
                            view.alpha = 0.4F
                        } else {
                            view.alpha = 1F
                        }
                    }
                    .onClick { }
                    .clipToOutline()

            }
        }.layoutFill()
    }

    @Suppress("FunctionName")
    private fun ViewFactory<LayoutParams>.PreviewOnBackground() {

        fun stateShape(name: String, color: Colors.() -> Int): RectangleShape {
            val accent = color(Colors)
            return RectangleShape {
                gravity { center }
                size(100, 75)
                Rectangle {
                    padding(2)
                    stroke(accent, 2)
                }
                Label(name) {
                    textColor(accent)
                    textGravity { center }
                    textSize { 17 }
                }
            }
        }

        VStack {

            val view = View()
                .layout(fill, 200)
                .layoutMargin(16)
                .onViewStateChange { view, viewState ->
                    val vs = view.viewState
                    Debug.i("view.state:$vs viewState:$viewState")
                }
                .overlay {
                    Rectangle {
                        padding(2)
                        Rectangle {
                            stroke(Colors.black.withAlphaComponent(0.42F), 4, 8, 4)
                        }
                    }
                }
                .backgroundWithState {
                    /**
                    We define then here because we reuse them in compositions, normally
                    you would define one base shape that you would copy to create a new state.

                    ```kotlin
                    val base = RoundedRectangle(9)

                    enabled.activated = base.copy { fill { .activated } }
                    enabled = base.copy { fill { .highlight } }

                    activated = base.copy { fill { .blue } }
                    ```
                     */
                    val enabledShape = stateShape("enabled") { primary }.translate(x = -32, y = -24)
                    val activatedShape =
                        stateShape("activated") { accent }.translate(x = 32, y = 24)

                    enabled.activated = Rectangle {
                        add(enabledShape)
                        add(activatedShape)
                    }
                    activated = Rectangle { add(activatedShape) }
                    enabled = Rectangle { add(enabledShape) }
                    default = Rectangle {
                        add(stateShape("default") { black }.gravity { leading.top })
                    }
                }
                // just in case to trigger redraw
                .onClick { }

            HStack {

                SmallButton(text = "Enable/Disable")
                    .layout(0, wrap, 1F)
                    .layoutMargin(horizontal = 8)
                    .also { el ->
                        el.onClick {
                            val target = !view.view.isEnabled
                            view.view.isEnabled = target
                            el.text(
                                // if target is ENABLED, then button is the opposite -> DISABLE
                                if (target) "Disable" else "Enable"
                            )
                        }
                    }

                SmallButton(text = "Activate/Dis-activate", color = { primary })
                    .layout(0, wrap, 1F)
                    .layoutMargin(horizontal = 8)
                    .also { el ->
                        el.onClick {
                            val target = !view.view.isActivated
                            view.view.isActivated = target
                            el.text(
                                // if target is ENABLED, then button is the opposite -> DISABLE
                                if (target) "Dis-activate" else "Activate"
                            )
                        }
                    }
            }
        }
    }

    @Suppress("FunctionName")
    fun <LP : LayoutParams> ViewFactory<LP>.SmallButton(
        text: String,
        color: Colors.() -> Int = { accent }
    ) = Text(text)
        .padding(horizontal = 16, vertical = 8)
        .textColor { white }
        .textSize { body }
        .textGravity { center }
        .textBold()
        .background {
            RoundedRectangle(8) {
                fill(color(Colors))
            }
        }
        .foregroundDefaultSelectable()
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIViewStateSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIViewStateSample()
}