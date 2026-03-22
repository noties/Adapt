package io.noties.adapt.sample.annotation

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class AdaptSample(
    val id: String,
    val title: String,
    val description: String = "",
    val tags: Array<String> = []
)
