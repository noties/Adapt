package io.noties.adapt.sample.viewdsl

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.GravityInt
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Item
import io.noties.debug.Debug
import java.util.*
import kotlin.reflect.KMutableProperty0

// TODO: onClickListener, actually, do not expose, we should focus on _static_ building...
// TODO: View (just view for bg, or space)

class ViewFactory<LP : LayoutParams> {

    fun <V : View> ViewElement<V, LP>.layout(
        block: LP.() -> Unit
    ): ViewElement<V, LP> = this.also {
        it.layoutBlocks.add(block)
    }

    fun <V : View> ViewElement<V, LP>.layout(
        width: Int,
        height: Int,
        block: (LP.() -> Unit)? = null
    ): ViewElement<V, LP> = this.also {
        it.layoutBlocks.add {
            this.width = width
            this.height = height
        }
        if (block != null) {
            it.layoutBlocks.add(block)
        }
    }

    fun <V : View, LLP : LinearLayout.LayoutParams> ViewElement<V, LLP>.layoutWeight(
        weight: Float
    ): ViewElement<V, LLP> {
        return this.also {
            it.layoutBlocks.add { this.weight = weight }
        }
    }

    @JvmName("linearLayoutGravity")
    fun <V : View, LLP : LinearLayout.LayoutParams> ViewElement<V, LLP>.layoutGravity(
        gravity: Int
    ): ViewElement<V, LLP> {
        return this.also {
            it.layoutBlocks.add { this.gravity = gravity }
        }
    }

    @JvmName("frameLayoutGravity")
    fun <V : View, FLP : FrameLayout.LayoutParams> ViewElement<V, FLP>.layoutGravity(
        gravity: Int
    ): ViewElement<V, FLP> {
        return this.also {
            it.layoutBlocks.add { this.gravity = gravity }
        }
    }

    var elements: MutableList<ViewElement<out View, LP>> = mutableListOf()

    companion object {

        inline fun <G : ViewGroup, reified LP : LayoutParams> addChildren(
            g: G,
            children: ViewFactory<LP>.() -> Unit
        ) {

            val factory = ViewFactory<LP>()
            children(factory)

            factory.elements.forEach { el ->
                @Suppress("UNCHECKED_CAST")
                el as ViewElement<View, LP>

                val view = el.provider(g.context)

                // now layoutParams are generated
                g.addView(view)

                val lp = view.layoutParams as LP
                el.layoutBlocks.forEach { it(lp) }
                el.viewBlocks.forEach { it(view) }

                view.requestLayout()
            }
        }


    }
}

@Suppress("PropertyName", "unused")
val ViewFactory<*>.FILL: Int
    get() {
        return LayoutParams.MATCH_PARENT
    }

@Suppress("PropertyName", "unused")
val ViewFactory<*>.WRAP: Int
    get() {
        return LayoutParams.WRAP_CONTENT
    }

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onView(
    block: V.() -> Unit
): ViewElement<V, LP> {
    return this.also {
        it.viewBlocks.add(block)
    }
}

@JvmName("holdInNotNull")
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.holdIn(
    property: KMutableProperty0<V>
): ViewElement<V, LP> = onView {
    property.set(this)
}

@JvmName("holdInNullable")
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.holdIn(
    property: KMutableProperty0<V?>
): ViewElement<V, LP> = onView {
    property.set(this)
}

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.id(
    id: Int
): ViewElement<V, LP> = onView {
    setId(id)
}

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.background(
    @ColorInt color: Int
): ViewElement<V, LP> = onView {
    setBackgroundColor(color)
}

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.padding(
    padding: Int
): ViewElement<V, LP> = onView {
    setPadding(padding, padding, padding, padding)
}

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.Text(
    text: String? = null
): ViewElement<TextView, LP> {
    // not only return, but we also need to add it to internal collection
    return ViewElement<TextView, LP> {
        TextView(it).also { tv -> tv.text = text }
    }.also(elements::add)
}

