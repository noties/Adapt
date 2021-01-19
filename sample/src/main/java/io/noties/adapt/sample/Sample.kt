package io.noties.adapt.sample

data class Sample(
    val name: String,
    val description: CharSequence?,
    val tags: List<String>,
    val provider: () -> SampleView
)