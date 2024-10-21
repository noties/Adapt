package io.noties.adapt.sample.explore

import android.view.View
import androidx.annotation.IdRes
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object ExploreUtils {

    // good utility function
    sealed class ViewTag {
        companion object {

        }
    }

    private object Tag : ViewTag()
    private data class IdTag(@IdRes val id: Int) : ViewTag()

    // direct tag (not recommended)
    inline fun <reified T : Any?> View.someRandomStringShit1(): ReadWriteProperty<Nothing?, T?> {
        val view = this
        return object : ReadWriteProperty<Nothing?, T?> {
            override fun getValue(thisRef: Nothing?, property: KProperty<*>): T? {
                return view.tag as? T
            }

            override fun setValue(thisRef: Nothing?, property: KProperty<*>, value: T?) {
                view.tag = value
            }
        }
    }

    inline fun <reified T : Any?> View.someRandomStringShit2(@IdRes id: Int): ReadWriteProperty<Nothing, T?> {
        val view = this
        return object : ReadWriteProperty<Nothing?, T?> {
            override fun getValue(thisRef: Nothing?, property: KProperty<*>): T? {
                return (view.getTag(id) as? T)
            }

            override fun setValue(thisRef: Nothing?, property: KProperty<*>, value: T?) {
                view.setTag(id, value)
            }
        }
    }

//    fun hey(view: View) {
//        var maxHeight: Int? by view.someRandomStringShit2(0)
//    }
}