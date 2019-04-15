package io.noties.adapt.sample

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.sample.utils.setTextOrHide


data class Sample(
        val color: Int,
        val shape: Shape,
        val title: String,
        val description: String? = null)


class SampleItem(private val sample: Sample) : Item<SampleItem.Holder>(sample.hashCode().toLong()) {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup) =
            Holder(inflater.inflate(R.layout.item_sample, parent, false))

    override fun render(holder: Holder) {
        holder.title.text = sample.title
        holder.description.setTextOrHide(sample.description)

        val bg = holder.icon.background as? IconDrawable
                ?: IconDrawable().also { holder.icon.background = it }
        bg.update(sample.color, sample.shape)
    }

    override fun recyclerDecoration(recyclerView: RecyclerView): RecyclerView.ItemDecoration? {
        return DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
    }

    class Holder(view: View) : Item.Holder(view) {
        val icon = requireView<View>(R.id.icon)
        val title = requireView<TextView>(R.id.title)
        val description = requireView<TextView>(R.id.description)
    }
}

