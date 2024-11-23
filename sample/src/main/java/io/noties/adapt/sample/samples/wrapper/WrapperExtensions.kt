package io.noties.adapt.sample.samples.wrapper

import android.graphics.drawable.Drawable
import io.noties.adapt.Item
import io.noties.adapt.ui.util.dip
import io.noties.adapt.util.Edges
import io.noties.adapt.wrapper.BackgroundWrapper
import io.noties.adapt.wrapper.FrameWrapper
import io.noties.adapt.wrapper.IdWrapper
import io.noties.adapt.wrapper.MarginWrapper
import io.noties.adapt.wrapper.OnBindWrapper
import io.noties.adapt.wrapper.OnClickWrapper
import io.noties.adapt.wrapper.PaddingWrapper

@Deprecated("")
fun Item<*>.background(color: Int): Item<*> = wrap(BackgroundWrapper.init(color))

@Deprecated("")
fun Item<*>.background(drawable: Drawable?): Item<*> = wrap(BackgroundWrapper.init(drawable))

@Deprecated("")
fun Item<*>.padding(padding: Int = 16.dip): Item<*> = wrap(PaddingWrapper.all(padding))

@Deprecated("")
fun Item<*>.padding(edges: Edges): Item<*> = wrap(PaddingWrapper.init(edges))

@Deprecated("")
fun Item<*>.margin(margin: Int = 16.dip): Item<*> = wrap(MarginWrapper.all(margin))

@Deprecated("")
fun Item<*>.margin(edges: Edges): Item<*> = wrap(MarginWrapper.init(edges))

@Deprecated("")
fun Item<*>.frame(
    width: Int = FrameWrapper.MATCH_PARENT,
    height: Int = FrameWrapper.WRAP_CONTENT,
    contentGravity: Int = FrameWrapper.NO_GRAVITY
): Item<*> =
    wrap(FrameWrapper.init(width, height, contentGravity))

@Deprecated("")
fun Item<*>.onClick(action: (Item<*>) -> Unit): Item<*> =
    wrap(OnClickWrapper.init(action))

@Deprecated("")
fun Item<*>.id(id: Long): Item<*> = wrap(IdWrapper.init(id))

@Deprecated("")
fun Item<*>.onBind(action: (Item.Holder) -> Unit): Item<*> =
    wrap(OnBindWrapper.init(action))