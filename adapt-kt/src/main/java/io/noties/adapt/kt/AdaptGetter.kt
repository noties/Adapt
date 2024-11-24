package io.noties.adapt.kt

import io.noties.adapt.Adapt
import io.noties.adapt.Item

fun interface AdaptGetter<T : Item<*>> {
    fun items(): List<T>
}

// `abstract` so we can have inline fun with reified generics
abstract class AdaptGetterBuilder<T : Item<*>> {
    abstract fun build(): AdaptGetter<T>


    fun filter(test: (T) -> Boolean): AdaptGetterBuilder<T> =
        Filter(build(), test)

    inline fun <reified R : T> filterIsInstance(): AdaptGetterBuilder<R> =
        FilterIsInstance(build(), R::class.java)

    fun <R : T> filterIsInstance(type: Class<R>): AdaptGetterBuilder<R> =
        FilterIsInstance(build(), type)

    /**
     * Does a direct cast of returned List. NB! the cast will succeed always
     * but it will throw if on iteration there would be item of different type
     * (crash at runtime). Performant, as it does not do additional iteration
     * on items, but potentially fail at runtime if underlying items contain
     * other type. Use only when underlying collection is known to be on certain type
     */
    fun <R : T> cast(type: Class<R>): AdaptGetterBuilder<R> =
        Cast(build(), type)

    inline fun <reified R : T> cast(): AdaptGetterBuilder<R> =
        Cast(build(), R::class.java)
}

fun <T : Item<*>> Adapt.getter(
    builder: AdaptGetterBuilder<Item<*>>.() -> AdaptGetterBuilder<T>
): AdaptGetter<T> {
    val impl = AdaptGetterBuilderImpl(this)
    return builder(impl).build()
}

inline fun <reified T : Item<*>> Adapt.getter(): AdaptGetter<T> {
    return getter { filterIsInstance<T>() }
}

private class AdaptGetterBuilderImpl(val adapt: Adapt) : AdaptGetterBuilder<Item<*>>() {
    override fun build(): AdaptGetter<Item<*>> {
        return AdaptGetter { adapt.items() }
    }
}

private class Filter<T : Item<*>>(
    val getter: AdaptGetter<T>,
    val test: (T) -> Boolean
) : AdaptGetterBuilder<T>() {
    override fun build(): AdaptGetter<T> {
        return AdaptGetter {
            getter.items()
                .filter(test)
        }
    }
}

@Suppress("FunctionName")
fun <IN : Item<*>, OUT : IN> FilterIsInstance(
    getter: AdaptGetter<IN>,
    type: Class<OUT>
): AdaptGetterBuilder<OUT> = FilterIsInstance(Byte.MIN_VALUE, getter, type)

// do not expose this class (it is used by inline, so should have been visible)
private class FilterIsInstance<IN : Item<*>, OUT : IN>(
    @Suppress("UNUSED_PARAMETER") dummy: Byte,
    val getter: AdaptGetter<IN>,
    val type: Class<OUT>
) : AdaptGetterBuilder<OUT>() {
    override fun build(): AdaptGetter<OUT> {
        return AdaptGetter {
            getter.items()
                .filterIsInstance(type)
        }
    }
}

@Suppress("FunctionName")
fun <IN : Item<*>, OUT : IN> Cast(
    getter: AdaptGetter<IN>,
    type: Class<OUT>
): AdaptGetterBuilder<OUT> = Cast(Byte.MIN_VALUE, getter, type)

private class Cast<IN : Item<*>, OUT : IN>(
    @Suppress("UNUSED_PARAMETER") dummy: Byte,
    val getter: AdaptGetter<IN>,
    @Suppress("unused") val type: Class<OUT>,
) : AdaptGetterBuilder<OUT>() {
    override fun build(): AdaptGetter<OUT> {
        return AdaptGetter {
            @Suppress("UNCHECKED_CAST")
            getter.items() as List<OUT>
        }
    }
}