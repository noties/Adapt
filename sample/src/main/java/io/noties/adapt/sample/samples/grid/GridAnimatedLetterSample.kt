package io.noties.adapt.sample.samples.grid

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.Adapt
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.sample.ui.text.body
import io.noties.adapt.sample.util.GridAlphabet
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.grid.Grid
import io.noties.adapt.ui.element.grid.GridRow
import io.noties.adapt.ui.element.grid.Squares
import io.noties.adapt.ui.element.grid.gridSpacing
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.item.ElementItemNoRef
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.overlay
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.shape.Label
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.element
import io.noties.adapt.ui.widget.grid.GridLayout
import io.noties.adapt.ui.widget.grid.GridOverlayLayout
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.adapt.viewgroup.TransitionChangeHandler

@AdaptSample(
    id = "20241107234915",
    title = "Grid alphabet animated",
    description = "Draws letters with grid cells and animates transition between them",
    tags = ["grid", "widget", "adapt-ui"]
)
class GridAnimatedLetterSample : AdaptUISampleView() {

    // TODO: composite adapt?
    lateinit var shadowAdapt: Adapt
    lateinit var adapt: Adapt

    override fun ViewFactory<LayoutParams>.body() {
        // TODO: drop shadow?
        // TODO draw equal color divider (that is not divider, but just cell bounds)
        VStack {

            Grid {
                repeat(9) {
                    GridRow {
                        Squares(7)
                    }
                }
            }.indent()
                .backgroundColor { orange }
                .gridSpacing(4)
                .onView {
                    adapt = AdaptViewGroup.init(
                        it.gridForeground,
                        TransitionChangeHandler.createTransitionOnParent()
                    )
                    shadowAdapt = AdaptViewGroup.init(
                        it.getOrCreateOverlay(GridLayout.OVERLAY_PRIORITY_FOREGROUND - 1)
                            .also {
                                it.translationY = 4.dip.toFloat()
                                it.translationX = 4.dip.toFloat()
                            }
                    )
                }

            // controls

            Text("NEXT")
                .padding(8)
                .background { RoundedRectangle(8) { fill { primary } } }
                .textColor { white }
                .textGravity { center }
                .textSize { body }
                .layoutMargin(16)
                .foregroundDefaultSelectable()
                .onClick {
                    bind(charRoulette.next())
                }
        }.indent()
            .onView {
                // initially bind current
                bind(charRoulette.currentChar)
            }
            .preview {
                // render last added char in preview (as it is the one being added)
//                bind(letters.keys.last())
                bind('F')
            }
    }

    fun bind(char: Char) {
        val positions = GridAlphabet.glyphs[char] ?: error("Unknown char:'$char'")
        val spacing = GridAlphabet.Pos(x = 1, y = 1)

        val items = positions
            .withIndex()
            .map { (i, v) ->
                GridItem(
                    id = i.toLong(),
                    pos = v + spacing
                )
            }
        val shadowItems = items
            .map {
                GridItem(
                    id = it.id(),
                    pos = it.pos,
                    color = Colors.black
                )
            }

        adapt.setItems(items)
        shadowAdapt.setItems(shadowItems)
    }

    private val charRoulette = CharRoulette
        .all()
//        .adapt().also { startAutomaticNext() }
//        .text("NOTWHERE")

    private fun startAutomaticNext(delay: Long = 1000L) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object: Runnable {
            override fun run() {
                bind(charRoulette.next())
                handler.postDelayed(this, delay)
            }
        }
        handler.postDelayed(runnable, delay)
    }

    private class AllAvailableCharRoulette(
        override val characters: List<Char>
    ) : CharRoulette() {

        override var currentChar: Char = characters.first()

        override fun next(): Char {
            var char: Char? = null
            while (char == null) {
                char = characters.random().takeIf { it != currentChar }
            }
            currentChar = char
            return char
        }
    }

    private class StringCharRoulette(
        target: String,
        available: List<Char>
    ) : CharRoulette() {
        private companion object {
            fun prepare(target: String, available: List<Char>): List<Char> {
                return target
                    .map { c ->
                        available.firstOrNull { it == c } ?: error("Target char:'$c' is not found in:$available")
                    }
            }
        }

        override val characters = prepare(target, available)

        private var index = 0

        override var currentChar: Char = characters[index]

        override fun next(): Char {
            return characters[(++index) % characters.size]
        }
    }

    // make create implementations here directly? use-case might be abundant as we only delegate everything to it
    private abstract class CharRoulette {
        abstract val characters: List<Char>

        abstract val currentChar: Char

        abstract fun next(): Char

        companion object
    }

    private fun CharRoulette.Companion.adapt() =
        StringCharRoulette("ADAPT", GridAlphabet.available)

    private fun CharRoulette.Companion.text(text: String) =
        StringCharRoulette(text, GridAlphabet.available)

    private fun CharRoulette.Companion.all() =
        AllAvailableCharRoulette(GridAlphabet.available)

    private class GridItem(
        id: Long,
        val pos: GridAlphabet.Pos,
        val color: Int = Colors.white
    ) : ElementItemNoRef(id) {

        override fun ViewFactory<ViewGroup.LayoutParams>.body() {
            View()
        }

        override fun bind(holder: Holder<Unit>) {
            super.bind(holder)

            val view = holder.itemView()

            view.element
                .backgroundColor { color }
                .preview {
                    it.overlay {
                        Label(id().toString())
                            .textGravity { center }
                            .textSize { body }
                            .textColor { text }
                    }
                }
                .render()

            view.gridOverlayLayoutParams.also {
                it.horizontalSpan = { just(pos.x) }
                it.verticalSpan = { just(pos.y) }
            }

            view.requestLayout()
        }

        override fun createLayoutParams(parent: ViewGroup): ViewGroup.LayoutParams {
            return GridOverlayLayout.LayoutParams()
        }

        private val View.gridOverlayLayoutParams get() = layoutParams as GridOverlayLayout.LayoutParams
    }
}

@Preview
private class PreviewGridAnimatedLetterSample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample get() = GridAnimatedLetterSample()
}