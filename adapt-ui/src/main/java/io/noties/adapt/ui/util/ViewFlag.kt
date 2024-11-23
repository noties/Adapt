package io.noties.adapt.ui.util

import android.view.View
import androidx.annotation.IdRes
import io.noties.adapt.ui.R

interface ViewFlag {
    @get:IdRes
    val id: Int

    operator fun set(view: View, value: Boolean): Boolean {
        // if current is null, we consider it as `false` value,
        //  so if false is received, there is no change (as they equal)
        val hasChange = get(view) != value
        if (hasChange) {
            view.setTag(id, value)
        }
        return hasChange
    }

    operator fun get(view: View): Boolean {
        return (view.getTag(id) as? Boolean) ?: false
    }

    companion object : ViewFlag {
        fun create(@IdRes id: Int): ViewFlag {
            return object : ViewFlag {
                override val id: Int = id
            }
        }

        // uses one value by default, if same view would contain multiple
        //  flags, they need to have dedicated ids, otherwise the same value would
        //  be updated
        override val id: Int
            get() = R.id.adaptui_view_flag
    }
}

class ViewFlagWrapper internal constructor(
    private val view: View,
    private val flag: ViewFlag
) {
    fun set(value: Boolean): Boolean = flag.set(view, value)
    fun get(): Boolean = flag[view]
}

val View.flag: ViewFlagWrapper get() = ViewFlagWrapper(this, ViewFlag)

fun View.flag(@IdRes id: Int) = ViewFlagWrapper(this, ViewFlag.create(id))

