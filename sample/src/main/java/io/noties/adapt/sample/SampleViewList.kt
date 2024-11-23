package io.noties.adapt.sample

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Adapt
import io.noties.adapt.CachingHolder
import io.noties.adapt.ItemLayout
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.ui.color.backgroundSecondary
import io.noties.adapt.sample.ui.color.emeraldGreen
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.dimen.appBarHeight
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
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Recycler
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.TextInput
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.imageScaleType
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
import io.noties.adapt.ui.util.ViewFlag
import io.noties.adapt.ui.util.addOnFocusChangedListener
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.element
import io.noties.adapt.ui.util.onDetachedOnce
import io.noties.adapt.ui.util.withAlphaComponent
import io.noties.adapt.ui.visible
import io.noties.debug.Debug

// TODO: make list a timeline - contain additional views,
//  for example:
//  - new version detected (inline list item, no modals)
//  - new library version detected
//  - contact info (chat, forum, etc)

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.SampleAppBar(
    tint: ColorsBuilder? = null
) = ZStack {
    Image(R.drawable.adapt_logo)
        .layoutFill()
        .imageScaleType { centerInside }
        .padding(top = 14, bottom = 6)
        .let {
            if (tint != null) {
                it.imageTint(tint)
            } else {
                it
            }
        }
}.indent()
    .layout(fill, Dimens.appBarHeight)

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

    fun view(parent: ViewGroup): View {
        return ViewFactory.createView(parent.context) {
            ZStack {

                val recycler = Recycler()
                    .layoutFill()
                    .recyclerLinearLayoutManager()
                    .padding(top = (Dimens.appBarHeight * 2) + 12)
                    .noClip()
                    .also {
                        it
                            .adaptRecyclerView()
                            .reference(::adapt)
                    }

                VStack {

                    SampleAppBar()

                    val verticalPadding = 6

                    ZStack {

                        lateinit var clear: ViewElement<out View, *>

                        val height = Dimens.appBarHeight - (verticalPadding * 2)

                        Image(R.drawable.ic_search_24)
                            .layout(height, height)
                            .layoutMargin(leading = 16)
                            .layoutGravity { center.vertical }
                            .padding(10)
                            .imageTint { text.withAlphaComponent(0.22F) }

                        val focusedColor = Colors.emeraldGreen.withAlphaComponent(0.1F)
                        val normalColor = Colors.text.withAlphaComponent(0.1F)

                        val input = TextInput()
                            .layout(fill, fill)
                            .textInputType { text.shortMessage }
                            .backgroundWithState {
                                val base = Capsule()
                                focused = base.copy { fill(focusedColor) }
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

                    Flex { }
                        .flexWrap()
                        .flexGap(8)
                        .padding(horizontal = 16, vertical = 2)
                        .layoutMargin(bottom = 8)
                        .adaptViewGroup {
                            it.hideIfEmpty(true)
                        }
                        .reference(::searchTagsAdapt)

                }.indent()
                    .layout(fill, wrap)
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
                    .onViewLayoutChanged { v, _, _ ->
                        val rv = recycler.view
                        if (rv.top != v.bottom) {
                            val y = rv.canScrollVertically(-1)
                            rv.setPadding(
                                rv.paddingLeft,
                                v.bottom,
                                rv.paddingRight,
                                rv.paddingBottom
                            )
                            if (!y) {
                                rv.post { rv.scrollToPosition(0) }
                            }
                        }
                    }
                    .preview {
                        it.onView { it.background.alpha = 255 }
                    }
                    .onView { view ->
                        recycler.onView { setupRecyclerScroll(view, it) }
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
        }
    }

    private fun bind() {
        adapt.setItems(initialItems)
        searchView.element
            .textOnTextChanged {
                search(it.toString())
            }
            .render()
    }

    private fun setupRecyclerScroll(view: View, recycler: RecyclerView) {
//        var animator: ValueAnimator? = null

        fun change(isVisible: Boolean) {
            // actually changed
            if (ViewFlag.set(view, isVisible)) {
                Debug.i("changed, isVisible:$isVisible")
                view.background?.also {
                    it.alpha = if (isVisible) 255 else 0
                }
                view.elevation = if (isVisible) {
                    4.dip.toFloat()
                } else {
                    0F
                }
            }
        }

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                change(recycler.canScrollVertically(-1))
            }
        })
    }


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
        val items = search.search(
            searchText,
            searchTags,
            initialItems
        ).takeIf { it.isNotEmpty() } ?: listOf(EmptyItem())

        adapt.setItems(items)
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
                sample.description?.toString()?.normalized() ?: ""
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

    class SampleItem(
        val sample: Sample,
        private val onTagClicked: (String) -> Unit,
        val onSampleClicked: (Sample) -> Unit
    ) : ElementItem<SampleItem.Ref>(hash(sample), { Ref() }) {
        class Ref {
            lateinit var titleView: TextView
            lateinit var tagsAdapt: Adapt
            lateinit var descriptionView: TextView
        }

        override fun bind(holder: Holder<Ref>) {
            with(holder.ref) {
                titleView.text = sample.name

                val tags = sample.tags
                    .map {
                        TagItem(tag = it, onClick = onTagClicked)
                    }
                tagsAdapt.setItems(tags)

                descriptionView.text = sample.description
            }

            holder.itemView().element
                .onClick { onSampleClicked(sample) }
                .render()
        }

        override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
            VStack {

                Text()
                    .reference(ref::titleView)
                    .textSize { title3 }
                    .textBold()
                    .textColor { text }

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
                .padding(horizontal = 16)
                .padding(top = 8, bottom = 12)
                .background {
                    RoundedRectangle(8) {
                        padding(3)
                    }
                }
                .foregroundDefaultSelectable()
                .clipToOutline()
        }

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
                        description = HtmlUtil.fromHtml("This is the description that goes here, <em>someone</em> says that it supports some <b>HTML</b>&mldr;"),
                        tags = listOf("widget", "no", "maybe", "50%")
                    )
            ),
            onSampleClicked = {}
        )
        return view.view(parent)
    }
}