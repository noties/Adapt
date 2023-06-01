package io.noties.adapt.sample

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.sample.items.ControlItem
import io.noties.adapt.sample.samples.adaptui.Colors
import io.noties.adapt.sample.util.SampleUtil
import io.noties.adapt.sample.util.html
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.imageTint
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textLetterSpacing
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.debug.Debug

abstract class SampleView {

    private lateinit var _sample: Sample
    val sample: Sample
        get() = _sample

    lateinit var context: Context

    fun view(sample: Sample, parent: ViewGroup): View {

        val sourceUrl = SampleUtil.sourceCodeUrl(sample)

        Debug.i("sample:'${sample.id}' source-url:$sourceUrl")

        this._sample = sample
        this.context = parent.context

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.view_sample, parent, false)

        fun processSampleView(view: View) {
            val container = view.findViewById<ViewGroup>(R.id.container)
            val sampleView = inflater.inflate(layoutResId, container, false)
            container.addView(sampleView)

            view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View?) {
                    view.removeOnAttachStateChangeListener(this)
                    ItemGenerator.reset()
                    render(sampleView)
                }

                override fun onViewDetachedFromWindow(v: View?) = Unit
            })
        }

        fun processAppBar(view: View) {
            val container = view.findViewById<View>(R.id.app_bar_container)
            container.findViewById<View>(R.id.app_bar_back).setOnClickListener {
                (it.context as? Activity)?.onBackPressed()
            }
            val title = container.findViewById<TextView>(R.id.app_bar_title)
            ViewElement.create(title)
                .text(sample.name.html())
                .textLetterSpacing(-0.05F)
                .onClick { onAppBarTitleClick(sample) }
                .render()

            val code = container.findViewById<ImageView>(R.id.app_bar_code)
            ViewElement.create(code)
                .imageTint(Colors.black)
                .background {
                    RoundedRectangle(12) {
                        fill(Colors.white)
                        padding(8)
                    }
                }
                .clipToOutline()
                .ifAvailable(Build.VERSION_CODES.M) {
                    it.foregroundDefaultSelectable()
                }
                .onClick { onAppBarCodeClick(sourceUrl) }
                .render()
        }

        processAppBar(view)
        processSampleView(view)

        return view
    }

    abstract val layoutResId: Int

    abstract fun render(view: View)

    private fun onAppBarTitleClick(sample: Sample) {
        // copies sample id
        val manager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
        val data = ClipData.newPlainText("sample-id", sample.id)
        manager.setPrimaryClip(data)
    }

    private fun onAppBarCodeClick(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val picker = Intent.createChooser(intent, "Sample source code")
        context.startActivity(picker)
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