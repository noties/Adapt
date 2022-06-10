package io.noties.adapt.sample.samples.viewpager

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.FILL
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipToPadding
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.viewpager.AdaptViewPager

@AdaptSample(
    id = "20220610113434",
    title = "ViewPager (androidx.viewpager)",
    description = "Usage with androidx.ViewPager",
    tags = ["viewpager"]
)
class ViewPagerSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_viewpager

    override fun render(view: View) {
        val pageWidth = 0.82F
        val viewPager = view.findViewById<ViewPager>(R.id.view_pager)
        val adapt = AdaptViewPager.init(viewPager) {
            it.pageWidth(pageWidth)
        }

        viewPager.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val w = viewPager.width
                if (w > 0) {
                    viewPager.viewTreeObserver.removeOnPreDrawListener(this)

                    viewPager.setPadding(
                        Math.round(w * (1F - pageWidth)),
                        viewPager.paddingTop,
                        viewPager.paddingRight,
                        viewPager.paddingBottom
                    )
                    viewPager.clipChildren = false
                    viewPager.clipToPadding = false
                }
                return true
            }
        })

        val onClick: (PageItem) -> Unit = { item ->
            adapt.items()
                .filterIsInstance<PageItem>()
                .forEach {
                    if (it == item) {
                        it.text = it.text.uppercase()
                    } else {
                        it.text = it.text.lowercase()
                    }
                    adapt.notifyItemChanged(it)
                }

        }

        val items = listOf(
            PageItem("The FIRST page here! How it is?", onClick),
            PageItem("The SECOND page here! How it is?", onClick),
            PageItem("The THIRD page here! How it is?", onClick),
            PageItem("The FORTH page here! How it is?", onClick),
            PageItem("The FIFTH page here! How it is?", onClick)
        )

        adapt.setItems(items)
    }

    private class PageItem(
        var text: String,
        private val onClick: (PageItem) -> Unit
    ) : ElementItem<PageItem.Ref>(hash(text), ::Ref) {
        class Ref {
            lateinit var textView: TextView
        }

        override fun ViewFactory<ViewGroup.LayoutParams>.body(references: Ref) {
            HStack {

                View()
                    .background(Circle {
                        fill(Color.YELLOW)
                    })
                    .layout(64, 64)

                Text()
                    .textSize(21)
                    .textColor(Color.BLACK)
                    .textFont(fontStyle = Typeface.BOLD)
                    .margin(leading = 8)
                    .reference(references::textView)

            }.layout(FILL, FILL)
                .padding(16)
                .background(RoundedRectangle(8) {
                    fill(Color.WHITE)
                    padding(8)
                })
                .elevation(2)
                .clipToPadding(false)
        }

        override fun bind(holder: Holder<Ref>) {
            holder.references.textView.text = text
            holder.itemView().setOnClickListener { onClick(this) }
        }
    }
}