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
    // initially it was CharSequence with Html.fromHtml parsing, but it is failing in tests
    //  java.lang.UnsatisfiedLinkError: 'int android.os.SystemProperties.native_get_int(java.lang.String, int)'
    //  it turns out Html.fromHtml would try to obtain Resources in order to render some tags (b, strong) :'(
    val description: String?,
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