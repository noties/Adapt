package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.TextInput
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textImeOptions
import io.noties.adapt.ui.element.textSingleLine
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.util.ImeOptions
import io.noties.adapt.ui.util.ImeOptionsBuilder
import io.noties.debug.Debug

@AdaptSample(
    id = "20240331120232",
    title = "Text.textImeOptions",
    tags = ["adapt-ui"]
)
class AdaptUiTextInputImeOptionsSample: AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VScroll {
            VStack {

                Ime {
                    actionSearch { Debug.i("search") }
                }

                Ime {
                    actionGo { Debug.i("go") }
                }

                Ime {
                    navigatePrevious.actionPrevious { Debug.i("previous") }
                }

                Ime {
                    navigateNext.actionNext { Debug.i("next") }
                }

                Ime {
                    actionSend { Debug.i("send") }
                }

                Ime {
                    actionDone { Debug.i("done") }
                }

                Ime {
                    noFullScreen.noExtractUi.actionUnspecified
                }

            }
        }.layoutFill()
    }

    @Suppress("FunctionName")
    private fun <LP: LayoutParams> ViewFactory<LP>.Ime(builder: ImeOptionsBuilder) = VStack {
        val ime = builder(ImeOptions)
        Text(ime.toString())
            .textSize { 15 }
            .textColor { hex("#ccc") }
        TextInput()
            .textImeOptions { ime }
            .textSingleLine(true)
    }
}

private class PreviewAdaptUiTextInputImeOptionsSample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUiTextInputImeOptionsSample()
}