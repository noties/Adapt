package io.noties.adapt.sample

data class Sample(
    val id: String,
    val name: String,
    val description: CharSequence?,
    val tags: List<String>,
    val javaClassName: String
)