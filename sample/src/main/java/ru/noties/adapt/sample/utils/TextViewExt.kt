package ru.noties.adapt.sample.utils

import android.text.TextUtils
import android.view.View
import android.widget.TextView

fun TextView.setTextOrHide(text: CharSequence?) {
    this.visibility = if (TextUtils.isEmpty(text)) View.GONE else View.VISIBLE
    this.text = text
}