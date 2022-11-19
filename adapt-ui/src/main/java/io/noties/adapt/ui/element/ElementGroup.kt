package io.noties.adapt.ui.element

import android.content.Context
import android.view.ViewGroup
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

/**
 * `PLP` represent _parent_ LayoutParams
 * `CLP` represents _children_ LayoutParams of created view-group
 */
@Suppress("FunctionName")
fun <VG : ViewGroup, PLP : LayoutParams, CLP : LayoutParams> ViewFactory<PLP>.ElementGroup(
    provider: (Context) -> VG,
    configure: (VG) -> Unit,
    children: ViewFactory<CLP>.() -> Unit
): ViewElement<VG, PLP> = ViewElement<VG, PLP> {
    val viewGroup = provider(it)
    configure(viewGroup)
    ViewFactory.addChildren(viewGroup, children)
    viewGroup
}.also { add(it) }

@Suppress("FunctionName")
fun <VG : ViewGroup, PLP : LayoutParams, CLP : LayoutParams> ViewFactory<PLP>.ElementGroup(
    provider: (Context) -> VG,
    children: ViewFactory<CLP>.() -> Unit
): ViewElement<VG, PLP> = ElementGroup(provider, {}, children)