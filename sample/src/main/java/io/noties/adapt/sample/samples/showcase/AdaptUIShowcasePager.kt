package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorInt
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.samples.adaptui.Colors
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.alpha
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Pager
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.ViewPagerLayoutParams
import io.noties.adapt.ui.element.pagerOffscreenPageLimit
import io.noties.adapt.ui.element.pagerOnPageSelectedListener
import io.noties.adapt.ui.element.pagerPageWidthRatio
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.Gravity
import io.noties.debug.Debug

@AdaptSample(
    id = "20230601135039",
    title = "[Showcase] AdaptUI, Pager element",
    description = "<em>Pager</em>, <em>ViewPager</em>",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcasePager : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {
            Pager {

                Page("This is my first page", Colors.white)
                    .also { element ->
                        // listen for THIS page selection state
                        element.pagerOnPageSelectedListener {
                            Debug.i("isSelected:$it")
                            val color = if (it) Colors.primary else Colors.orange
                            element.textColor(color)
                        }
                    }

                Page("This is second", Colors.accent)
                Page("This is third", Colors.orange)

            }.indent()
                // pager needs specific layout dimensions
                .layout(FILL, 128)
                // offscreen page limit
                .pagerOffscreenPageLimit(3)
                .pagerOnPageSelectedListener { page ->
                    Debug.i("page selected:$page totalPages:$pagesCount")
                    // also can access ViewPager if required
                    viewPager.postInvalidate()

                    // access elements
                    pageElements.withIndex()
                        .forEach {
                            val isSelected = it.index == page
                            val alpha = if (isSelected) 1F else 0.45F
                            it.value.alpha(alpha)
                        }
                }
        }
    }

    @Suppress("FunctionName")
    private fun ViewFactory<ViewPagerLayoutParams>.Page(
        title: String,
        @ColorInt color: Int
    ) = Text(title)
        .layoutFill()
        .textGravity(Gravity.center)
        .textSize(18)
        .pagerPageWidthRatio(0.82F)
        // content padding + shape padding
        .padding(8 + 8)
        .background {
            RoundedRectangle(12)
                .fill(color)
                .padding(4, 8)
                .shadow(4)
        }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIShowcasePager(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIShowcasePager()
}