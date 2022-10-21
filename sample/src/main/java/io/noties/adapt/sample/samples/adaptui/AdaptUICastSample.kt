package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.PreviewLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.addChildren
import io.noties.adapt.ui.background
import io.noties.adapt.ui.castLayout
import io.noties.adapt.ui.castView
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.ifCastLayout
import io.noties.adapt.ui.ifCastView
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.StatefulShape
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.util.Gravity
import io.noties.debug.Debug

@AdaptSample(
    id = "20221014142148",
    title = "AdaptUI, cast and ifCast sample",
    tags = ["adapt-ui"]
)
class AdaptUICastSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {
        ViewFactory.addChildren(view as ViewGroup) {
            VScroll {
                VStack {

                    val viewCastEntries = listOf(
                        true to false,
                        false to false,
                        true to true,
                        false to true
                    )
                    for ((success, isIf) in viewCastEntries) {
                        val name = if (isIf) "ifCastView" else "castView"
                        val result = if (success) "success" else {
                            if (isIf) "fail ignored" else "fail"
                        }
                        ViewCastEntry(
                            "Text() $name ($result)",
                            success,
                            isIf
                        )
                    }

                    val layoutCastEntries = listOf(
                        true to false,
                        false to false,
                        true to true,
                        false to true
                    )
                    for ((success, isIf) in layoutCastEntries) {
                        val name = if (isIf) "ifCastLayout" else "castLayout"
                        val result = if (success) "success" else {
                            if (isIf) "fail ignored" else "fail"
                        }
                        LayoutCastEntry(
                            "LinearLayout $name ($result)",
                            success,
                            isIf
                        )
                    }

                    // it is still possible to do `unsafe` cast
                    //  which can fail with a ClassCastException
                    //  when casting fails, but error message won't be
                    //  very helpful as it won't point to the actual
                    //  call there this happened

                    Element { CheckBox(it) as TextView }
                        .text("Unsafe cast (success)")
                        .textSize(16)
                        .padding(16)
                        .also { element ->
                            element.onClick {
                                (element as ViewElement<CheckBox, *>).render {
                                    it.checked(true)
                                }
                            }
                        }

                    Button("Unsafe cast (fail)")
                        .also { element ->
                            element.onClick {
                                try {
                                    (element as ViewElement<CheckBox, *>).render {
                                        it.checked(true)
                                    }
                                } catch (t: ClassCastException) {
                                    Debug.e(t, "Casting had failed")
                                }
                            }
                        }
                }
            }
        }
    }

    @Suppress("FunctionName")
    private fun ViewFactory<LayoutParams>.Title(title: String) = Text(title)
        .textColor(Colors.black)
        .textSize(21)
        .textFont(fontStyle = Typeface.BOLD)

    @Suppress("FunctionName")
    private fun ViewFactory<ViewGroup.MarginLayoutParams>.Button(title: String) = Text(title)
        .textGravity(Gravity.center)
        .padding(horizontal = 16, vertical = 8)
        .textColor(Colors.white)
        .layoutMargin(top = 8)
        .background(StatefulShape.drawable {
            val base = RoundedRectangle(6) {
                fill(Colors.primary)
            }
            setPressed(base.copy {
                alpha(0.45F)
            })
            setDefault(base)
        })

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.ViewCastEntry(
        title: String,
        success: Boolean,
        isIf: Boolean
    ): ViewElement<out View, LP> {
        return VStack {

            Title(title)

            val element: ViewElement<TextView, *> = if (success) {
                Element(::CheckBox)
            } else {
                Text()
            }

            element
                .text("Text() element")
                .textSize(16)
                .textColor(Colors.orange)
            // if we would not exit the render callback, there is no need to call explicitly `render`
//                .castView(CheckBox::class.java)
//                .checked(true)

            Button("Cast")
                .padding(16)
                .onClick {
                    // because we have exited the render callback, we need to explicitly call it
                    if (isIf) {
                        element.ifCastView(CheckBox::class.java) {
                            it.checked(true)
                                .layout(FILL, 128)
                                .background(Colors.orange)
                        }.render()
                    } else {
                        element.castView(CheckBox::class.java)
                            .checked(true)
                            .layout(FILL, 128)
                            .background(Colors.orange)
                            .render()
                    }
                }

        }.padding(16)
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.LayoutCastEntry(
        title: String,
        success: Boolean,
        isIf: Boolean
    ): ViewElement<out View, LP> {
        return VStack {

            Title(title)

            lateinit var element: ViewElement<out View, LayoutParams>

            if (success) {
                VStack {
                    element = Text("Inside LinearLayout!")
                }
            } else {
                ZStack {
                    element = Text("Inside FrameLayout!")
                }
            }

            Button("Cast")
                .onClick {
                    if (isIf) {
                        element.ifCastLayout(LinearLayout.LayoutParams::class.java) {
                            it.background(Colors.orange)
                                .layout(FILL, 128)

                        }.render()
                    } else {
                        element.castLayout(LinearLayout.LayoutParams::class.java)
                            .background(Colors.orange)
                            .layout(FILL, 128)
                            .render()
                    }
                }

        }.padding(16)
    }

    private fun <V : CheckBox, LP : LayoutParams> ViewElement<V, LP>.checked(
        checked: Boolean = true
    ) = onView {
        isChecked = checked
    }
}

@Suppress("ClassName", "unused")
private class __AdaptUICastSample(context: Context, attributeSet: AttributeSet?) :
    PreviewLayout(context, attributeSet) {
    init {
        AdaptUICastSample().render(this)
    }
}