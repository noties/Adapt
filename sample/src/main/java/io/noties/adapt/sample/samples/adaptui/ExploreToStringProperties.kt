package io.noties.adapt.sample.samples.adaptui

import kotlin.reflect.KProperty0

object ExploreToStringProperties {

    fun toStringProperties(
        vararg properties: Pair<KProperty0<Any?>, ((Any?) -> Any?)?>
    ): String {
        return properties
            .map { pair ->
                val convert = pair.second ?: { it }
                pair.first.name to convert(pair.first.get())
            }
            .filter { it.second != null }
            .joinToString(", ") {
                "${it.first}=${it.second}"
            }
    }

    class Builder {
        val properties: MutableList<Pair<KProperty0<Any?>, Any?>> = mutableListOf()

        inline fun it(property: KProperty0<Any?>): Builder = this.also {
            properties.add(property to property.get())
        }

        inline fun <T : Any?> it(property: KProperty0<T>, map: (T) -> Any?): Builder = this.also {
            properties.add(property to map(property.get()))
        }

        // bad, requires `invoke` or `this`
        operator fun invoke(property: KProperty0<Any?>): Builder = this.also {
            properties.add(property to property.get())
        }
    }

//    // bad, property might receive any value, no exactly the one
//    fun toStringProperties(
//        properties: List<KProperty0<Any?>>,
//        propertiesMap: List<Pair<KProperty0<Any?>, ((Any?) -> Any?)>>? = null
//    ): String {
//        val p = properties.map { it.name to it.get() }
//        val pm =
//            propertiesMap?.map { it.first.name to it.second.invoke(it.first.get()) } ?: emptyList()
//        return (p + pm)
//            .filter { it.second != null }
//            .joinToString(", ") {
//                "${it.first}=${it.second}"
//            }
//    }

    inline fun toStringProperties(builder: Builder.() -> Unit): String {
        val b = Builder()
        builder(b)
        return b.properties
            .filter { it.second != null }
            .joinToString(", ") {
                "${it.first}=${it.second}"
            }
    }

    // each, we can expose it(), but it does not give type safety (reduces to Any?)
//    inline fun toStringProperties2(builder: ((KProperty0<Any?>, ((Any?) -> Any)) -> Unit) -> Unit): String {
//        val properties: MutableList<Pair<KProperty0<Any?>, Any?>> = mutableListOf()
//        val action = {  }
//        builder(action)
//        return b.properties
//            .filter { it.second != null }
//            .joinToString(", ") {
//                "${it.first}=${it.second}"
//            }
//    }

    private var text: String = "hello"

    fun hey() {
        val s = toStringProperties {
            it(::text)
            it(::text) {
                it.toInt()
            }
//            this(::text)
        }
    }
}