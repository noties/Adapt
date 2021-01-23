package io.noties.adapt.sample

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.sample.items.ControlItem

abstract class SampleView {

    private lateinit var _sample: Sample
    val sample: Sample
        get() = _sample

    fun view(sample: Sample, parent: ViewGroup): View {

        this._sample = sample

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
            container.findViewById<TextView>(R.id.app_bar_title).text = sample.name
        }

        processAppBar(view)
        processSampleView(view)

        return view
    }

    abstract val layoutResId: Int

    abstract fun render(view: View)

    protected fun initialItems(adapt: Adapt): List<Item<*>> = initialItems {
        ControlItem.init(adapt)
    }

    protected fun initialItems(onAdded: () -> Unit, onShuffled: () -> Unit) = initialItems {
        ControlItem.init(onAdded, onShuffled)
    }

    protected fun initialItems(provider: () -> ControlItem): List<Item<*>> =
        ItemGenerator.next(0).toMutableList().apply {
            add(provider())
        }
}