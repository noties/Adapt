package io.noties.adapt.ui.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.Item
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

/**
 * To check if an item is instance of created factory use specific functions:
 * ```kotlin
 * val MyItem = ItemTypeFactory.builder().build()
 * val item = MyItem()
 * item.isInstanceOf(MyItem)
 * ```
 * ```kotlin
 * // for input, input must be provided explicitly with each check
 * val MyItemWithInput = ItemTypeFactory.builder()
 *   .input(String::class)
 *   .build()
 *
 * val item = MyInputWithInput("hello")
 *
 * // will return true, checks only the type, not contents
 * val result = item.isInstanceOf("world", MyItemWithInput)
 * ```
 */
interface ItemTypeFactory<ID, INPUT, REF, VIEW> {
    companion object {
        fun builder(): ItemTypeFactory<Unit, Unit, Unit, Unit> =
            ItemTypeFactoryDefault(Unit, Unit, Unit, Unit)
    }

    fun view(
        view: ViewFactory<LayoutParams>.(ref: REF) -> ViewElement<out View, out LayoutParams>
    ): ItemTypeFactory<ID, INPUT, REF, View>

    fun inflateView(
        inflateView: (inflater: LayoutInflater, parent: ViewGroup) -> View
    ): ItemTypeFactory<ID, INPUT, REF, View>

    // conditional seems cannot be done here and instead should be done by extensions
    //  for example, if input is specified, then bind is required,
    //  but current bind - if it is not Unit should not removed.. but, let's not modify other
    //  outputs, just specify `build` extension what is required (for example,
    //  <INPUT=Unit, then when BIND=Unit it is possible to build the item)
    fun <T : Any> input(
        input: KClass<T>
    ): ItemTypeFactory<ID, T, REF, VIEW>

    fun id(
        id: (INPUT) -> Long
    ): ItemTypeFactory<Long, INPUT, REF, VIEW>

    /**
     * [Item.NO_ID] would be used
     */
    fun noId() = id { Item.NO_ID }

    /**
     * Would assign INPUT.hashCode value as `id`
     */
    fun idHashInput(): ItemTypeFactory<Long, INPUT, REF, VIEW>

    // TO-DO: NO INPUT -> AVAILABLE to build without it, IF HAS INPUT -> required to provide bind (another type argument)
    // :too much, type signature effort is significant

    // optional, bind cannot be omitted
    fun bind(
        bind: ItemFactoryItem.Holder<REF>.(INPUT) -> Unit
    ): ItemTypeFactory<ID, INPUT, REF, VIEW>

    fun <OUT_REF : Any> ref(
        ref: () -> OUT_REF
    ): ItemTypeFactory<ID, INPUT, OUT_REF, VIEW>

    fun onRefReady(
        ref: REF.() -> Unit
    ): ItemTypeFactory<ID, INPUT, REF, VIEW>
}

/**
 * Returns an invokable function that takes **no input** and returns an instance of [ItemFactoryItem]
 *
 * @available: `create` becomes available when id and view are defined (ID:Long, VIEW:View)
 */
fun <INPUT, REF> ItemTypeFactory<
        Long,
        INPUT,
        REF,
        View
        >.build(): (INPUT) -> ItemFactoryItem<INPUT, REF> where INPUT : Any {
    (this as? ItemTypeFactoryDefault) ?: error("Only `ItemTypeFactoryDefault` is supported")
    // generate new id and use it as type-checker (as viewType in adapter)
    val viewType = viewTypesGenerator.incrementAndGet()
    return { input ->
        createItem(
            id = id!!(input),
            input = input,
            viewType = viewType
        )
    }
}

private val viewTypesGenerator = AtomicInteger()

fun <INPUT, REF> ItemTypeFactory<
        Long,
        INPUT,
        REF,
        View
        >.build(): () -> ItemFactoryItem<INPUT, REF> where INPUT : Unit {
    (this as? ItemTypeFactoryDefault) ?: error("Only `ItemTypeFactoryDefault` is supported")
    // generate new id and use it as type-checker (as viewType in adapter)
    val viewType = viewTypesGenerator.incrementAndGet()
    return {
        createItem(
            // this is weird, if null is passed , there is a crash with parameter
            //  is null when it is defined as non-null, but `id` have parameter nullable - Any?, seems
            //  there is something weird from compiler, as it does not detect that
            id = id!!.invoke(Unit),
            // no input, Unit, is null
            input = Unit as INPUT,
            viewType = viewType
        )
    }
}

private fun <INPUT, REF> ItemTypeFactoryDefault<
        Long,
        INPUT,
        REF,
        View
        >.createItem(id: Long, input: Any?, viewType: Int): ItemFactoryItem<INPUT, REF> {
    return ItemFactoryItem(
        id = id,
        input = input as INPUT,
        viewType = viewType,
        onCreateHolder = { inflater, parent ->
            val ref = ref?.invoke() as REF
            ItemFactoryItem.Holder(
                itemView = view!!.invoke(inflater, parent, ref as? Any ?: Unit),
                ref = ref
            )
        },
        onBind = {
            bind?.invoke(it as ItemFactoryItem.Holder<Any>, input)
        },
        onRefReady = {
            onRefReady?.invoke(this as Any)
        }
    )
}

