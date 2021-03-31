package io.noties.adapt.sample.processor

data class AdaptSampleHolder(
    val javaClassName: String,
    val id: String,
    val title: String,
    val description: String,
    val tags: List<String>
)