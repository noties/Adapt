package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.alpha
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.onElementView
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.widget.VStackReverseDrawingOrder
import io.noties.adapt.ui.sticky.stickyVerticalScrollContainer
import io.noties.adapt.ui.sticky.stickyView
import io.noties.debug.Debug

@AdaptSample(
    id = "20230403235622",
    title = "AdaptUI[Explore] sticky with reverse linear layout"
)
class AdaptUIStickySample : AdaptUISampleView() {

    lateinit var element: View

    override fun ViewFactory<LayoutParams>.body() {
        VScroll {
            // NB! very important to use reverse drawing order for sticky views to be drawn on top
            VStackReverseDrawingOrder {
                repeat(5) {
                    Regular("Before first $it")
                }

                Sticky("Sticky #1")
                Sticky("Sticky #1.1")

                repeat(20) {
                    Regular("After first #$it")
                }

                Sticky("Sticky #2")
                    .onElementView {
                        it.onClick {
                            // will automatically remove sticky view
                            val parent = it.view.parent as? ViewGroup
                            parent?.removeView(it.view)
                        }
                    }

                repeat(30) {
                    Regular("After second")
                }
            }
        }.layoutFill()
            .stickyVerticalScrollContainer {
                it.stickyViewDecoration = { v, isSticky ->
                    // will be called only when there is a change in state
                    val alpha = if (isSticky) 1F else 0.75F
                    v.clearAnimation()
                    v.animate()
                        .alpha(alpha)
                        .setDuration(250L)
                        .start()
                }
            }
//            .overScrollMode(View.OVER_SCROLL_NEVER)
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.Sticky(text: String) = Text(text)
        .padding(16)
        .alpha(0.75F)
        .background(Colors.black)
        .textColor(Colors.white)
        .ifAvailable(Build.VERSION_CODES.M) {
            it.foregroundDefaultSelectable()
        }
        .stickyView()
        .onClick { Debug.i("click '$text'") }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.Regular(text: String) = Text(text)
        .padding(8)
        .background(Colors.orange)
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIStickySample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIStickySample()
}