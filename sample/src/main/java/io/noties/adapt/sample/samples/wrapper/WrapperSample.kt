package io.noties.adapt.sample.samples.wrapper

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.adapt.viewgroup.TransitionChangeHandler
import io.noties.debug.Debug

@AdaptSample(
    id = "20220220212847",
    title = "ItemWrapper padding background frame",
    description = "With <em>similar</em> semantics to SwiftUI",
    tags = ["wrapper"]
)
class WrapperSample : SampleView() {

    override val layoutResId: Int
        get() = R.layout.view_sample_view_group

    override fun render(view: View) {
        val viewGroup = view.findViewById<ViewGroup>(R.id.view_group)
        val adapt = AdaptViewGroup.init(
            viewGroup,
            TransitionChangeHandler.create()
        )

        val first = ItemGenerator.next(0).first()
        val items = mutableListOf(
            first
        )

        repeat(4) {
            items.last()
                // `frame` is required to create another view, as padding and background
                //      by default operate on the same view as original Item
                .frame().padding().background(ItemGenerator.nextColor())
                .also(items::add)
        }

        // make last item clickable
        items.removeLast()
            .onClick {
                Debug.i("Click on last item, shuffling")
                adapt.setItems(adapt.items().shuffled())
            }
            .also(items::add)

        adapt.setItems(items)

        // represents a similar to SwiftUI semantics
        //  NB! by default wrapper operates on view created by an item, `frame` is required
        //  to use another view
//        val item = ItemGenerator.next(0).first()
//            // those 2 (padding and background) modify _original_ view of item
//            .padding(8.dip).background(0x40FF0000)
//            // `frame` wraps original view into a FrameLayout
//            .frame().padding(16.dip).background(0x4000ffff)
//            // as background and padding operate on current view their order does not matter
//            .frame().background(0x400000ff).padding(24.dip)
//            .frame().padding(32.dip).background(0xFFff00ff.toInt())

        // without extensions:
//        val item = ItemGenerator.next(0).first()
//            .wrap(PaddingWrapper.init(16.dip))
//            .wrap(BackgroundWrapper.init(0x40FF0000))
//            .wrap(FrameWrapper.init())
//            .wrap(PaddingWrapper.init(16.dip))
//            .wrap(BackgroundWrapper.init(0x4000ffff))
//            .wrap(FrameWrapper.init())
//            .wrap(PaddingWrapper.init(16.dip))
//            .wrap(BackgroundWrapper.init(0x400000ff))

//        adapt.setItems(Collections.singletonList(item))
    }
}