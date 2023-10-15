package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.ElementViewFactory
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.util.CachingContextWrapper
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.withAlphaComponent
import io.noties.debug.Debug
import kotlin.random.Random
import kotlin.random.nextInt

@AdaptSample(
    id = "20231014222418",
    title = "UI, ElementViewFactory contextWrapper",
    description = "Provide context wrapper to customize Context",
    tags = ["adapt-ui", "context"]
)
class AdaptUIFactoryContextSample : SampleView() {

    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {
        val viewGroup = view as ViewGroup

        fun render(dpi: Int, fontScale: Float) {
            Debug.i("dpi:$dpi fontScale:$fontScale")
            // it is possible to add caching, so each element does not create a wrapper
            // `contextWrapper` is called for each function, so it potentially could affect view-building time
            ElementViewFactory.contextWrapper =
                CachingContextWrapper { createContext(it, dpi, fontScale) }

            // otherwise `contextWrapper` is a simple function that takes Context as argument
            //  and returns Context (this is the default implementation):
            // ElementViewFactory.contextWrapper = { it }

            viewGroup.removeAllViews()

            ViewFactory.addChildren(viewGroup) {
                VStack {
                    Text("Hey! How are you?!")
                        .onView { Debug.i("Text.context:${it.context}") }
                        .background(Colors.black.withAlphaComponent(0.2F))
                        .padding(16)
                        .ifAvailable(23) {
                            it.foregroundDefaultSelectable()
                        }
                        .onClick {
                            val newDpi = Random.nextInt(100..1000)
                            val newFontScale = 0.2F + (Random.nextFloat() * 5)
                            render(newDpi, newFontScale)
                        }
                }.indent()
                    .onView {
                        Debug.i("VStack.context:${it.context}")

                        val _16System = 16.dip
                        val _16Density = (16 * (it.context.resources.displayMetrics.density + 0.5F)).toInt()
                        Debug.i("system:$_16System density:$_16Density")
                    }
                    .padding(24)
            }

            // reset after this view is created
            //  This function is intended for tests and showcases, in a normal application
            //  it should not be used
            ElementViewFactory.reset()

            // also clear the cache, otherwise the same context would be reused
            //  done just for the purpose of showcasing, should not be used
            CachingContextWrapper.cache.clear()
        }

        Debug.i("Original context:${view.context}")

        render(600, 1F)
    }

    private fun createContext(context: Context, dpi: Int, fontScale: Float): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.densityDpi = dpi
        configuration.fontScale = fontScale
        return context.createConfigurationContext(configuration)
    }
}