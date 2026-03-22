package io.noties.adapt.ui.element

import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.widget.AdaptViewPagerWrapContent

/**
 * NB! in order to function ViewPager must be initialized with [io.noties.adapt.viewpager.AdaptViewPager.init]
 * NB! File name differs from element\'s name to bring attention to the requirement
 * to have a [ViewPager] initialized with [Adapt] instance
 *
 * @since $UNRELEASED;
 * @see AdaptViewPagerWrapContent
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.AdaptPagerWrapContent(
) = Element { AdaptViewPagerWrapContent(it) }