package io.noties.adapt.sample.explore

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.sample.explore.ExploreItem2.ItemFactoryBuilder.Companion.build
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.text

object ExploreItem2 {
    // restrict an item to certain LP

    interface CreateLayoutParams {
        fun createLayoutParams(parent: ViewGroup): LayoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    interface CreateHolder<H : Item.Holder> {
        fun createHolder(parent: ViewGroup): H
    }

    interface BindHolder<H : Item.Holder> {
        fun bind(holder: H)
    }

    interface Item<H : Item.Holder> : CreateLayoutParams, CreateHolder<H>, BindHolder<H> {
        open class Holder(val itemView: View)

//        fun createHolder(parent: ViewGroup): H
//        fun bind(holder: H)

//        open fun createLayoutParams(parent: ViewGroup) =
//            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

//        abstract fun createLayoutParams(parent: ViewGroup): LayoutParams
    }

    interface RestrictToViewGroup : CreateLayoutParams {
        override fun createLayoutParams(parent: ViewGroup): LayoutParams {
            return super.createLayoutParams(parent)
        }
    }

//    interface ProvideLayoutParams: CreateLayoutParams {
//        override fun createLayoutParams(parent: ViewGroup): LayoutParams {
//            return super.createLayoutParams(parent)
//        }
//
//        fun check(parent: ViewGroup): Boolean
//
//        fun
//    }

    interface ElementCreateRef<R : Any> {
        fun createRef(): R
    }

    interface ElementBody<LP : LayoutParams, R : Any> {
        fun ViewFactory<LP>.body(ref: R)
    }

    interface ElementItem<R : Any> :
        Item<ElementItem.Holder<R>>,
        ElementBody<LayoutParams, R>,
        ElementCreateRef<R> {

        // no need to extend it (or should we give such ability? not very frequently needed really)
        class Holder<R : Any>(
            itemView: View,
            val ref: R
        ) : Item.Holder(itemView)

        override fun createHolder(parent: ViewGroup): Holder<R> {
            // ViewFactory can be different (provide own)
            val ref = createRef()
            val view = ViewFactory.newView(parent)
                // LP obviously can be provided already
                .layoutParams(createLayoutParams(parent))
                .create {
                    body(ref)
                }
            return Holder(view, ref)
        }
    }

    interface AlreadyCreatedHolder<H : Item.Holder> : CreateHolder<H> {
        override fun createHolder(parent: ViewGroup): H {
            TODO("Not yet implemented")
        }
    }

    interface AlreadyBoundHolder<H : Item.Holder> : BindHolder<H> {
        override fun bind(holder: H) {
            TODO("Not yet implemented")
        }
    }

    interface ReflectiveCreateRef<R : Any> : ElementCreateRef<R> {
        override fun createRef(): R {
            TODO("Not yet implemented")
        }
    }

    class MyItem : ElementItem<MyItem.Ref>, ReflectiveCreateRef<MyItem.Ref> {
        class Ref

        override fun bind(holder: ElementItem.Holder<Ref>) {
            TODO("Not yet implemented")
        }

        override fun ViewFactory<LayoutParams>.body(ref: Ref) {
            TODO("Not yet implemented")
        }
    }

//    sealed class BodyHolder
//    data object HasBody: BodyHolder()
//
//    interface TestBuilder<BODY: BodyHolder> {
//        companion object {
//            fun start(): TestBuilder<BodyHolder> = TODO()
//
//            fun TestBuilder<HasBody>.build(): Int {
//                return 0
//            }
//        }
//
//        fun id(): TestBuilder<BODY>
//
//        fun name(): TestBuilder<BODY>
//
//        fun body(): TestBuilder<HasBody>
//    }
//
//    fun heyhey() {
//        val builder = TestBuilder.start()
//        builder
//            .id()
//            .name()
//            .body()
//            .build()
//    }

    // what if we define factory in runtime, specifying all requirements
    //  and implementations, via normal builder pattern, which in turn returns
    //  factory which can be used to create items

    // this is very hard to maintain (manipulate types for any change..)
    //  in the end the experience is better than trying to create a structure to extend/subclass
    //  more realiable and reproducible, compositoon over inheritance
    // also debuggin might be hard, as it would be not clear which is missing

    // required configurations
    sealed class RequiredBody
    data object HasBody : RequiredBody()

    sealed class RequiredRef
    data object HasRef : RequiredRef()


    interface ItemFactoryBuilder<INPUT : Any, LAYOUT_PARAMS : LayoutParams, REF : Any, BODY : RequiredBody, HOLDER : io.noties.adapt.Item.Holder> {

        companion object {
            fun create(): ItemFactoryBuilder<Unit, LayoutParams, Unit, RequiredBody, io.noties.adapt.Item.Holder> {
                TODO()
            }

            fun <INPUT : Any, LP : LayoutParams> ItemFactoryBuilder<INPUT, LP, Unit, HasBody, *>.build(): (INPUT) -> io.noties.adapt.Item<*> {
                TODO()
            }

            // unfortunately cannot use the same name, nor change to return
            //  different function when IN is Unit
            fun <LP : LayoutParams> ItemFactoryBuilder<Unit, LP, Unit, HasBody, *>.build(): () -> io.noties.adapt.Item<*> {
                TODO()
            }
        }

//        // changes type
//        fun <OUT_IN : Any> input(type: KClass<OUT_IN>): ItemFactoryBuilder<OUT_IN, LAYOUT_PARAMS, REF>

        // by default hashes input and uses it as ID
        fun id(provider: (INPUT) -> Long): ItemFactoryBuilder<INPUT, LAYOUT_PARAMS, REF, BODY, HOLDER>

        fun <OUT_LP : LayoutParams> layoutParams(provider: (ViewGroup) -> OUT_LP): ItemFactoryBuilder<INPUT, OUT_LP, REF, BODY, HOLDER>

        fun <OUT_REF : Any> ref(provider: () -> OUT_REF): ItemFactoryBuilder<INPUT, LAYOUT_PARAMS, OUT_REF, BODY, HOLDER>

        // required, make it generic param? so we can create a build extension when it is specified
        //  actually, here we provide different implementations: to create from view, from layout params, from element, etc
        fun body(provider: ViewFactory<LAYOUT_PARAMS>.(REF) -> Unit): ItemFactoryBuilder<INPUT, LAYOUT_PARAMS, REF, HasBody, HOLDER>

        fun bind(provider: (REF) -> Unit): ItemFactoryBuilder<INPUT, LAYOUT_PARAMS, REF, BODY, HOLDER>

        // bind is optional, so it is available here
        fun <OUT_INPUT : Any> bind(provider: OUT_INPUT.(REF) -> Unit): ItemFactoryBuilder<OUT_INPUT, LAYOUT_PARAMS, REF, BODY, HOLDER>

        // build will be available only after required configuration is done
//        fun build(): (INPUT) -> io.noties.adapt.Item<*>

        // unfortunately cannot use the same name, nor change to return
        //  different function when IN is Unit
//        fun buildNoInput(): () -> io.noties.adapt.Item<*>
    }

    fun hey() {
        class Ref {
            lateinit var textView: TextView
        }

        val factory = ItemFactoryBuilder.create()

        val item = factory
            .body {
                Text()
                    .text("Hello")
            }
            .build()

        val item2 = factory
            .body {
                Text("")
            }
            .bind {

            }
    }
}