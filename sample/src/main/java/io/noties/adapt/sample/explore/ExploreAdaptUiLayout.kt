package io.noties.adapt.sample.explore

import android.content.Context
import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.ViewFactoryConstants
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.layout

@Suppress("StopShip")
object ExploreAdaptUiLayout {
    fun hey(context: Context) {
        ViewFactory.createView(context) {
            View()
//                .layout3 {
//                    width 1
//                }
                .layout(fill, 0)
                .layout(0, 0)
                .layout2 {
                    // cannot understand without prefix this
                    fill(fill)
                    // second `fill` is unresolved (it sees `val fill` first)
//                    fill fill

                    // verbose, different highlight, required `this`
                    this fill fill
                }
                .layout3 {
                    width = 10
                    width = fill
                    height = wrap
                }
                .layout4 {
                    width to fill
                    height to wrap
                }
        }
    }


    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.layout2(
        block: LayoutBuilder2.() -> LayoutBuilder2.Layout
    ) = this.also {
        // we need to specify width and height
        //  (potentially others like weight or gravity for other layouts)
    }

    interface LayoutBuilder2 {
        data class Layout(val w: Int, val h: Int)
        data class Dimension(val value: Int)

        infix fun fill(dimension: Dimension): Layout

        val fill: Dimension get() = Dimension(-1)
        val wrap: Dimension get() = Dimension(-2)
    }

    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.layout3(
        block: LayoutBuilder3.() -> Unit
    ) = this.also {

    }

    class LayoutBuilder3: ViewFactoryConstants {
//        data class Layout(val w: Int, val h: Int)
//        data class Dimension(val value: Int)

//        interface DimensionBuilder {
//            infix operator fun invoke(dimension: Int): Dimension = TODO()
//        }

        var width: Int = 0
        var height: Int = 0
    }

    ///////////////////////////////////////////////////////////////


    class LayoutBuilder4 {
//        class Layout
        class Dimension {
//            infix fun to(value: Int) {
//
//            }
        }

        val width: Dimension get() = TODO()
        val height: Dimension get() = TODO()

        infix fun Dimension.to(value: Int) {

        }
    }

    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.layout4(
        block: LayoutBuilder4.() -> Unit
    ) = this.also {
        it
    }
}