/**
 * @param staticId id to assign to all created instances, if multiple instances with different
 * ids are required, [ItemTypeFactory.id] or [ItemTypeFactory.idHashInput] could be used.
 */
fun <INPUT, REF> ItemTypeFactory<
        Unit,
        INPUT,
        REF,
        View
        >.build(
    staticId: Long
): (INPUT) -> ItemFactoryItem<INPUT, REF> where INPUT : Any = this
    .id { staticId }
    .build()

fun <INPUT, REF> ItemTypeFactory<
        Unit,
        INPUT,
        REF,
        View
        >.build(
    staticId: Long
): () -> ItemFactoryItem<INPUT, REF> where INPUT : Unit = this
    .id { staticId }
    .build()

// REF is not Any, because it can be Unit, but generally speaking if REF is not Unit,
//  it should be Any (not-null)
class ItemFactoryItem<INPUT, REF>(
    id: Long,
    // auto generated viewType for each created builder
    val viewType: Int,
    val input: INPUT,
    val onCreateHolder: (inflater: LayoutInflater, parent: ViewGroup) -> Holder<REF>,
    val onBind: (holder: Holder<REF>) -> Unit,
    val onRefReady: REF.() -> Unit
) : Item<ItemFactoryItem.Holder<REF>>(id) {

    class Holder<REF>(
        itemView: View,
        val ref: REF
    ) : Item.Holder(itemView)

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder<REF> {
        return onCreateHolder(inflater, parent)
            .also {
                onRefReady(it.ref)
            }
    }

    override fun bind(holder: Holder<REF>) {
        onBind(holder)
    }

    fun isInstanceOf(item: () -> ItemFactoryItem<INPUT, REF>): Boolean {
        val other = item()
        return viewType == other.viewType
    }

    // so we can check against other items without casting to the REF
    fun <INPUT> isInstanceOf(input: INPUT, item: (INPUT) -> ItemFactoryItem<*, *>): Boolean {
        val other = item(input)
        return viewType == other.viewType
    }
}

class ItemTypeFactoryDefault<ID, INPUT, REF, VIEW>(
    id: ID,
    input: INPUT,
    ref: REF,
    view: VIEW
) : ItemTypeFactory<ID, INPUT, REF, VIEW> {

    var id: ((Any?) -> Long)? = null
    var ref: (() -> Any)? = null
    var view: ((inflater: LayoutInflater, parent: ViewGroup, ref: Any) -> View)? = null
    var bind: (ItemFactoryItem.Holder<Any>.(Any?) -> Unit)? = null
    var onRefReady: (Any.() -> Unit)? = null

    override fun view(
        view: ViewFactory<LayoutParams>.(ref: REF) -> ViewElement<out View, out LayoutParams>
    ): ItemTypeFactory<ID, INPUT, REF, View> {
        this.view = { inflater, parent, ref ->
            val factory = ViewFactory.createView(inflater.context, parent) {
                view(this, ref as REF)
            }
            factory
        }
        return this as ItemTypeFactory<ID, INPUT, REF, View>
    }

    override fun inflateView(
        inflateView: (inflater: LayoutInflater, parent: ViewGroup) -> View
    ): ItemTypeFactory<ID, INPUT, REF, View> {
        this.view = { inflater, parent, ref ->
            inflateView(inflater, parent)
        }
        return this as ItemTypeFactory<ID, INPUT, REF, View>
    }

    override fun <T : Any> input(
        input: KClass<T>
    ): ItemTypeFactory<ID, T, REF, VIEW> {
        return this as ItemTypeFactory<ID, T, REF, VIEW>
    }

    override fun idHashInput(): ItemTypeFactory<Long, INPUT, REF, VIEW> {
        return id { it.hashCode().toLong() }
    }

    override fun <OUT_REF : Any> ref(
        ref: () -> OUT_REF
    ): ItemTypeFactory<ID, INPUT, OUT_REF, VIEW> {
        this.ref = ref
        return this as ItemTypeFactory<ID, INPUT, OUT_REF, VIEW>
    }

    override fun onRefReady(
        ref: REF.() -> Unit
    ): ItemTypeFactory<ID, INPUT, REF, VIEW> {
        this.onRefReady = ref as Any.() -> Unit
        return this
    }

    override fun bind(
        bind: ItemFactoryItem.Holder<REF>.(INPUT) -> Unit
    ): ItemTypeFactory<ID, INPUT, REF, VIEW> {
        this.bind = bind as (ItemFactoryItem.Holder<Any>.(Any?) -> Unit)
        return this
    }

    override fun id(
        id: (INPUT) -> Long
    ): ItemTypeFactory<Long, INPUT, REF, VIEW> {
        this.id = id as ((Any?) -> Long)
        return this as ItemTypeFactory<Long, INPUT, REF, VIEW>
    }

    // cannot change types with this, it is always current ones
//    private fun create(
//        id: ID = this.id,
//        input: INPUT = this.input,
//        ref: REF = this.ref,
//        view: VIEW = this.view,
//        bind: (ItemFactoryItem.Holder<REF>.(INPUT) -> Unit)? = this.bind,
//    ) = ItemTypeFactoryDefault(
//        id = id,
//        input = input,
//        ref = ref,
//        view = view,
//        bind = bind
//    )
}