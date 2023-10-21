package io.noties.adapt.sample.explore

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import java.util.Locale

@SuppressLint("StaticFieldLeak")
object ExploreContextConfiguration {
    // can we override config for presented layout?
    // add empty view element with children?...

    // could we limit its applicability?
    //  for example only when building view, but not when view is already build?
    //  !we could validate that view is initialized already
    data class ContextConfiguration(
        val density: Float? = null,
        val locale: String? = null
    )

    private lateinit var view: View
    private var configuration: ContextConfiguration = ContextConfiguration()

    private val provider: (Context) -> View = { error("") }

    val isInitialized: Boolean get() = this::view.isInitialized

    fun <V: View, LP: LayoutParams> ViewElement<V, LP>.density(density: Float?) = this
        .also {
            if (!isInitialized) {
                configuration = configuration.copy(density = density)
            } else {
                error("View is already initialized")
            }
        }

    fun <V: View, LP: LayoutParams> ViewElement<V, LP>.locale(locale: String?) = this
        .also {
            if (!isInitialized) {
                configuration = configuration.copy(locale = locale)
            } else {
                error("View is already initialized")
            }
        }

    // maybe just configuration function that takes it?

    fun init(context: Context): View {
        val view = provider(processContext(context))
        this.view = view
        return view
    }

    private fun processContext(context: Context): Context {
        val density = configuration.density
        val locale = configuration.locale

        if (density == null && locale == null) {
            return context
        }

        // there are some interesting configurations... night, orientation, etc
        val configuration = Configuration(context.resources.configuration)
        if (locale != null) {
            configuration.setLocale(Locale(locale))
        }
        if (density != null) {
            val currentDensity = context.resources.displayMetrics.density
            configuration.densityDpi = (density * (configuration.densityDpi / currentDensity) + 0.5F).toInt()
        }
        return context.createConfigurationContext(configuration)
    }

    // unfortunately this does not work in preview layout, so it is useless
    fun Context.createWithDensity(density: Float): Context {
        val configuration = Configuration(this.resources.configuration)
        val currentDensity = this.resources.displayMetrics.density
        configuration.densityDpi = (density * (configuration.densityDpi / currentDensity) + 0.5F).toInt()
        val c = this.createConfigurationContext(configuration)
        // com.android.layoutlib.bridge.android.BridgeContext
        // STOPSHIP:
        val builder = StringBuilder()
            .append("context=$this")
            .append(", this::class.java=${this::class.java}")
            .append(", resources.configuration=${resources.configuration}")
            .append(", resources.displayMetrics.density=${resources.displayMetrics.density}")
            .append(", configuration.densityDpi=${configuration.densityDpi}")
            .append(", c=${c}")
        if (c == null) {
//        error(builder.toString())
            val field = this::class.java.getDeclaredMethod("getConfiguration")
            val conf = field.invoke(this) as? Configuration
            conf?.densityDpi = (density * (configuration.densityDpi / currentDensity) + 0.5F).toInt()
            return this
        } else {
            return c
        }
    }
}