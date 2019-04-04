package ru.noties.adapt.sample.screen.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import ru.noties.adapt.Item
import ru.noties.adapt.ItemWrapper

class GroupWrapper<H : Item.Holder>(item: Item<H>) : ItemWrapper<H>(item) {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): H {
        // this is done for sample purposes only
        // in this case we reduce the original size of item to be 1/3th of parent
        return super.createHolder(inflater, parent).also { holder ->
            parent.width {
                holder.itemView.layoutParams.width = it / 3
                holder.itemView.requestLayout()
            }
        }
    }

    private fun View.width(callback: (Int) -> Unit) {
        val w = width
        if (w > 0) {
            callback(w)
        } else {
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return true.also {
                        if (viewTreeObserver.isAlive) {
                            viewTreeObserver.removeOnPreDrawListener(this)
                            callback(width)
                        }
                    }
                }
            })
        }
    }
}