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
 */
object ElementViewFactory {
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
        HScroll = { HorizontalScrollView(it) }
        HStack = { LinearLayout(it) }
        Image = { ImageView(it) }
        Pager = { androidx.viewpager.widget.ViewPager(it) }
        Progress = { ProgressBar(it) }
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