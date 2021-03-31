package io.noties.adapt.sample.items.wrapper

import io.noties.adapt.ItemWrapper

class PaddingWrapper(val padding: Int, provider: Provider) : ItemWrapper(provider.provide()) {

    override fun bind(holder: Holder) {
        // important to understand where to apply styling, if immutable and static, then `onCreateHolder`
        //  is also possible. In case of a variable arguments, always apply in this `bind` method

        // also important when to call super, before or after applying own styling
        // let's do it before
        holder.itemView()
            .setPadding(padding, padding, padding, padding)

        super.bind(holder)
    }
}