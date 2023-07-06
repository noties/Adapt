package io.noties.adapt.sample.explore

import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IdRes

object ExploreRemoteViews {
    // can we create the same pattern for the other parts, like widgets?
    //  it seems to be not possible to create views programmatically for widgets...
    //  but at least configure then we can

    fun RemoteViews.factory(block: RemoteViewsFactory.() -> Unit) {

    }

    // onClickPendingIntent (whole listener)
    // setOnClickFillInIntent() for individual views
    // setPendingIntentTemplate (for a adapter, each item uses it)

    // TODO: create view normally and just, convert it to bitmap and just set

    // maybe we do not need at all, it has no value added, can use RemoteViews directly
    class RemoteViewsFactory(val remoteViews: RemoteViews) {
        init {
            // only accepts other remote-views
//            remoteViews.addView()
        }
    }

    class RemoteViewsElement<T : View>(
        val remoteViews: RemoteViews,
        @IdRes val id: Int
    )

    // not very convenient
    @Suppress("FunctionName")
    fun RemoteViewsFactory.Text(@IdRes id: Int): RemoteViewsElement<TextView> {
        return RemoteViewsElement(remoteViews, id)
    }

//    @Suppress("FunctionName")
//    fun RemoteViews.Text(@IdRes id: Int, block: (RemoteViewsElement<TextView>) -> Unit) = this
//        .also { block(RemoteViewsElement(it, id)) }

    fun <V : View> RemoteViewsElement<V>.enabled(
        enabled: Boolean = true
    ) = this.also {
        remoteViews.setBoolean(id, "setEnabled", enabled)
    }

    fun <V : View> RemoteViewsElement<V>.backgroundColor(
        @ColorInt backgroundColor: Int
    ) = this.also {
        remoteViews.setInt(id, "setBackgroundColor", backgroundColor)
    }

    // TODO: View layout, width, height, etc
    // View:
    // setBackgroundResource int
    // setMinimumHeight int
    // setVisibility int
    // setEnabled
    // setBackgroundColor
    // setBackgroundResource
    // setMinimumHeight
    // setMinimumWidth (not annotated as remotable)
    // setViewLayoutMargin (Android 12)
    // setViewPadding
    // clipToOutline

    fun <T : TextView> RemoteViewsElement<T>.text(text: CharSequence?) = this.also {
        remoteViews.setTextViewText(id, text)

        // shape to bitmap?
//        remoteViews.setBitmap()
    }

    fun <T : TextView> RemoteViewsElement<T>.textColor(@ColorInt color: Int) = this.also {
        remoteViews.setTextColor(id, color)
    }

    fun <T : TextView> RemoteViewsElement<T>.textSize(size: Int) = this.also {
        remoteViews.setTextViewTextSize(id, TypedValue.COMPLEX_UNIT_SP, size.toFloat())
    }

    fun <T : TextView> RemoteViewsElement<T>.textSingleLine(
        singleLine: Boolean = true
    ) = this.also {
        remoteViews.setBoolean(id, "setSingleLine", singleLine)
    }

    // TextView:
    // setTextScaleX float
    // setLetterSpacing float
    // setTextColor color-state-list, huh? how?!
    // setHighlightColor int
    // setAutoLinkMask int
    // setLinksClickable boolean
    // setHintTextColor int
    // setLinkTextColor int
    // setPaintFlags int
    // setMinLines int
    // setMinHeight int
    // setMaxLines int
    // setMaxHeight int
    // setLines int
    // setHeight int
    // setMinEms int
    // setMinWidth int
    // setMaxEms int
    // setMaxWidth int
    // setEms int
    // setWidth int
    // setHint char-sequence
    // setHint int
    // setError char-sequence

    // RelativeLayout:
    // setIgnoreGravity int (id of view to ignore gravity)
    // setGravity int
    // setHorizontalGravity int
    // setVerticalGravity int

    // https://github.com/manishcm/weatherwidget/blob/master/src/com/example/android/weatherlistwidget/WeatherWidgetService.java
    // ListView:
    // setRemoteViewsAdapter intent
    // smoothScrollToPosition int
    // smoothScrollByOffset int

    // LinearLayout:
    // setBaselineAligned boolean
    // setMeasureWithLargestChildEnabled boolean
    // setBaselineAlignedChildIndex int
    // setWeightSum float
    // setGravity int
    // setHorizontalGravity int
    // setVerticalGravity int

    // FrameLayout:
    // setForegroundGravity int
    // setMeasureAllChildren boolean
    //  if foreground gravity is here, should foreground be also?

    // ImageView
    // setAdjustViewBounds
    // setMaxWidth
    // setMaxHeight
    // setImageResource
    // setImageBitmap
    // setImageLevel
    // setColorFilter
    // setImageAlpha
    // setAlpha
    // setVisibility

    // Button

    // ProgressBar
    // setProgressTintList (Android 12)

    fun hey(remoteViews: RemoteViews) {
        // sizes mapping is for new Android 31...
//        RemoteViews()

        // TODO: we actually can have predefined layouts for basic views and them just add them!
//        remoteViews.addView()

        remoteViews.factory {
            Text(0)
                .text("")
                .textColor(0)
        }
    }
}