package io.noties.adapt.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup.LayoutParams

class ViewElement<V : View, LP : LayoutParams>(
    private val provider: (Context) -> V
) {

    lateinit var view: V

    private val layoutBlocks: MutableList<LP.() -> Unit> = mutableListOf()
    private  val viewBlocks: MutableList<V.() -> Unit> = mutableListOf()

    fun init(context: Context): V {
        view = provider(context)
        return view
    }

    fun onView(
        block: V.() -> Unit
    ): ViewElement<V, LP> = this.also {
        it.viewBlocks.add(block)
    }

    fun onLayout(
        block: LP.() -> Unit
    ): ViewElement<V, LP> = this.also {
        it.layoutBlocks.add(block)
    }

    // hm, as we do not hold view, it might not be very helpful,
    //  as we would need to keep referencing view additionally
    fun render() {

        // does clearing make sense? would it be possible to use the same
        //  element to apply styling to multiple views?
        //  it would make sense, if we would hold our created view only (without accepting
        //  external view)
        layoutBlocks.also {
            @Suppress("UNCHECKED_CAST")
            if (it.isNotEmpty()) {
                val lp = view.layoutParams as LP
                it.forEach { it(lp) }
            }
            it.clear()
        }

        viewBlocks.also {
            it.forEach { it(view) }
            it.clear()
        }
    }
}

typealias AnyViewElement = ViewElement<*, *>