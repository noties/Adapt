package io.noties.adapt.sample.screen.linear

import io.noties.adapt.Item
import io.noties.adapt.ItemLayoutWrapper
import io.noties.adapt.sample.R

//class LinearWrapper2<H : Item.Holder>(item: Item<H>) : ItemWrapper<H>(item) {
//    override fun recyclerDecoration(recyclerView: RecyclerView): RecyclerView.ItemDecoration? {
//        return object : RecyclerView.ItemDecoration() {
//            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//                // this one will add offsets for all items in recyclerview and not the one that we
//                // have wrapped. To process only wrapped one introduce special checks
//                outRect.set(16, 16, 16, 16)
//            }
//        }
//    }
//}

class LinearWrapper<H : Item.Holder>(item: Item<H>) : ItemLayoutWrapper<H>(R.layout.item_linear_wrapper, item) {
//    override fun recyclerDecoration(recyclerView: RecyclerView): RecyclerView.ItemDecoration? {
//        return object : RecyclerView.ItemDecoration() {
//            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//                // this one will add offsets for all items in recycler-view and not the one that we
//                // have wrapped. To process only wrapped one introduce special checks
//                outRect.set(16, 16, 16, 16)
//            }
//        }
//    }
}