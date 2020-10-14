package io.noties.adapt.sample

data class Sample(
    val name: String,
    val description: CharSequence?,
    val provider: () -> SampleView
)