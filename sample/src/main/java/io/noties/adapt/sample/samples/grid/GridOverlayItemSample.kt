package io.noties.adapt.sample.samples.grid

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStackWrapHeightOrScroll
import io.noties.adapt.ui.element.grid.Grid
import io.noties.adapt.ui.element.grid.GridRow
import io.noties.adapt.ui.element.grid.Squares
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.item.ElementItemNoRef
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.util.withAlphaComponent
import io.noties.adapt.ui.widget.grid.GridOverlayLayout
import io.noties.adapt.viewgroup.AdaptViewGroup

@AdaptSample(
    id = "20241106025706",
    title = "GridOverlay with adapter Item",
    tags = ["grid", "adapt-ui", "widget"]
)
class GridOverlayItemSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        // let's add a slightly different background for even rows
        ZStackWrapHeightOrScroll {
            VStack {
                val grid = Grid {
                    repeat(5) { GridRow { Squares(5) } }
                }.indent()
                    .preview(true) { it.previewBounds() }
                    .onView {
                        val adapt = AdaptViewGroup.init(it.gridBackground)
                        it.addOnGridStateChangeListener { (_, rows) ->
                            val items = (0 until rows)
                                .withIndex()
                                .map { (i, _) ->
                                    if (i % 2 == 0) {
                                        ClearRowItem(i)
                                    } else {
                                        BackgroundRowItem(i)
                                    }
                                }
                            adapt.setItems(items)
                        }
                    }

                GridControls(grid)
                    .layoutMargin(vertical = 24)
            }
        }
    }

    private class ClearRowItem(val y: Int) : ElementItemNoRef(y.toLong()) {
        override fun ViewFactory<ViewGroup.LayoutParams>.body() {
            View()
        }

        override fun createLayoutParams(parent: ViewGroup): ViewGroup.LayoutParams {
            return GridOverlayLayout.LayoutParams().also {
                it.horizontalSpan = { fill() }
            }
        }

        override fun bind(holder: Holder<Unit>) {
            super.bind(holder)

            (holder.itemView().layoutParams as GridOverlayLayout.LayoutParams).verticalSpan =
                { just(y) }
            holder.itemView().requestLayout()
        }
    }

    private class BackgroundRowItem(val y: Int) : ElementItemNoRef(y.toLong()) {
        override fun ViewFactory<ViewGroup.LayoutParams>.body() {
            View()
                .backgroundColor { black.withAlphaComponent(0.42F) }
        }

        override fun createLayoutParams(parent: ViewGroup): ViewGroup.LayoutParams {
            return GridOverlayLayout.LayoutParams().also {
                it.horizontalSpan = { fill() }
            }
        }

        override fun bind(holder: Holder<Unit>) {
            super.bind(holder)

            (holder.itemView().layoutParams as GridOverlayLayout.LayoutParams).verticalSpan =
                { just(y) }
            holder.itemView().requestLayout()
        }
    }
}

@Preview
private class PreviewGridOverlayItemSample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = GridOverlayItemSample()
}