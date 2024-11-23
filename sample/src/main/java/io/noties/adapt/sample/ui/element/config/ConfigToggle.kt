package io.noties.adapt.sample.ui.element.config

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.Switch
import android.widget.TextView
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.text.body
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textMaxLines
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.element.textStyle
import io.noties.adapt.ui.enabled
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.AdaptUIPreviewLayout
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.reference

class ConfigToggleRef {
    lateinit var view: View
    lateinit var titleView: TextView
    lateinit var toggleView: Checkable

    fun bindTitle(title: String) {
        titleView.text = title
    }

    fun bindChecked(isChecked: Boolean) {
        toggleView.isChecked = isChecked
    }
}

// return `out ViewGroup` so element can have ViewGroup additional extensions
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.ConfigToggle(
    title: String,
    isChecked: Boolean = false,
    onRefReady: (ConfigToggleRef) -> Unit = {},
    onCheckedChanged: (Boolean) -> Unit
): ViewElement<out ViewGroup, LP> {
    @Suppress("NAME_SHADOWING") var isChecked = isChecked

    val ref = ConfigToggleRef()

    return HStack {

        Text()
            .reference(ref::titleView)
            .layout(0, wrap, 1F)
            .textStyle { configLabel() }

        Element { Switch(it) }
            .reference(ref::toggleView)
            .layoutWrap()
            .onView {
                // disable touch interactions, let the whole row be clickable
                it.isClickable = false
            }

    }.indent()
        .padding(leading = 16, trailing = 8)
        .padding(vertical = 12)
        .foregroundDefaultSelectable()
        .onView {
            ref.view = it

            ref.bindTitle(title)
            ref.bindChecked(isChecked)

            onRefReady.invoke(ref)
        }
        .onClick {
            isChecked = !isChecked
            ref.bindChecked(isChecked)
            onCheckedChanged(isChecked)
        }
}

private class PreviewConfigToggle(context: Context, attrs: AttributeSet?) :
    AdaptUIPreviewLayout(context, attrs) {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {
            ConfigToggle(
                "My name is",
                onCheckedChanged = {}
            )
            ConfigToggle(
                title = "Yes, I'm checked",
                isChecked = true,
                onCheckedChanged = {}
            )
            ConfigToggle(
                title = "Yes, I'm checked",
                isChecked = true,
                onCheckedChanged = {}
            ).enabled(false, applyToChildren = true)
        }.indent()
            .preview { it.previewBounds() }
    }
}