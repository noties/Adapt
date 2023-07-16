package io.noties.adapt.ui.util

import io.noties.adapt.ui.util.DynamicIterator.Companion.dynamicIterator
import org.junit.Assert.*
import org.junit.Test

@Suppress("ClassName")
class DynamicIterator_Test {

    @Test
    fun empty() {
        val input = emptyList<String>()
        val iterator = input.dynamicIterator()

        assertEquals(false, iterator.hasNext())
        assertEquals(false, iterator.hasNext())
        assertEquals(false, iterator.hasNext())

        assertThrows(NoSuchElementException::class.java) {
            iterator.next()
        }
    }

    @Test
    fun single() {
        val input = listOf(1)
        val iterator = input.dynamicIterator()
        assertEquals(true, iterator.hasNext())
        assertEquals(1, iterator.next())
        assertEquals(false, iterator.hasNext())
        assertEquals(false, iterator.hasNext())
        assertThrows(NoSuchElementException::class.java) {
            iterator.next()
        }
    }

    @Test
    fun `added during iteration`() {
        val input = mutableListOf(1L, 2L, 3L)
        val iterator = input.dynamicIterator()
        val output = mutableListOf<Long>()

        assertEquals(3, input.size)

        while (iterator.hasNext()) {
            val next = iterator.next()
            when (next) {
                1L -> input.add(4L)
                3L -> input.add(5L)
            }
            output.add(next)
        }

        assertEquals(5, output.size)
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L), output)
    }
}