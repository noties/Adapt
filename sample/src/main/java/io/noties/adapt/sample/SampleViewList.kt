package io.noties.adapt.sample

import android.content.Context
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Adapt
import io.noties.adapt.CachingHolder
import io.noties.adapt.Item
import io.noties.adapt.ItemLayout
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.ui.color.backgroundSecondary
import io.noties.adapt.sample.ui.color.emeraldGreen
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.sample.ui.dimen.appBarHeight
import io.noties.adapt.sample.ui.element.SampleAppBar
import io.noties.adapt.sample.ui.text.body
import io.noties.adapt.sample.ui.text.footnote
import io.noties.adapt.sample.ui.text.subHeadline
import io.noties.adapt.sample.ui.text.title3
import io.noties.adapt.sample.util.HtmlUtil
import io.noties.adapt.sample.util.normalized
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.adaptRecyclerView
import io.noties.adapt.ui.adaptViewGroup
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.app.color.ColorsBuilder
import io.noties.adapt.ui.app.dimen.Dimens
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundGradient
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Recycler
import io.noties.adapt.ui.element.Spacer
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.TextInput
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.imageTint
import io.noties.adapt.ui.element.recyclerLinearLayoutManager
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textHideIfEmpty
import io.noties.adapt.ui.element.textInputType
import io.noties.adapt.ui.element.textOnTextChanged
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.flex.Flex
import io.noties.adapt.ui.flex.flexGap
import io.noties.adapt.ui.flex.flexWrap
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.onViewLayoutChanged
import io.noties.adapt.ui.onViewPreDrawOnce
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.PreviewLayout
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Line
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.ShapeDrawable
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.shape.reference
import io.noties.adapt.ui.state.backgroundWithState
import io.noties.adapt.ui.util.addOnFocusChangedListener
import io.noties.adapt.ui.util.element
import io.noties.adapt.ui.util.onDetachedOnce
import io.noties.adapt.ui.util.withAlphaComponent
import io.noties.adapt.ui.visible
import io.noties.adapt.ui.windowinset.OnWindowInsetsChangedListenerDelegate
import io.noties.adapt.ui.windowinset.windowInsetsPadding
import io.noties.debug.Debug

// TODO: make list a timeline - contain additional views,
//  for example:
//  - new version detected (inline list item, no modals)
//  - new library version detected
//  - contact info (chat, forum, etc)

