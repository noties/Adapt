package io.noties.adapt.sample

import io.noties.adapt.sample.ui.color.emeraldGreen
import io.noties.adapt.sample.ui.color.naplesYellow
import io.noties.adapt.sample.ui.color.salmonRed
import io.noties.adapt.sample.ui.color.steelBlue
import io.noties.adapt.ui.app.color.Colors
import kotlin.random.Random

data class Sample(
    val id: String,
    val name: String,
    val description: CharSequence?,
    val tags: List<String>,
    val javaClassName: String
) {
    companion object {
        fun empty() = Sample(
            id = "",
            name = "",
            description = "",
            tags = emptyList(),
            javaClassName = ""
        )
    }
}

fun Sample.Companion.gradientColors(sample: Sample): IntArray {
    val seed = sample.id.hashCode()
    return intArrayOf(
        Colors.salmonRed,
        Colors.steelBlue,
        Colors.naplesYellow,
        Colors.emeraldGreen
    ).also {
        it.shuffle(Random(seed))
    }
}