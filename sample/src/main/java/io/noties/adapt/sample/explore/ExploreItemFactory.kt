package io.noties.adapt.sample.explore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.reference
import kotlin.reflect.KClass

// too bad it does not hide original from scope :'(
import kotlin.Long as ItemId

private typealias HEY = String

object ExploreItemFactory {

    fun hey() {
        val MyItem = ItemTypeFactory.builder()
            .input(String::class)
            .ref {
                class Ref {
                    lateinit var textView: TextView
                }
                Ref()
            }
            .onRefReady {
                // do something when view-block exited, thus ref should have all properties referenced
                textView.text
            }
            .view {
                ZStack {
                    Text()
                        .reference(it::textView)
                        .text(context.getString(0))
                }
            }
            .bind {
                ref.textView.text = it
            }
            .build(0L)

    }

    // ItemFactory<ID, INPUT, REF, VIEW, BIND>

    /**
     * @param ID -
     */
    interface ItemTypeFactory<ID, INPUT, REF, VIEW> {
        companion object {
            fun builder(): ItemTypeFactory<Unit, Unit, Unit, Unit> = TODO()
        }

        // TODO: receiver ViewFactory has proper `parent` as the `viewGroup` prop in factory set, so it could be inspected
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
            id: (INPUT) -> ItemId
        ): ItemTypeFactory<ItemId, INPUT, REF, VIEW>

        /**
         * [Item.NO_ID] would be used
         */
        fun noId() = id { Item.NO_ID }

        /**
         * Would assign INPUT.hashCode value as `id`
         */
        fun idHashInput(): ItemTypeFactory<ItemId, INPUT, REF, VIEW>

        // TODO: NO INPUT -> AVAILABLE to build without it, IF HAS INPUT -> required to provide bind (another type argument)
        // optional, bind cannot be omitted
        fun bind(
            bind: ItemFactoryItem.Holder<REF>.(INPUT) -> Unit
        ): ItemTypeFactory<ID, INPUT, REF, VIEW>

        fun <OUT_REF : Any> ref(
            ref: () -> OUT_REF
        ): ItemTypeFactory<ID, INPUT, OUT_REF, VIEW>

        fun onRefReady(ref: REF.() -> Unit): ItemTypeFactory<ID, INPUT, REF, VIEW>
    }

    // REF is not Any, because it can be Unit, but generally speaking if REF is not Unit,
    //  it should be Any (not-null)
    class ItemFactoryItem<REF>(
        id: ItemId
    ) : Item<ItemFactoryItem.Holder<REF>>(id) {

        class Holder<REF>(
            itemView: View,
            val ref: REF
        ) : Item.Holder(itemView)

        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder<REF> {
            TODO("Not yet implemented")
        }

        override fun bind(holder: Holder<REF>) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Returns an invokable function that takes **no input** and returns an instance of [ItemFactoryItem]
     *
     * @available: `create` becomes available when id and view are defined (ID:Long, VIEW:View)
     */
    fun <INPUT, REF : Any> ItemTypeFactory<ItemId, INPUT, REF, out View>.build(): () -> ItemFactoryItem<REF> =
        TODO()

    /**
     * @param staticId id to assign to all created instances, if multiple instances with different
     * ids are required, [ItemTypeFactory.id] or [ItemTypeFactory.idHashInput] could be used.
     */
    fun <INPUT, REF : Any> ItemTypeFactory<Unit, INPUT, REF, out View>.build(
        staticId: ItemId
    ): () -> ItemFactoryItem<REF> = this.id { staticId }.build()

//    /**
//     * Returns an invokable function that takes **INPUT** and returns an instance of [ItemFactoryItem]
//     *
//     * @available: `create` becomes available when id and view are defined (ID:Long, VIEW:View)
//     * @param staticId if provided (not-null) would assign the same id value to all created instances
//     */
//    fun <REF, INPUT : Any> ItemTypeFactory<ItemId, INPUT, REF, out View>.build(
//
//    ): (INPUT) -> ItemFactoryItem<REF> = TODO()
//
//    /**
//     * Returns an invokable function that takes **INPUT** and returns an instance of [ItemFactoryItem]
//     *
//     * @available: `create` becomes available when id and view are defined (ID:Long, VIEW:View)
//     * @param staticId if provided (not-null) would assign the same id value to all created instances
//     */
//    fun <REF, INPUT : Any> ItemTypeFactory<Unit, INPUT, REF, out View>.build(
//        staticId: ItemId
//    ): (INPUT) -> ItemFactoryItem<REF> = this.id { staticId }.build()
}