// TODO: zstack that has content and overlay
//  content contribute to bounds, overlay does nto contribute to bounds, only can match what content reported
class SampleViewList(
    samples: List<Sample>,
    private val onSampleClicked: (Sample) -> Unit
) {

    lateinit var adapt: Adapt
    lateinit var searchView: EditText
    lateinit var searchTagsAdapt: Adapt
    lateinit var searchTagColor: SearchTagColor

    val initialItems = samples
        .map {
            SampleItem(
                sample = it,
                onTagClicked = ::onTagClicked,
                onSampleClicked = onSampleClicked
            )
        }

    private val search = Search()
    private val searchTags = mutableSetOf<String>()
    private var searchText = ""
    private val recyclerPadding = RecyclerPadding()

    fun view(parent: ViewGroup): View {
        return ViewFactory.createView(parent.context) {

            lateinit var appBar: ViewElement<out View, out FrameLayout.LayoutParams>
            lateinit var bottomBar: ViewElement<out View, out FrameLayout.LayoutParams>

            ZStack {

                Recycler()
                    .layoutFill()
                    .recyclerLinearLayoutManager()
                    .noClip()
                    .also {
                        it
                            .adaptRecyclerView()
                            .reference(::adapt)
                    }
                    .onView {
                        recyclerPadding.recyclerView(view = it)
                    }

                appBar = SampleAppBar()
                    .backgroundGradient {
                        linear {
                            edges { top to bottom }
                                .setColors(white, white.withAlphaComponent(alpha = 0F))
                        }
                    }
                    .onViewPreDrawOnce {
                        recyclerPadding.appBar(height = it.height)
                    }


                bottomBar = VStack {
                    Flex { }
                        .layout(width = fill, height = wrap)
                        .flexWrap()
                        .flexGap(8)
                        .padding(horizontal = 16, vertical = 2)
                        .adaptViewGroup {
                            it.hideIfEmpty(true)
                        }
                        .reference(::searchTagsAdapt)

                    val verticalPadding = 6

                    ZStack {

                        lateinit var clear: ViewElement<out View, *>

                        val height = Dimens.appBarHeight - (verticalPadding * 2)

                        val focusedColor = Colors.emeraldGreen
                        val normalColor = Colors.hex("#eee")

                        val input = TextInput()
                            .layout(fill, fill)
                            .textInputType { text.shortMessage }
                            .backgroundWithState {
                                val base = Capsule()
                                focused = base.copy {
                                    fill(normalColor)
                                    Capsule {
                                        padding(1)
                                        stroke(color = focusedColor, width = 2)
                                    }
                                }
                                default = base.copy { fill(normalColor) }
                            }
                            .padding(horizontal = height)
                            .layoutMargin(horizontal = 16)
                            .textOnTextChanged {
                                clear.visible(it.isNotEmpty()).render()
                            }
                            .reference(::searchView)
                            .onView {
                                searchTagColor = SearchTagColor.create(
                                    textField = it,
                                    normalColor = { normalColor },
                                    focusedColor = { focusedColor }
                                )
                            }

                        Image(R.drawable.ic_search_24)
                            .layout(height, height)
                            .layoutMargin(leading = 16)
                            .layoutGravity { center.vertical }
                            .padding(10)
                            .imageTint { text.withAlphaComponent(0.22F) }

                        clear = Image(R.drawable.ic_close_24)
                            .imageTint { text.withAlphaComponent(0.42F) }
                            .layout(height, height)
                            .background { Circle() }
                            .foregroundDefaultSelectable()
                            .clipToOutline()
                            .layoutMargin(trailing = 16)
                            .layoutGravity { trailing.center }
                            .padding(10)
                            .visible(false)
                            .onClick {
                                input.view.setText("")
                            }

                    }.indent()
                        .layout(fill, Dimens.appBarHeight)
                        .padding(vertical = verticalPadding)

                }.indent()
                    .layout(fill, wrap)
                    .layoutGravity { bottom }
                    .background {
                        Rectangle {
                            alpha(0F)
                            fill { backgroundSecondary }
                            Line {
                                fromRelative(0F, 1F)
                                toRelative(1F, 1F)
                                stroke(Colors.text.withAlphaComponent(0.1F))
                            }
                        }
                    }
                    .preview {
                        it.onView { it.background.alpha = 255 }
                    }
                    .padding(bottom = 12)
                    .onViewLayoutChanged { _, _, height ->
                        Debug.e("onViewLayoutChanged height:$height")
                        // plus additional padding
                        recyclerPadding.bottomBar(height = height + 96)
                    }
                    .backgroundGradient {
                        linear {
                            edges { bottom to top }
                                .setColors(white, white.withAlphaComponent(alpha = 0F))
                        }
                    }

            }.layoutFill()
                .preview {
                    it.previewBounds()
                        .onView {
                            onTagClicked("hello")
                        }
                }
                .onView {
                    bind()
                }
                .windowInsetsPadding { ime }
//                .onWindowInsetsChanged(insets = { ime }) {
//
////                    appBar.layoutMargin(top = insetsTop).render()
////                    bottomBar.layoutMargin(bottom = insetsBottom).render()
//
//                    recyclerPadding.windowInsets(
//                        padding = Padding(
//                            leading = insetsLeading,
//                            top = insetsTop,
//                            trailing = insetsTrailing,
//                            bottom = insetsBottom
//                        )
//                    )
//                }
                .ifAvailable(version = Build.VERSION_CODES.R) {
                    it.onView {
                        val d = OnWindowInsetsChangedListenerDelegate.get(it)
                        d?.add { _, insets ->
                            if (!insets.isVisible(WindowInsets.Type.ime())) {
                                if (searchView.hasFocus()) {
                                    searchView.clearFocus()
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun bind() {
        adapt.setItems(items(items = initialItems))
        searchView.element
            .textOnTextChanged {
                search(it.toString())
            }
            .render()
    }

    private fun items(items: List<SampleItem>): List<Item<*>> {
//        return items
//            .flatMap {
//                listOf(
//                    SampleItemTopDivider(sample = it.sample),
//                    it
//                )
//            }
        return items
    }

//    private fun setupRecyclerScroll(view: View, recycler: RecyclerView, isTopBar: Boolean) {
////        var animator: ValueAnimator? = null
//
//        fun change(isVisible: Boolean) {
//            // actually changed
//            if (ViewFlag.set(view, isVisible)) {
//                Debug.i("changed, isVisible:$isVisible")
//                view.background?.also {
//                    it.alpha = if (isVisible) 255 else 0
//                }
//                view.elevation = if (isVisible) {
//                    4.dip.toFloat()
//                } else {
//                    0F
//                }
//            }
//        }
//
//        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                change(recycler.canScrollVertically(if (isTopBar) -1 else 1))
//            }
//        })
//    }


    private fun onTagClicked(tag: String) {
        Debug.i("tag:'$tag' tags:$searchTags")

        if (searchTags.add(tag)) {
            triggerSearch()

            bindSearchTags()
        }
    }

    private fun onTagRemoveClicked(tag: String) {
        Debug.i("tag:'$tag' tags:$searchTags")
        if (searchTags.remove(tag)) {
            triggerSearch()

            bindSearchTags()
        }
    }

    private fun bindSearchTags() {
        val items = searchTags
            .map {
                SearchTagItem(it, searchTagColor) {
                    onTagRemoveClicked(it)
                }
            }
        searchTagsAdapt.setItems(items)
    }

    private fun triggerSearch() {
        Debug.i("text:'$searchText' tags:$searchTags")
        val searchItems = search.search(
            searchText,
            searchTags,
            initialItems
        ).takeIf { it.isNotEmpty() }

        if (searchItems != null) {
            adapt.setItems(items(items = searchItems))
        } else {
            adapt.setItems(listOf(EmptyItem()))
        }
    }

    private fun search(text: String) {
        Debug.i("search:'$text'")
        searchText = text
        triggerSearch()
    }

    private class Search {

        data class SampleInfo(val sample: Sample) {
            val name: String by lazy(LazyThreadSafetyMode.NONE) { sample.name.normalized() }
            val description: String by lazy(LazyThreadSafetyMode.NONE) {
                sample.description?.normalized() ?: ""
            }
            val tags: List<String> by lazy(LazyThreadSafetyMode.NONE) {
                sample.tags
                    .map { it.normalized() }
            }
        }

        private val cache = mutableMapOf<Sample, SampleInfo>()

        fun search(
            text: String,
            tags: Set<String>,
            items: List<SampleItem>
        ): List<SampleItem> {

            // filter by tag first
            val tagFiltered = items
                .filter {
                    tags.all { tag -> it.sample.tags.contains(tag) }
                }

            val search = text.normalized()

            return tagFiltered
                .filter {
                    val info = cache.getOrPut(it.sample) { SampleInfo(it.sample) }
                    test(search, info)
                }
        }

        private fun test(text: String, info: SampleInfo): Boolean {
            return info.name.contains(text) ||
                    info.description.contains(text) ||
                    info.tags.any { it.contains(text) } ||
                    info.sample.id.contains(text)
        }
    }

    interface SearchTagColor {
        fun tagColor(item: SearchTagItem, block: (/*@ColorInt*/color: Int) -> Unit)

        companion object {
            fun create(
                textField: View,
                normalColor: ColorsBuilder,
                focusedColor: ColorsBuilder
            ): SearchTagColor {
                fun color(hasFocus: Boolean = textField.hasFocus()) = if (hasFocus) {
                    focusedColor(Colors)
                } else {
                    normalColor(Colors)
                }

                val map = mutableMapOf<SearchTagItem, (Int) -> Unit>()

                val registration = textField.addOnFocusChangedListener { _, hasFocus ->
                    map.values
                        .forEach {
                            it(color(hasFocus))
                        }
                }

                // unregister everything on detached
                textField.onDetachedOnce {
                    registration.unregisterOnFocusChangedListener()
                    map.clear()
                }

                return object : SearchTagColor {
                    override fun tagColor(item: SearchTagItem, block: (color: Int) -> Unit) {
                        // ignore the old
                        val old = map.put(item, block)
                        // immediately send the color based on state
                        block(color())
                    }
                }
            }
        }
    }

    class SearchTagItem(
        val tag: String,
        val tagColor: SearchTagColor,
        val onClickTag: (String) -> Unit
    ) : ElementItem<SearchTagItem.Ref>(hash(tag), { Ref() }) {
        class Ref {
            lateinit var tagView: TextView
            lateinit var drawable: ShapeDrawable<Unit>
        }

        override fun bind(holder: Holder<Ref>) {
            holder.ref.tagView.text = tag

            holder.itemView().element
                .onClick {
                    onClickTag(tag)
                }
                .render()

            tagColor.tagColor(this) { color ->
                holder.ref.drawable.invalidate {
                    shape.fill { color }
                }
            }
        }

        override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
            HStack {

                Text("#")
                    .layoutWrap()
                    .textSize { body }
                    .textColor { text.withAlphaComponent(0.22F) }
                    .layoutMargin(trailing = 6)

                Text()
                    .reference(ref::tagView)
                    .layoutWrap()
                    .textSize { body }
                    .textColor { text }

            }.indent()
                .layoutWrap()
                .padding(horizontal = 12, vertical = 2)
                .background(ShapeDrawable.invoke {
                    Capsule()
                }.reference(ref::drawable))
                .foregroundDefaultSelectable()
                .clipToOutline()
        }
    }

    class EmptyItem : ItemLayout(99L, R.layout.widget_empty) {
        override fun bind(holder: CachingHolder) = Unit
    }

    class SampleItemTopDivider(val sample: Sample) :
        ElementItem<SampleItemTopDivider.Ref>(hash(sample), { Ref() }) {
        class Ref {
            lateinit var colorCircles: List<ViewElement<out View, *>>

            fun bind(colors: IntArray) {
                colorCircles.zip(colors.toList())
                    .map {
                        it.first.view to it.second
                    }
                    .forEach { (el, color) ->
                        el.element
                            .background {
                                Circle { fill(color = color) }
                            }
                            .render()
                    }
            }
        }

        override fun bind(holder: Holder<Ref>) {
            val colors = Sample.gradientColors(sample = sample)
            holder.ref.bind(colors)
        }

        override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
            HStack {
                Spacer()

                listOf(
                    ColorCircle(),
                    ColorCircle(),
                    ColorCircle(),
                    ColorCircle(),
                ).also {
                    ref.colorCircles = it
                }

                Spacer()
            }.indent()
                .layout(width = fill, height = wrap)
        }

        @Suppress("FunctionName")
        fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.ColorCircle() = View()
            .layout(width = 16, height = 16)
            .layoutMargin(horizontal = 2)
            .layoutMargin(top = 8, bottom = 2)
    }

    class SampleItem(
        val sample: Sample,
        private val onTagClicked: (String) -> Unit,
        val onSampleClicked: (Sample) -> Unit
    ) : ElementItem<SampleItem.Ref>(hash(sample), { Ref() }) {

        private val sampleColors: List<Int> by lazy {
            Sample.gradientColors(sample = sample).toList()
        }

        class Ref {
            lateinit var titleView: TextView
            lateinit var tagsAdapt: Adapt
            lateinit var descriptionView: TextView
            lateinit var colorCircles: List<ViewElement<out View, *>>
        }

        private val description: CharSequence? by lazy(LazyThreadSafetyMode.NONE) {
            sample.description
                ?.takeIf { it.isNotEmpty() }
                ?.let { HtmlUtil.fromHtml(it) }
        }

        override fun bind(holder: Holder<Ref>) {
            with(holder.ref) {
                titleView.text = sample.name

                val tags = sample.tags
                    .map {
                        TagItem(tag = it, onClick = onTagClicked)
                    }
                tagsAdapt.setItems(tags)

                descriptionView.text = description

                colorCircles.zip(sampleColors)
                    .forEach { (el, color) ->
                        el.background { Circle { fill(color = color) } }
                    }
            }

            holder.itemView().element
                .onClick { onSampleClicked(sample) }
                .render()
        }

        override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
            VStack {

                HStack {

                    Text()
                        .reference(ref::titleView)
                        .layoutWrap()
                        .textSize { title3 }
                        .textBold()
                        .textColor { text }

                    Spacer()

                    listOf(
                        ColorCircle(),
                        ColorCircle(),
                        ColorCircle(),
                        ColorCircle(),
                    ).also {
                        ref.colorCircles = it
                    }
                }

                Flex { }
                    .flexWrap()
                    .flexGap(6)
                    .layoutMargin(top = 6)
                    .adaptViewGroup()
                    .reference(ref::tagsAdapt)

                Text()
                    .reference(ref::descriptionView)
                    .textHideIfEmpty()
                    .layoutMargin(top = 6)
                    .textSize { subHeadline }
                    .textColor { text.withAlphaComponent(0.82F) }

            }.indent()
                .preview { it.previewBounds() }
                .padding(horizontal = 16, vertical = 12)
//                .padding(top = 8, bottom = 12)
                .background {
                    RoundedRectangle(8) {
                        padding(3)
                    }
                }
                .foregroundDefaultSelectable()
                .clipToOutline()
        }

        @Suppress("FunctionName")
        fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.ColorCircle() = View()
            .layout(width = 6, height = 6)
            .layoutMargin(horizontal = 1)

        class TagItem(
            val tag: String,
            val onClick: (tag: String) -> Unit
        ) : ElementItem<TagItem.Ref>(hash(tag), { Ref() }) {
            class Ref {
                lateinit var nameView: TextView
            }

            private val text: CharSequence by lazy(LazyThreadSafetyMode.NONE) {
                SpannableStringBuilder().apply {

                    append(
                        "#",
                        ForegroundColorSpan(Colors.text.withAlphaComponent(0.42F)),
                        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    append(tag)
                }
            }

            override fun bind(holder: Holder<Ref>) {
                holder.ref.nameView.text = text
                with(holder.ref) {
                    nameView.text = text

                    nameView.element
                        .onClick { onClick(tag) }
                        .render()
                }
            }

            override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
                Text()
                    .reference(ref::nameView)
                    .layoutWrap()
                    .textSize { footnote }
                    .textColor { text }
                    .padding(horizontal = 8, vertical = 4)
                    .background {
                        RoundedRectangle(6) { fill { hex("#eee") } }
                    }
                    .foregroundDefaultSelectable()
                    .clipToOutline()
            }
        }
    }

    class Padding private constructor(
        // not suppressed, must not be unused
        private var _privateMarker: Byte?,
        val leading: Int,
        val top: Int,
        val trailing: Int,
        val bottom: Int,
    ) {

        interface Factory {
            val zero: Padding get() = this(all = 0)

            operator fun invoke(all: Int) = Padding(
                _privateMarker = null,
                leading = all,
                top = all,
                trailing = all,
                bottom = all,
            )

            operator fun invoke(
                horizontal: Int? = null,
                vertical: Int? = null
            ) = Padding(
                _privateMarker = null,
                leading = horizontal ?: 0,
                top = vertical ?: 0,
                trailing = horizontal ?: 0,
                bottom = vertical ?: 0
            )

            operator fun invoke(
                leading: Int? = null,
                top: Int? = null,
                trailing: Int? = null,
                bottom: Int? = null,
            ) = Padding(
                _privateMarker = null,
                leading = leading ?: 0,
                top = top ?: 0,
                trailing = trailing ?: 0,
                bottom = bottom ?: 0,
            )
        }

        companion object : Factory

        override fun toString(): String {
            return "Padding(bottom=$bottom, trailing=$trailing, top=$top, leading=$leading)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Padding) return false

            if (leading != other.leading) return false
            if (top != other.top) return false
            if (trailing != other.trailing) return false
            if (bottom != other.bottom) return false

            return true
        }

        override fun hashCode(): Int {
            var result = leading
            result = 31 * result + top
            result = 31 * result + trailing
            result = 31 * result + bottom
            return result
        }

        fun copy(
            horizontal: Int? = null,
            vertical: Int? = null
        ): Padding {
            return Padding(
                _privateMarker = null,
                leading = horizontal ?: leading,
                top = vertical ?: top,
                trailing = horizontal ?: trailing,
                bottom = vertical ?: bottom,
            )
        }

        fun copy(
            leading: Int? = null,
            top: Int? = null,
            trailing: Int? = null,
            bottom: Int? = null
        ) = Padding(
            _privateMarker = null,
            leading = leading ?: this.leading,
            top = top ?: this.top,
            trailing = trailing ?: this.trailing,
            bottom = bottom ?: this.bottom,
        )

        operator fun plus(other: Padding): Padding = Padding(
            _privateMarker = null,
            leading = leading + other.leading,
            top = top + other.top,
            trailing = trailing + other.trailing,
            bottom = bottom + other.bottom,
        )
    }

    private class RecyclerPadding {

        private var recyclerView: RecyclerView? = null

        private var contentPadding: Padding = Padding.zero
        private var windowInsetsPadding: Padding = Padding.zero
        private var appBarHeight: Int = 0
        private var bottomBarHeight: Int = 0

        private var currentValue: Padding = Padding.zero

        fun recyclerView(view: RecyclerView?) {
            Debug.i("view:$view")
            if (view == null) {
                recyclerView = null
                contentPadding = Padding.zero
            } else {
                recyclerView = view
                contentPadding = Padding(
                    leading = view.paddingLeft,
                    top = view.paddingTop,
                    trailing = view.paddingRight,
                    bottom = view.paddingBottom
                )
                update(force = true)
            }
        }

        fun windowInsets(padding: Padding) {
            Debug.i("padding:$padding")
            if (windowInsetsPadding != padding) {
                windowInsetsPadding = padding
                update()
            }
        }

        fun appBar(height: Int) {
            Debug.i("height:$height")
            if (appBarHeight != height) {
                appBarHeight = height
                update()
            }
        }

        fun bottomBar(height: Int) {
            Debug.i("height:$height")
            if (bottomBarHeight != height) {
                bottomBarHeight = height
                update()
            }
        }

        private fun update(force: Boolean = false) {
            val padding = Padding(
                leading = contentPadding.leading + windowInsetsPadding.leading,
                top = contentPadding.top + windowInsetsPadding.top + appBarHeight,
                trailing = contentPadding.trailing + windowInsetsPadding.trailing,
                bottom = contentPadding.bottom + windowInsetsPadding.bottom + bottomBarHeight,
            )

            if (force || padding != currentValue) {
                Debug.i("force:$force padding:$padding current:$currentValue")
                currentValue = padding

                recyclerView?.also { recyclerView ->
                    Debug.i("applying to recycler:$recyclerView")
                    recyclerView.setPadding(
                        padding.leading,
                        padding.top,
                        padding.trailing,
                        padding.bottom
                    )

                    // if cannot scroll top (already at top)
                    if (!recyclerView.canScrollVertically(-1)) {
                        // then apply scroll to position, sometimes if it is not updated
                        recyclerView.post {
                            recyclerView.scrollToPosition(0)
                        }
                    }
                }
            }
        }
    }
}

@Preview
private class PreviewSampleViewList(context: Context, attrs: AttributeSet?) :
    PreviewLayout(context, attrs) {
    override fun createView(context: Context, parent: PreviewLayout): View {
        val view = SampleViewList(
            samples = listOf(
                Sample.empty()
                    .copy(
                        name = "Hello my dear sample",
                        description = "This is the description that goes here, <em>someone</em> says that it supports some <b>HTML</b>&mldr;",
                        tags = listOf("widget", "no", "maybe", "50%")
                    )
            ),
            onSampleClicked = {}
        )
        return view.view(parent)
    }
}