data class TextStyle(
    val size: Int,
    @ColorInt val color: Int,
    @GravityInt val gravity: Int,
    val font: Typeface?
)

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textSize(
    size: Int,
): ViewElement<V, LP> {
    return onView {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
    }
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textColor(
    @ColorInt color: Int
): ViewElement<V, LP> {
    return onView {
        setTextColor(color)
    }
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textGravity(
    @GravityInt gravity: Int
): ViewElement<V, LP> {
    return onView {
        setGravity(gravity)
    }
}

class ViewElement<V : View, LP : LayoutParams>(
    val provider: (Context) -> V
) {
    var viewBlocks: MutableList<V.() -> Unit> = mutableListOf()
    var layoutBlocks: MutableList<LP.() -> Unit> = mutableListOf()
}

@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.VStack(
    @GravityInt gravity: Int = Gravity.CENTER_HORIZONTAL,
    children: ViewFactory<LinearLayout.LayoutParams>.() -> Unit
): ViewElement<LinearLayout, LP> {
    return ViewElement<LinearLayout, LP> {
        LinearLayout(it).also { ll ->
            ll.orientation = LinearLayout.VERTICAL
            ll.gravity = gravity
            ViewFactory.addChildren(ll, children)
        }
    }.also(elements::add)
}

@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.HStack(
    @GravityInt gravity: Int = Gravity.CENTER_VERTICAL,
    children: ViewFactory<LinearLayout.LayoutParams>.() -> Unit
): ViewElement<LinearLayout, LP> {
    return ViewElement<LinearLayout, LP> {
        LinearLayout(it).also { ll ->
            ll.orientation = LinearLayout.HORIZONTAL
            ll.gravity = gravity
            ViewFactory.addChildren(ll, children)
        }
    }.also(elements::add)
}

@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.ZStack(
    children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
): ViewElement<FrameLayout, LP> {
    return ViewElement<FrameLayout, LP> {
        FrameLayout(it).also { fl ->
            ViewFactory.addChildren(fl, children)
        }
    }.also(elements::add)
}

abstract class ViewItem<H>(id: Long): Item<ViewItem.Holder<H>>(id) {
    class Holder<H>(view: View, val holder: H): Item.Holder(view)

    abstract fun ViewFactory<LayoutParams>.body(holder: H)

    // TODO: name differently, we already have holder
    abstract fun createHolder(): H

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder<H> {
        val factory = ViewFactory<LayoutParams>()
        val h = createHolder()
        factory.body(h)

        // ensure single element
        if (factory.elements.size != 1) {
            throw IllegalStateException("Unexpected state, `body` must return exactly one element")
        }

        @Suppress("UNCHECKED_CAST")
        val root = factory.elements[0] as ViewElement<View, LayoutParams>

        val view = root.provider(parent.context)
        val lp = generateDefaultLayoutParams(parent)

        view.layoutParams = lp

        root.layoutBlocks.forEach { it(lp) }
        root.viewBlocks.forEach { it(view) }

        return Holder(view, h)
    }

    override fun bind(holder: Holder<H>) {
        bind(holder.holder)
    }

    open fun bind(h: H) {
    }

    // TODO: recycler is compile only, ensure no exception is thrown if missing
    private fun generateDefaultLayoutParams(parent: ViewGroup): LayoutParams {
        val manager = (parent as? RecyclerView)?.layoutManager ?: return LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        return manager.generateDefaultLayoutParams()
    }
}

class MyItem2: ViewItem<MyItem2.Views>(0) {
    class Views {
        lateinit var rightView: TextView
    }

    override fun ViewFactory<LayoutParams>.body(holder: Views) {
        VStack {
            Text("Header!")
                .layout(WRAP, WRAP)
                .layoutGravity(Gravity.CENTER_HORIZONTAL)
                .background(Color.BLUE)
                .onView {
                    // raw _platform_ values
                    setTextColor(Color.WHITE)
                    textSize = 48F
                    setPadding(48, 48, 48, 48)
                }
            Text("Under the header!")
                .textSize(24)
                .textColor(Color.MAGENTA)
                .textGravity(Gravity.CENTER_HORIZONTAL)
                .padding(16)
                .background(Color.YELLOW)
            HStack {
                Text("Left")
                    .layoutWeight(1F)
                    .layout(0, FILL)
                    .background(Color.RED)
                    .textGravity(Gravity.CENTER)
                Text("Right")
                    .layoutWeight(2F)
                    .layout(0, FILL)
                    .background(Color.GREEN)
                    .textGravity(Gravity.CENTER)
                    .holdIn(holder::rightView)
            }
        }.layout(FILL, WRAP)
    }

    override fun createHolder(): Views = Views()

    override fun bind(h: Views) {
        h.rightView.text = "BOUND!!! at: ${Date()}"
    }
}

class MyItem3: ViewItem<Unit>(0) {
    override fun ViewFactory<LayoutParams>.body(holder: Unit) {
        VStack {
            Text("Header!")
                .layout(WRAP, WRAP)
                .layoutGravity(Gravity.CENTER_HORIZONTAL)
                .background(Color.BLUE)
                .onView {
                    // raw _platform_ values
                    setTextColor(Color.WHITE)
                    textSize = 48F
                    setPadding(48, 48, 48, 48)
                }
            Text("Under the header!")
                .textSize(24)
                .textColor(Color.MAGENTA)
                .textGravity(Gravity.CENTER_HORIZONTAL)
                .padding(16)
                .background(Color.YELLOW)
            HStack {
                // it is possible to have conditional branches
                if (true) {
                    Text("Another one inside if branch")
                }
                Text("Left")
                    .layoutWeight(1F)
                    .background(Color.RED)
                Text("Right")
                    .layoutWeight(2F)
                    .background(Color.GREEN)
            }
        }.layout(FILL, WRAP)
    }

    override fun createHolder() = Unit
}

class MyItem : Item<MyItem.MyHolder>(1L) {
    class MyHolder(view: View) : Item.Holder(view)

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): MyHolder {
        val views = Views()
        val factory = ViewFactory<LayoutParams>()
        factory.body(views)

        // ensure single element
        if (factory.elements.size != 1) {
            throw IllegalStateException("Unexpected state, `body` must return exactly one element")
        }

        @Suppress("UNCHECKED_CAST")
        val root = factory.elements[0] as ViewElement<View, LayoutParams>

        val view = root.provider(parent.context)
        val lp = generateDefaultLayoutParams(parent)

        Debug.e("views:%s", views.rightView)

        view.layoutParams = lp

        root.layoutBlocks.forEach { it(lp) }
        root.viewBlocks.forEach { it(view) }

        return MyHolder(view)
    }

    private fun generateDefaultLayoutParams(parent: ViewGroup): LayoutParams {
        val manager = (parent as? RecyclerView)?.layoutManager ?: return LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        return manager.generateDefaultLayoutParams()
    }

    private class Views {
        lateinit var rightView: TextView
        var headerView: TextView? = null
    }

    // todo: make all int literals Dip (width, height, padding, margin)
    private fun ViewFactory<LayoutParams>.body(views: Views) {
        VStack {
            Text("Header!")
                .layout(WRAP, WRAP)
                .layoutGravity(Gravity.CENTER_HORIZONTAL)
                .background(Color.BLUE)
                .onView {
                    // raw _platform_ values
                    setTextColor(Color.WHITE)
                    textSize = 48F
                    setPadding(48, 48, 48, 48)
                }
                .holdIn(views::headerView)
            Text("Under the header!")
                .textSize(24)
                .textColor(Color.MAGENTA)
                .textGravity(Gravity.CENTER_HORIZONTAL)
                .padding(16)
                .background(Color.YELLOW)
            HStack {
                Text("Left")
                    .layoutWeight(1F)
                    .background(Color.RED)
                Text("Right")
                    .layoutWeight(2F)
                    .background(Color.GREEN)
                    .holdIn(views::rightView)
            }
        }.layout(FILL, WRAP)
    }

    override fun bind(holder: MyHolder) {
        // no op for now
    }
}