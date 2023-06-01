package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.sample.util.withAlphaComponent
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textAllCaps
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSingleLine
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.focusable
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.CapsuleShape
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.ShapeDrawable
import io.noties.adapt.ui.shape.Text
import io.noties.adapt.ui.state.DrawableState
import io.noties.adapt.ui.state.onDrawableStateChange
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip
import io.noties.debug.Debug

@AdaptSample(
    id = "20230513001821",
    title = "DrawableState",
    description = "Receive drawable state updates"
)
class AdaptUIDrawableStateSample : AdaptUISampleView() {

    /**
     * In order to receive all drawable states reliably a stateful drawable must be used
     * as a `background` or `foreground`. For example, default selectable drawable
     */
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            Text("Click me!")
                .textSize(16)
                .textColor(Colors.white)
                .textGravity(Gravity.center)
                .padding(12)
                .layoutMargin(16)
                .background(CapsuleShape {
                    fill(Colors.primary)
                }.newDrawable().stateful(setOf(DrawableState.pressed)))
                .onDrawableStateChange { textView, drawableState ->
                    Debug.e("state:$drawableState")
                    textView.clearAnimation()

                    val target = if (drawableState.pressed) 0.82F else 1F
                    textView.animate()
                        .alpha(target)
                        .setDuration(250L)
                        .start()
                }
                .onClick {
                    Debug.i("clicked")
                }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Text("Shape.Text is not available before Android.O (26)")
                    .textSize(16)
                    .layoutWrap()
                    .layoutGravity(Gravity.center)
            } else {
                val view = View()
                    .layout(FILL, 128)
                    .focusable(true)
                    .background(createStatefulShapeDrawable().stateful { state ->
                        Debug.i("state:$state")
                        ref.pressed.hidden(!state.pressed)
                        ref.focused.hidden(!state.focused)
                        ref.enabled.hidden(!state.enabled)
                        ref.activated.hidden(!state.activated)
                    })
                    .onClick {
                        Debug.i("clicked!")
                    }

                HStack {

                    Button("Enable") {
                        view.view.isEnabled = !view.view.isEnabled
                    }

                    Button("Focus") {
                        if (view.view.isFocused) {
                            view.view.clearFocus()
                        } else {
                            view.view.requestFocus()
                        }
                    }

                    Button("Activate") {
                        view.view.isActivated = !view.view.isActivated
                    }
                }.padding(4)
            }

            HStack {
                ElevatingButton("Normal")
                    .layout(0, WRAP, 1F)
                    .layoutMargin(horizontal = 8)
                ElevatingButton("Pressed")
                    .layout(0, WRAP, 1F)
                    .layoutMargin(horizontal = 8)
                    .onView { it.isPressed = true }
            }.layoutMargin(top = 16)
                .noClip()

        }.layoutFill()
            .noClip()
    }

    @Suppress("FunctionName")
    fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.Button(
        label: String,
        onClick: () -> Unit
    ) = Text(label)
        .textSize(16)
        .textColor(Colors.white)
        .textSingleLine()
        .textGravity(Gravity.center)
        .textAllCaps()
        .padding(12, 4)
        .layoutMargin(horizontal = 4)
        .layout(0, WRAP, 1F)
        .background(CapsuleShape().fill(Colors.primary))
        .ifAvailable(Build.VERSION_CODES.O) {
            it.foregroundDefaultSelectable()
        }
        .clipToOutline()
        .onClick(onClick)

    @Suppress("FunctionName")
    fun <LP : LayoutParams> ViewFactory<LP>.ElevatingButton(
        label: String
    ) = Text(label)
        .textSize(21)
        .textFont(Typeface.MONOSPACE)
        .textAllCaps()
        .textColor(Colors.white)
        .textSingleLine()
        .textGravity(Gravity.center)
        .padding(12, 8)
        .background(ShapeDrawable {
            Rectangle {
                padding(2)
                fill(Colors.orange)
                Rectangle {
                    stroke(Colors.primary, 4)
                }
            }
        }.stateful { state ->
            if (state.pressed) {
                shape.shadow(12)
            } else {
                shape.shadow(2)
            }
        })
        .onDrawableStateChange { textView, drawableStateSet ->
            val y = if (drawableStateSet.pressed) -2 else 0
            textView.translationY = y.dip.toFloat()
        }
        .onClick {

        }

    private class Ref {
        lateinit var pressed: Shape
        lateinit var focused: Shape
        lateinit var enabled: Shape
        lateinit var activated: Shape
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createStatefulShapeDrawable(): ShapeDrawable<Ref> {
        return ShapeDrawable(Ref()) { ref ->
            RectangleShape {
                fill(Colors.black.withAlphaComponent(0.1F))

                ref.pressed = Text("pressed") {
                    textSize(16)
                    textBold()
                    textGravity(Gravity.leading.top)
                    textColor(Colors.primary)
                    padding(8)
                }

                ref.focused = Text("focused") {
                    textSize(24)
                    textItalic()
                    textGravity(Gravity.trailing.top)
                    textColor(Colors.black)
                    padding(8)
                }

                ref.enabled = Text("enabled") {
                    textSize(21)
                    textUnderline()
                    textGravity(Gravity.trailing.bottom)
                    textColor(Colors.orange)
                    padding(8)
                }

                ref.activated = Text("activated") {
                    textSize(18)
                    textStrikethrough()
                    textGravity(Gravity.leading.bottom)
                    textColor(Colors.accent)
                    padding(8)
                }
            }
        }
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIDrawableStateSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIDrawableStateSample()
}