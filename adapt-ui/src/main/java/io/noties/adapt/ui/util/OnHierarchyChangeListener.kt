package io.noties.adapt.ui.util

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import io.noties.adapt.ui.R
import java.util.concurrent.CopyOnWriteArrayList

fun interface OnHierarchyChangeListenerRegistration {
    fun remove()
}

fun ViewGroup.addOnHierarchyChangeListener(listener: OnHierarchyChangeListener): OnHierarchyChangeListenerRegistration {
    val delegate = OnHierarchyChangeListenerDelegate.getOrCreate(this)
    delegate.add(listener)
    return OnHierarchyChangeListenerRegistration {
        delegate.remove(listener)
    }
}

fun ViewGroup.removeOnHierarchyChangeListener(listener: OnHierarchyChangeListener) {
    val delegate = OnHierarchyChangeListenerDelegate.getOrCreate(this)
    delegate.remove(listener)
}

private class OnHierarchyChangeListenerDelegate(val view: ViewGroup) {

    companion object {
        fun getOrCreate(view: ViewGroup): OnHierarchyChangeListenerDelegate {
            val tag = view.getTag(tagId) as? OnHierarchyChangeListenerDelegate
            return if (tag != null) {
                tag
            } else {
                OnHierarchyChangeListenerDelegate(view).also {
                    view.setTag(tagId, it)
                }
            }
        }

        private val tagId get() = R.id.adaptui_internal_hierarchy_delegate
    }

    val listeners = CopyOnWriteArrayList<OnHierarchyChangeListener>()

    init {
        view.onDetachedOnce {
            view.setTag(tagId, null)
            listeners.clear()
            view.setOnHierarchyChangeListener(null)
        }
        view.setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                listeners.forEach {
                    it.onChildViewAdded(parent, child)
                }
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {
                listeners.forEach {
                    it.onChildViewRemoved(parent, child)
                }
            }
        })
    }

    fun add(listener: OnHierarchyChangeListener) {
        listeners.add(listener)
    }

    fun remove(listener: OnHierarchyChangeListener) {
        listeners.remove(listener)
    }
}