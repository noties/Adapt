package io.noties.adapt.sample.samples.viewpager

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import io.noties.adapt.Item
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleViewLayout
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.magenta
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.sample.ui.text.title3
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.adaptViewPager
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipToPadding
import io.noties.adapt.ui.element.AdaptPagerWrapContent
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ViewPagerOnPageChangeListener
import io.noties.adapt.ui.element.pagerOnPageChangedListener
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.setItems
import io.noties.adapt.ui.shape.CircleShape
import io.noties.adapt.ui.shape.Corners
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.state.backgroundWithState
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.viewgroup.TransitionChangeHandler
import io.noties.debug.Debug
import kotlin.math.roundToInt

@AdaptSample(
    id = "20220610113434",
    title = "ViewPager (androidx.viewpager)",
    description = "Usage with androidx.ViewPager",
    tags = ["viewpager"]
)
class ViewPagerSample : SampleViewLayout() {

    override val layoutResId: Int = R.layout.view_sample_frame

    private val pageWidth = 0.82F

    override fun render(view: View) {
        val container: ViewGroup = view.findViewById(R.id.frame_layout)

        ViewFactory.addChildren(container) {
            VStack {

                Text("Fixed height")
                    .padding(16, 8)
                    .layoutMargin(top = 8)
                    .textSize(21)

                Element(::ViewPager)
                    .layout(fill, 128)
                    .onView(::processViewPager)
                    .adaptViewPager()
                    .setItems(items)

                Text("Wrap height")

                val adapt = AdaptPagerWrapContent()
                    .pagerOnPageChangedListener(object : ViewPagerOnPageChangeListener() {
                        override fun onPageSelected(position: Int) {
                            Debug.i("selected:$position count:$pagesCount vp:$viewPager")
                        }
                    })
                    .onView(::processViewPager)
                    .layout(fill, wrap)
                    .adaptViewPager { it.pageWidth(pageWidth) }
                    .setItems(items)

                Text("At bottom")
                    .padding(16)
                    .textGravity(Gravity.center)
                    .foregroundDefaultSelectable()
                    .onClick {
                        (items.dropLast(1) + PageItem("CHANGED!!"))
                            .also {
                                adapt.adapt.setItems(it)
                            }
                    }
            }
        }
    }

    private val items: List<Item<*>>
        get() {
            return listOf(
                PageItem("The FIRST page here! How it is?"),
                PageItem("The SECOND page here! How it is?"),
                PageItem("The THIRD page here! How it is? DFGHJKdsgfhjskdgfhjdsk sfghjkdsgfhjkdsgfhjds"),
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
                        (w * (1F - pageWidth)).roundToInt(),
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
            lateinit var textElement: ViewElement<out TextView, *>
        }

        var isSelected = false

        override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
            HStack {

                View()
                    .background(CircleShape {
                        fill(Color.YELLOW)
                    })
                    .layout(64, 64)

                Text()
                    .textSize { title3 }
                    .textColor { black }
                    .textBold()
                    .layoutMargin(leading = 8)
                    .reference(ref::textView)
                    .reference(ref::textElement)
//                    .also { references.textElement = it }

            }.indent()
                .layoutFill()
                .padding(16)
                .backgroundWithState {
                    selected = Corners(leadingTop = 24, trailingBottom = 24) {
                        fill { magenta }
                        padding(8)
                    }
                    default = RoundedRectangle(8) {
                        fill { white }
                        padding(8)
                    }
                }
                .elevation(2)
                .clipToPadding(false)
        }

        override fun bind(holder: Holder<Ref>) {
            holder.ref.textView.text = text
            holder.itemView().isSelected = isSelected

            val parent = holder.itemView().parent as? ViewGroup
            val handler = TransitionChangeHandler.createTransitionOnParent()
            parent?.also(handler::begin)

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
            parent?.also(handler::end)
        }
    }
}