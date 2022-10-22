package io.noties.adapt.ui.testutil

import org.mockito.Mockito
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.KStubbing
import org.mockito.kotlin.withSettings
import org.mockito.stubbing.Answer

@Suppress("unused", "SpellCheckingInspection")
inline fun <reified T : Any> mockt(
    defaultAnswer: Answer<Any>? = null,
    verboseLogging: Boolean = false,
    block: KStubbing<T>.(T) -> Unit = {}
): T {
    return Mockito.mock(
        T::class.java,
        withSettings(
            defaultAnswer = defaultAnswer,
            verboseLogging = verboseLogging
        )
    ).apply { KStubbing(this).block(this) }!!
}

// returns single value, throws if there are more
val <T : Any?> KArgumentCaptor<T>.value: T
    get() = if (this.allValues.size == 1) lastValue else throw IllegalStateException(
        "Multiple values found: $allValues"
    )