package io.noties.adapt.ui.util

// Simple iterator that allows wrapped list modification, so if an element was
//  added it would be properly returned for the iteration
internal class DynamicIterator<T>(private val original: List<T>) : Iterator<T> {

    internal companion object {
        fun <T> List<T>.dynamicIterator() = DynamicIterator(this)
    }

    private var index = -1

    override fun hasNext(): Boolean {
        return (index + 1) < (original.size)
    }

    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException("index:$index, size:${original.size}, original:$original")
        return original[++index]
    }
}