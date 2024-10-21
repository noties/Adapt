package io.noties.adapt.sample.explore

object ExploreTypedAdapt {

    // used only to obtain items, NO SET (MUTATION)
    //  is created with an adapt instance as receives it
//    class TypedAdapt<T> {
//        companion object {
//            // 2 variants:
//            //  - relaxed, wrapped adapt can be any types, but this one would only return of a single
//            fun <T> only(): TypedAdapt<T> =
//        }
//    }

//    val getter = adapt.getter { filter<MyItem>() }
//    val getter = adapt.getter { filter { it: Item<*> -> (it as? MyItem).hello } }
//    val getter = adapt.getter { cast<MyItem>() }
//
//    // can be used for validation that all items are of this type?
//    //  seems this must be done in some other place, this is not it
//    class TypedGetterAdapt<T : Any>(
//        val type: KClass<T>,
//        val adapt: Adapt
//    ) {
//        fun items(): List<T> = adapt.items()
//            .mapNotNull {
//                if (type.isInstance(it)) {
//                    @Suppress("UNCHECKED_CAST")
//                    it as T
//                } else {
//                    null
//                }
//            }
//    }

//    interface AdaptGetter<T : Item<*>> {
//        fun items(): List<T>
//    }
//
//    // TODO: what if we could chain it further? allow chaining it further
//    class AdaptGetterBlock(val adapt: Adapt) : AdaptGetter<Item<*>> {
//        override fun items(): List<Item<*>> {
//            return adapt.items()
//        }
//    }
//
//    fun <T : Item<*>> Adapt.getter(
//        block: AdaptGetterBlock.() -> AdaptGetter<T>
//    ): AdaptGetter<T> = block(AdaptGetterBlock(this))
//
//    // utility to filterIsInstance
//    inline fun <reified T: Item<*>> Adapt.getter() = FilterIsInstance(
//        AdaptGetterBlock(this),
//        T::class.java
//    )
//
//    // useless?? as we need to put in signture or provide 2 types
//    inline fun <T : Item<*>, reified R : T> AdaptGetter<T>.filterIsInstance(): AdaptGetter<R> =
//        FilterIsInstance(this, R::class.java)
//
//    fun <T : Item<*>, R : T> AdaptGetter<T>.filterIsInstance(type: Class<R>): AdaptGetter<R> =
//        FilterIsInstance(this, type)
//
//    fun <T : Item<*>> AdaptGetter<T>.filter(filter: (T) -> Boolean): AdaptGetter<T> =
//        Filter(this, filter)
//
//    fun <T : Item<*>, R : T> AdaptGetter<T>.cast(type: Class<R>): AdaptGetter<R> =
//        Cast(this, type)
//
//    // what for?
////    fun <T: Item<*>, R: Item<*>> AdaptGetter<T>.map(map: (T) -> R)
//
//    class FilterIsInstance<IN : Item<*>, OUT : IN>(
//        private val getter: AdaptGetter<IN>,
//        private val type: Class<OUT>
//    ) : AdaptGetter<OUT> {
//        override fun items(): List<OUT> {
//            return getter.items()
//                .filterIsInstance(type)
//        }
//    }
//
//    class Filter<IN : Item<*>>(
//        private val getter: AdaptGetter<IN>,
//        private val filter: (IN) -> Boolean
//    ) : AdaptGetter<IN> {
//        override fun items(): List<IN> {
//            return getter.items().filter(filter)
//        }
//    }
//
//    // NB! each must use getter, otherwise chiaing would be gone, as adapt is used directly
//    class Cast<IN : Item<*>, OUT : IN>(
//        private val getter: AdaptGetter<IN>,
//        private val type: Class<OUT>
//    ) : AdaptGetter<OUT> {
//        override fun items(): List<OUT> {
//            @Suppress("UNCHECKED_CAST")
//            return getter.items() as List<OUT>
//        }
//    }
//
//    private abstract class MyItem(val isSelected: Boolean) : ElementItemNoRef(0L)
//    private abstract class MyChildItem : MyItem(false)
//
//    fun hey(adapt: Adapt) {
//        val getter = adapt.getter { filterIsInstance<_, MyItem>() }
//        val getter2 = adapt.getter { filterIsInstance(MyItem::class.java) }
//
//        val getter3 = adapt.getter {
//            cast(MyItem::class.java)
//                .filter { it.isSelected }
//                .filterIsInstance<_, MyChildItem>()
//        }
//
//        val getter4 = adapt.getter<MyItem>()
//    }
}