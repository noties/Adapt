package io.noties.adapt.sample.ui.element.config

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.R
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.color.textDisabled
import io.noties.adapt.sample.ui.color.textSecondary
import io.noties.adapt.sample.ui.text.body
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textMaxLines
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.element.textStyle
import io.noties.adapt.ui.enabled
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.onElementView
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.AdaptUIPreviewLayout
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.state.imageTintWithState

class ConfigPickerRef {
    lateinit var view: View
    lateinit var titleView: TextView
    lateinit var selectedView: TextView

    fun bindTitle(title: String) {
        titleView.text = title
    }

    fun bindSelected(selected: String?) {
        selectedView.text = selected
    }
}

// return `out ViewGroup` so element can have ViewGroup additional extensions,
@Suppress("FunctionName")
fun <T : Any, LP : LayoutParams> ViewFactory<LP>.ConfigPicker(
    title: String,
    values: List<T>,
    selected: T? = null,
    onRefReady: (ConfigPickerRef) -> Unit = {},
    toString: (T) -> String = { it.toString() },
    onSelectedChanged: (T) -> Unit
): ViewElement<out ViewGroup, LP> {
    @Suppress("NAME_SHADOWING") var selected = selected

    val ref = ConfigPickerRef()

    return HStack {

        Text()
            .reference(ref::titleView)
            .layoutWrap()
            .textStyle { configLabel() }
            .layoutMargin(trailing = 12)

        Text()
            .reference(ref::selectedView)
            .layout(0, wrap, 1F)
            .textStyle {
                configLabel(
                    textColor = { textSecondary },
                )
            }
            .textGravity { trailing }
            .layoutMargin(trailing = 2)

        Image(R.drawable.ic_chevron_right_24)
            .layout(24, 24)
            .imageTintWithState {
                enabled = textSecondary
                default = textDisabled
            }
            .layoutMargin(trailing = 6)

    }.indent()
        .padding(leading = 16, top = 12, bottom = 12)
        .foregroundDefaultSelectable()
        .onView {
            ref.view = it

            ref.bindTitle(title)
            ref.bindSelected(selected?.let { toString(it) })

            onRefReady.invoke(ref)
        }
        .onElementView { el ->
            el.onClick {
                // show picker
                showPicker(
                    el.view.context,
                    title,
                    selected,
                    values,
                    toString
                ) {
                    selected = it
                    ref.bindSelected(toString(it))
                    onSelectedChanged(it)
                }
            }
        }
}

private fun <T : Any> showPicker(
    context: Context,
    title: String,
    selected: T?,
    values: List<T>,
    toString: (T) -> String,
    onSelected: (T) -> Unit
) {

    val adapter = AdaptListView.create(context) {
        it.areAllItemsEnabled(true)
    }
    val items = values
        .map { ConfigPickerSelectableItem(toString(it)) }
    adapter.setItems(items)

    AlertDialog.Builder(context)
        .setTitle(title)
        .setSingleChoiceItems(
            adapter.adapter(),
            if (selected != null) values.indexOf(selected) else -1
        ) { d, position ->
            d.dismiss()
            val target = values[position]
            if (target != selected) {
                onSelected(target)
            }
        }
        .setNeutralButton("Cancel") { d, _ -> d.dismiss() }
        .show()
}

private class ConfigPickerSelectableItem(
    val item: String
) : ElementItem<ConfigPickerSelectableItem.Ref>(hash(item), { Ref() }) {

    class Ref {
        lateinit var textView: TextView
    }

    override fun bind(holder: Holder<Ref>) {
        holder.ref.textView.text = item
    }

    override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
        Text()
            .reference(ref::textView)
            .layout(fill, wrap)
            .textSize { body }
            .textColor { text }
            .textMaxLines(1)
            .padding(horizontal = 16, vertical = 12)
    }
}

@Preview
private class PreviewConfigPicker(context: Context, attrs: AttributeSet?) :
    AdaptUIPreviewLayout(context, attrs) {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {
            ConfigPicker(
                title = "Some value",
                values = listOf("First", "Second"),
                onSelectedChanged = {}
            )
            ConfigPicker(
                title = "Some value",
                values = listOf("First", "Second"),
                selected = "First",
                onSelectedChanged = {}
            )
            ConfigPicker(
                title = "Some value",
                values = listOf("First", "Second"),
                selected = "First hjsfhljsdfhg shjkfg lkjsdfgjysg fgsdjkl fglsjkd fg",
                onSelectedChanged = {}
            )

            ConfigPicker(
                title = "I am disabled",
                values = listOf("First", "Second"),
                selected = "None",
                onSelectedChanged = {}
            ).enabled(false, applyToChildren = true)

        }.indent()
            .layoutFill()
            .preview { it.previewBounds() }
    }
}