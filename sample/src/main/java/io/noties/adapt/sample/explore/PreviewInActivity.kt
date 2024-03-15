package io.noties.adapt.sample.explore

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.layoutFill

class PreviewInActivity: Activity() {

    private companion object {
        const val ARG_PREVIEW_CLASS = "pc"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type = try {
            intent.getStringExtra(ARG_PREVIEW_CLASS)
                .also { println("ARG:'$it'") }
                ?.let { Class.forName(it) }
                ?.let { it.getConstructor(Context::class.java, AttributeSet::class.java) }
                ?.let {
                    it.newInstance(this, null) as View
                }
        } catch (t: Throwable) {
            t.printStackTrace(System.err)
            null
        }

        println("type:'$type'")

        if (type != null) {
            setContentView(ViewFactory.createView(this) {
                ZStack {
                    Element { type }
                }.layoutFill()
            })
        }
    }
}