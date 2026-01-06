package io.noties.adapt.sample

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.items.ControlItem
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.color.yellow
import io.noties.adapt.sample.ui.dimen.appBarHeight
import io.noties.adapt.sample.ui.test
import io.noties.adapt.sample.util.SampleUtil
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.App
import io.noties.adapt.ui.app.dimen.Dimens
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.imageScaleType
import io.noties.adapt.ui.element.imageTint
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textEllipsize
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textMaxLines
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.preview.PreviewLayout
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.util.activity

// open public constructor, so class can be created via reflection
abstract class SampleView constructor() {
    open val context: Context get() = App.context

    open val sample: Sample by lazy(LazyThreadSafetyMode.NONE) {
        SampleUtil.samples.firstOrNull { it.javaClassName == this::class.java.name }
            ?: error(
                "Sample is not found for this class instance. " +
                        "Make sure there is `@AdaptSample` annotation present " +
                        "and annotation processing is being run. class:'${this::class.java.name}' $this"
            )
    }

    fun createView(context: Context): View {
        ItemGenerator.reset()

        val sample = this.sample
        val view = ViewFactory.createView(context) {
            VStack {
                // app bar
                ZStack {

                    Image(R.drawable.ic_arrow_back_24)
                        .layout(Dimens.appBarHeight, Dimens.appBarHeight)
                        .layoutGravity { leading }
                        .imageTint { text }
                        .imageScaleType { centerInside }
                        .background { Circle() }
                        .foregroundDefaultSelectable()
                        .clipToOutline()
                        .onClick { onBack() }

                    Text(sample.name)
                        .textSize { 18 }
                        .textColor { text }
                        .textGravity { center }
                        .textMaxLines(2)
                        .textEllipsize { end }
                        .textBold()
                        .layoutFill()
                        .layoutMargin(horizontal = Dimens.appBarHeight)
                        .onClick { onAppBarTitleClick() }
                        // NB! the screenshot testing is using LayoutLib and thus
                        //  would report as a real-device in preview
//                        .preview {
//                            it.text(
//                                "Pretty long description name that is going " +
//                                        "to take some space around"
//                            )
//                        }
                        .test {
                            // return proper value for tests
                            it.text(sample.name)
                        }

                    Image(R.drawable.ic_info_24)
                        .layout(Dimens.appBarHeight, Dimens.appBarHeight)
                        .layoutGravity { trailing }
                        .imageTint { text }
                        .imageScaleType { centerInside }
                        .background { Circle() }
                        .foregroundDefaultSelectable()
                        .clipToOutline()
                        .onClick { onInfo() }

                }.indent()
                    .layout(fill, Dimens.appBarHeight)

                ZStack {}
                    .layoutFill()
                    .onView {
                        val view = createContentView(it)
                        val current = view.layoutParams
                        if (current == null) {
                            view.layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                        }
                        it.addView(view)
                    }
            }.layoutFill()
        }
        onViewCreated(view)
        return view
    }

    protected abstract fun createContentView(parent: ViewGroup): View

    open fun onViewCreated(view: View) = Unit

    protected open fun onBack() {
        context.activity?.onBackPressed()
    }

    private fun onInfo() {
        TODO()
    }

    private fun onAppBarTitleClick() {
        // copy sample-id on title click
        val id = sample.id
        val manager = context.getSystemService(ClipboardManager::class.java) ?: return
        manager.setPrimaryClip(ClipData.newPlainText("", id))
    }

    companion object {

        fun initSampleItems(
            adapt: Adapt,
            processItem: (Item<*>) -> Item<*> = { it },
            onAddingNewItems: (items: List<Item<*>>) -> List<Item<*>> = { it },
            onShuffle: () -> Unit = {
                adapt.setItems(adapt.items().shuffled())
            }
        ) {
            val initialItems = ItemGenerator.next(0)
                .toMutableList()
                .run {
                    add(
                        ControlItem(
                            {
                                val items = adapt.items()

                                val newItems = ItemGenerator.next(items.size)
                                    .map(processItem)
                                    .let {
                                        items.toMutableList().apply {
                                            addAll(it)
                                        }
                                    }

                                adapt.setItems(onAddingNewItems(newItems))

                            },
                            onShuffle
                        )
                    )
                    this.map(processItem)
                        .toMutableList()
                }

            adapt.setItems(onAddingNewItems(initialItems))
        }
    }
}

abstract class SampleViewLayout : SampleView() {
    abstract val layoutResId: Int

    abstract fun render(view: View)

    override fun createContentView(parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutResId, parent, false)
        render(view = view)
        return view
    }
}

abstract class SampleViewUI : SampleView() {
    abstract fun ViewFactory<LayoutParams>.body()

    override fun createContentView(parent: ViewGroup): View {
        return ViewFactory.createView(parent.context) {
            body()
        }
    }
}

abstract class PreviewSampleView(
    context: Context,
    attrs: AttributeSet?
) : PreviewLayout(context, attrs) {
    abstract val sampleView: SampleView

    init {
        io.noties.adapt.sample.App.mock(context)
    }

    override fun createView(context: Context, parent: PreviewLayout): View {
        val view = sampleView.createView(context)
        return ViewFactory.createView(context, parent) {
            VStack {

                View()
                    .layout(fill, 24)
                    .background {
                        Rectangle {
                            fill(Gradient.linear {
                                edges { leading to trailing }
                                    .setColors(*Sample.gradientColors(sampleView.sample))
                            })
                        }
                    }

                Element { view }
                    .layoutFill()

            }.indent()
                .layoutFill()
        }
    }
}

@Preview
private class PreviewSampleViewRoot(context: Context, attrs: AttributeSet?) :
    PreviewLayout(context, attrs) {
    override fun createView(context: Context, parent: PreviewLayout): View {
        return object : SampleView() {
            override val sample: Sample
                get() = Sample.empty().copy(name = "Just a name")

            override fun createContentView(parent: ViewGroup): View {
                return ViewFactory.createView(parent.context) {
                    View()
                        .layoutFill()
                        .backgroundColor { yellow }
                }
            }
        }.createView(context)
    }
}