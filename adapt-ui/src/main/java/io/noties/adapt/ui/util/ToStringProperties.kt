package io.noties.adapt.ui.util

import kotlin.reflect.KProperty0

@JvmInline
internal value class ToStringPropertiesBuilder(
    val properties: MutableList<Pair<String, Any?>>
) {
    @Suppress("NOTHING_TO_INLINE")
    inline fun it(vararg properties: KProperty0<Any?>) {
        this.properties.addAll(properties.map { it.name to it.get() })
    }

    inline fun <T : Any?> it(property: KProperty0<T>, block: (T) -> Any?) {
        properties.add(property.name to block(property.get()))
    }
}

// Function that accepts class properties and concats them into a string
//  all null values will be omitted, all strings would be wrapped inside single quotes 'like this'
internal inline fun toStringProperties(
    block: ToStringPropertiesBuilder.() -> Unit
): String {
    val builder = ToStringPropertiesBuilder(mutableListOf())
    block(builder)
    return builder.properties
        .filter { it.second != null }
        .joinToString(", ") {
            val value = it.second
                ?.takeIf { s -> s is String }
                ?.let { s -> "'$s'" }
                ?: it.second
            "${it.first}=${value}"
        }
}

internal inline fun toStringPropertiesDefault(
    self: Any,
    block: ToStringPropertiesBuilder.() -> Unit
): String = toStringPropertiesDefault(self::class.java.simpleName, block)

internal inline fun toStringPropertiesDefault(
    name: String,
    block: ToStringPropertiesBuilder.() -> Unit
): String {
    val properties = toStringProperties(block)
    return "${name}($properties)"
}