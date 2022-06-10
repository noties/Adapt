package io.noties.adapt.sample.samples.viewpager

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import io.noties.adapt.Item
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.FILL
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.adaptViewPager
import io.noties.adapt.ui.addChildren
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipToPadding
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Pager
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.setItems
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Corners
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.StatefulShape

@AdaptSample(
    id = "20220610113434",
    title = "ViewPager (androidx.viewpager)",
    description = "Usage with androidx.ViewPager",
    tags = ["viewpager"]
)
class ViewPagerSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_frame

    private val pageWidth = 0.82F

    override fun render(view: View) {
        val container: ViewGroup = view.findViewById(R.id.frame_layout)

        ViewFactory.addChildren(container) {
            VStack {

                Text("Fixed height")
                    .padding(16, 8)
                    .margin(top = 8)
                    .textSize(21)

                Pager()
                    .layout(FILL, 128)
                    .onView(::processViewPager)
                    .adaptViewPager { it.pageWidth(pageWidth) }
                    .setItems(items)
            }
        }
    }

    private val items: List<Item<*>>
        get() {
            return listOf(
                PageItem("The FIRST page here! How it is?"),
                PageItem("The SECOND page here! How it is?"),
                PageItem("The THIRD page here! How it is?"),
                PageItem("The FORTH page here! How it is?"),
                PageItem("The FIFTH page here! How it is?")
            )
        }

    private fun processViewPager(viewPager: ViewPager) {
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
    }

    private class PageItem(
        var text: String
    ) : ElementItem<PageItem.Ref>(hash(text), ::Ref) {

        class Ref {
            lateinit var textView: TextView
        }

        var isSelected = false

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
                .background(StatefulShape.drawable {
                    setSelected(Corners(leadingTop = 24, trailingBottom = 24) {
                        fill(Color.MAGENTA)
                        padding(8)
                    })
                    setDefault(RoundedRectangle(8) {
                        fill(Color.WHITE)
                        padding(8)
                    })
                })
                .elevation(2)
                .clipToPadding(false)
        }

        override fun bind(holder: Holder<Ref>) {
            holder.references.textView.text = text
            holder.itemView().isSelected = isSelected

            holder.itemView().setOnClickListener {
                val adapt = holder.adapt()
                adapt.items()
                    .filterIsInstance<PageItem>()
                    .forEach {
                        val clicked = it == this
                        it.isSelected = clicked
                        if (clicked) {
                            it.text = it.text.uppercase()
                        } else {
                            it.text = it.text.lowercase()
                        }
                        adapt.notifyItemChanged(it)
                    }
            }
        }
    }
}