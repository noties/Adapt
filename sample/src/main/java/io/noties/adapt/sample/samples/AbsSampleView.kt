package io.noties.adapt.sample.samples

import android.app.Activity
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.Sample
import io.noties.adapt.sample.SampleView

abstract class AbsSampleView : SampleView {

    final override fun view(parent: ViewGroup): View {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.view_abs_sample, parent, false)

        fun processSampleView(view: View) {
            val container = view.findViewById<ViewGroup>(R.id.container)
            val sampleView = inflater.inflate(layoutResId, container, false)
            container.addView(sampleView)

            view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View?) {
                    view.removeOnAttachStateChangeListener(this)
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

    abstract val sample: Sample
    abstract val layoutResId: Int
    abstract fun render(view: View)

    companion object {
        @Suppress("DEPRECATION")
        fun text(text: String): CharSequence = Html.fromHtml(text)
    }
}