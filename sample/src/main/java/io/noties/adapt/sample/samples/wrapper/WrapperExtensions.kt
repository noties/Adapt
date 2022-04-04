package io.noties.adapt.sample.samples.wrapper

import android.graphics.drawable.Drawable
import io.noties.adapt.Item
import io.noties.adapt.sample.util.dip
import io.noties.adapt.util.Edges
import io.noties.adapt.wrapper.*

fun Item<*>.background(color: Int): Item<*> = wrap(BackgroundWrapper.init(color))
fun Item<*>.background(drawable: Drawable?): Item<*> = wrap(BackgroundWrapper.init(drawable))

fun Item<*>.padding(padding: Int = 16.dip): Item<*> = wrap(PaddingWrapper.all(padding))
fun Item<*>.padding(edges: Edges): Item<*> = wrap(PaddingWrapper.init(edges))

fun Item<*>.margin(margin: Int = 16.dip): Item<*> = wrap(MarginWrapper.all(margin))
fun Item<*>.margin(edges: Edges): Item<*> = wrap(MarginWrapper.init(edges))

fun Item<*>.frame(
    width: Int = FrameWrapper.MATCH_PARENT,
    height: Int = FrameWrapper.WRAP_CONTENT,
    contentGravity: Int = FrameWrapper.NO_GRAVITY
): Item<*> =
    wrap(FrameWrapper.init(width, height, contentGravity))

fun Item<*>.onClick(action: (Item<*>) -> Unit): Item<*> =
    wrap(OnClickWrapper.init(action))

fun Item<*>.id(id: Long): Item<*> = wrap(IdWrapper.init(id))

fun Item<*>.onBind(action: (Item.Holder) -> Unit): Item<*> =
    wrap(OnBindWrapper.init(action))