package io.noties.adapt.ui.element

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.element.ElementViewFactory.HScroll
import io.noties.adapt.ui.element.ElementViewFactory.HStack
import io.noties.adapt.ui.element.ElementViewFactory.Image
import io.noties.adapt.ui.element.ElementViewFactory.Pager
import io.noties.adapt.ui.element.ElementViewFactory.Progress
import io.noties.adapt.ui.element.ElementViewFactory.Recycler
import io.noties.adapt.ui.element.ElementViewFactory.Spacer
import io.noties.adapt.ui.element.ElementViewFactory.Text
import io.noties.adapt.ui.element.ElementViewFactory.TextInput
import io.noties.adapt.ui.element.ElementViewFactory.VScroll
import io.noties.adapt.ui.element.ElementViewFactory.VStack
import io.noties.adapt.ui.element.ElementViewFactory.View
import io.noties.adapt.ui.element.ElementViewFactory.ZStack
import io.noties.adapt.ui.element.ElementViewFactory.contextWrapper

/**
 * Factories for all elements
 * @see HScroll
 * @see HStack
 * @see Image
 * @see Pager
 * @see Progress
 * @see Recycler
 * @see Spacer
 * @see Text
 * @see TextInput
 * @see View
 * @see VScroll
 * @see VStack
 * @see ZStack
 *
 * Additionally provides mechanism to process supplied Context (wrapping, inspecting, etc)
 * @see contextWrapper
 */
object ElementViewFactory {
    /**
     * Special function to wrap supplied context before passing to one of the factory methods below.
     * Can be useful to wrap supplied context into `ContextWrapper` to provide  various customizations.
     * Added here because it is not always convenient to provide wrapped context to each ViewFactory call
     * @see io.noties.adapt.ui.util.CachingContextWrapper
     *
     * __NB!__ none of the factories are calling this function directly. It is intended to be used
     * by [io.noties.adapt.ui.ViewElement] and [io.noties.adapt.ui.ViewFactory].
     *
     * __NB!__ as children of view-groups are using context of parent view-group and they
     * also are calling this wrapper function, it is possible that already wrapped context
     * might be wrapped again. This is why it is important to check receiving context
     * in order to detect if it has been processed already or not. For example, like
     * [io.noties.adapt.ui.util.CachingContextWrapper] does it.
     */
    lateinit var contextWrapper: (Context) -> Context

    lateinit var HScroll: (Context) -> HorizontalScrollView
    lateinit var HStack: (Context) -> LinearLayout
    lateinit var Image: (Context) -> ImageView
    lateinit var Pager: (Context) -> androidx.viewpager.widget.ViewPager
    lateinit var Progress: (Context) -> ProgressBar
    lateinit var Recycler: (Context) -> androidx.recyclerview.widget.RecyclerView
    lateinit var Spacer: (Context) -> View
    lateinit var Text: (Context) -> TextView
    lateinit var TextInput: (Context) -> EditText
    lateinit var View: (Context) -> View
    lateinit var VScroll: (Context) -> ScrollView
    lateinit var VStack: (Context) -> LinearLayout
    lateinit var ZStack: (Context) -> FrameLayout

    init {
        reset()
    }

    fun reset() {
        // default implementation just returns received argument
        contextWrapper = { it }
        HScroll = { HorizontalScrollView(it) }
        HStack = { LinearLayout(it) }
        Image = {
            ImageView(it).also { imageView ->
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                imageView.layoutParams =
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            }
        }
        Pager = { androidx.viewpager.widget.ViewPager(it) }
        Progress = {
            ProgressBar(it).also { progressBar ->
                progressBar.layoutParams =
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            }
        }
        Recycler = { androidx.recyclerview.widget.RecyclerView(it) }
        Spacer = { android.view.View(it) }
        Text = { TextView(it) }
        TextInput = { EditText(it) }
        View = { android.view.View(it) }
        VScroll = { ScrollView(it) }
        VStack = { LinearLayout(it) }
        ZStack = { FrameLayout(it) }
    }
}