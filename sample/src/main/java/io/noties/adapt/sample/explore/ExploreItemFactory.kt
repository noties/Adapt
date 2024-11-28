package io.noties.adapt.sample.explore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.Item
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.ZStack
import kotlin.reflect.KClass

object ExploreItemFactory {

    fun hey() {
        val MyItem = ItemFactory.builder()
            .view {
                ZStack { }
            }
            .input(String::class)
            .bind {
                // TODO: access to the holder or item-view
            }
            .create()
    }

    interface ItemFactory<VIEW, INPUT, REF> {
        companion object {
            fun builder(): ItemFactory<Unit, Unit, Unit> = TODO()
        }

        fun view(
            view: ViewFactory<LayoutParams>.(parent: ViewGroup) -> ViewElement<out View, out LayoutParams>
        ): ItemFactory<View, INPUT, REF>

        fun inflateView(
            inflateView: (inflater: LayoutInflater, parent: ViewGroup) -> View
        ): ItemFactory<View, INPUT, REF>

        fun <T : Any> input(
            input: KClass<T>
        ): ItemFactory<VIEW, T, REF>

        // TODO: id is special, adapt somehow requires it, but lately with type+id key
        //  there are not much of the issues, especially that most of the items are single-instance
        // normal class would simplify returning self
        fun id(
            id: (INPUT) -> Long
        ): ItemFactory<VIEW, INPUT, REF>

        /**
         * Would assign INPUT.hashCode value as `id`
         */
        fun idHashInput(): ItemFactory<VIEW, INPUT, REF>

        // optional, bind cannot be omitted
        fun bind(
            bind: REF.(INPUT) -> Unit
        ): ItemFactory<VIEW, INPUT, REF>

        fun <OUT_REF : Any> ref(
            ref: () -> OUT_REF
        ): ItemFactory<VIEW, INPUT, OUT_REF>
    }

    class ItemFactoryItem(
        id: Long
    ) : Item<ItemFactoryItem.Holder>(id) {
        class Holder(
            itemView: View
        ) : Item.Holder(itemView)

        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
            TODO("Not yet implemented")
        }

        override fun bind(holder: Holder) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Returns an invokable function that takes **no input** and returns an instance of [ItemFactoryItem]
     *
     * @available: `create` becomes available when view is defined (VIEW:View), so View was provided
     * @param staticId if provided (not-null) would assign the same id value to all created instances
     */
    fun ItemFactory<out View, Unit, *>.create(
        staticId: Long? = null
    ): () -> ItemFactoryItem = TODO()

    /**
     * Returns an invokable function that takes **INPUT** and returns an instance of [ItemFactoryItem]
     *
     * @available: `create` becomes available when view is defined (VIEW:View), so View was provided
     * @param staticId if provided (not-null) would assign the same id value to all created instances
     */
    fun <INPUT : Any?> ItemFactory<out View, INPUT, *>.create(
        staticId: Long? = null
    ): (INPUT) -> ItemFactoryItem = TODO()
}