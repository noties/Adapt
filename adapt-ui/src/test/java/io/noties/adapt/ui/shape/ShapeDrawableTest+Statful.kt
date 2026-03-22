package io.noties.adapt.ui.shape

import io.noties.adapt.ui.state.ViewState
import org.junit.Assert
import org.junit.Test

class ShapeDrawableStatefulTest {

    private data class Input(
        val name: String,
        val filterValues: Set<Int>?,
        val states: List<IntArray>,
        val expectedResults: List<Boolean>,
        val expectedCallbackStates: List<Set<Int>>
    ) {
        override fun toString(): String {
            return "Input(name='$name', filterValues=$filterValues, states=[${states.joinToString { it.contentToString() }}], expectedResults=$expectedResults, expectedCallbackStates=$expectedCallbackStates)"
        }
    }

    @Suppress("LocalVariableName")
    @Test
    fun testStatefulScenarios() {
        val _1 = 1
        val _2 = 2
        val _3 = 3
        val _4 = 4

        val inputs = listOf(
            Input(
                name = "noFilter_initialAndSameState",
                filterValues = null,
                states = listOf(
                    intArrayOf(_1, _2),
                    intArrayOf(_1, _2)
                ),
                expectedResults = listOf(true, false),
                expectedCallbackStates = listOf(setOf(_1, _2))
            ),
            Input(
                name = "noFilter_sameElementsDifferentOrder",
                filterValues = null,
                states = listOf(
                    intArrayOf(_1, _2),
                    intArrayOf(_2, _1)
                ),
                expectedResults = listOf(true, false),
                expectedCallbackStates = listOf(setOf(_1, _2))
            ),
            Input(
                name = "filterWithValues_matchingAndNonMatching",
                filterValues = setOf(_1, _2),
                states = listOf(
                    intArrayOf(_1, _3, _4),
                    intArrayOf(_1, _3)
                ),
                expectedResults = listOf(true, false),
                expectedCallbackStates = listOf(setOf(_1))
            ),
            Input(
                name = "filterWithValues_toEmpty",
                filterValues = setOf(_1, _2),
                states = listOf(
                    intArrayOf(_1, _3, _4),
                    intArrayOf(_3, _4)
                ),
                expectedResults = listOf(true, true),
                expectedCallbackStates = listOf(setOf(_1), emptySet())
            ),
            Input(
                name = "filterEmptyRawValues_noCallbacks",
                filterValues = emptySet(),
                states = listOf(
                    intArrayOf(_1),
                    intArrayOf(_1, _2),
                    intArrayOf()
                ),
                expectedResults = listOf(false, false, false),
                expectedCallbackStates = emptyList()
            ),
            Input(
                name = "transitionPreviousState",
                filterValues = setOf(_1, _2),
                states = listOf(
                    intArrayOf(_1),
                    intArrayOf(_1, _2),
                    intArrayOf(_2, _1)
                ),
                expectedResults = listOf(true, true, false),
                expectedCallbackStates = listOf(setOf(_1), setOf(_1, _2))
            ),
            Input(
                name = "noFilter_emptyInitialState",
                filterValues = null,
                states = listOf(
                    intArrayOf()
                ),
                expectedResults = listOf(false),
                expectedCallbackStates = emptyList()
            ),
            Input(
                name = "filterNonEmpty_toEmptyState",
                filterValues = setOf(_1, _2),
                states = listOf(
                    intArrayOf(_1),
                    intArrayOf()
                ),
                expectedResults = listOf(true, true),
                expectedCallbackStates = listOf(setOf(_1), emptySet())
            )
        )

        for (input in inputs) {
            val callbacks = mutableListOf<Set<Int>>()
            val stateful = ShapeDrawable.Stateful(
                filter = input.filterValues?.let { ViewState(rawValues = it) },
                onStatefulStateChange = { callbacks += it.rawValues }
            )

            val results = input.states.map { stateful.onStateChanged(it) }

            Assert.assertEquals(input.toString(), input.expectedResults, results)
            Assert.assertEquals(input.toString(), input.expectedCallbackStates, callbacks)
        }
    }
}
