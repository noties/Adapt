package io.noties.adapt.sample.screen

import android.app.Activity
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.View
import android.widget.TextView
import io.noties.adapt.sample.R

abstract class BaseSampleActivity : Activity() {


    @LayoutRes
    abstract fun layoutResId(): Int

    abstract fun title(): CharSequence

    abstract fun addMoreItems()

    abstract fun shuffleItems()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId())

        findViewById<TextView>(R.id.app_bar_title).apply {
            text = title()
        }

        findViewById<View>(R.id.app_bar_back).setOnClickListener { onBackPressed() }

        findViewById<View>(R.id.app_bar_add).setOnClickListener { addMoreItems() }

        findViewById<View>(R.id.app_bar_shuffle).setOnClickListener { shuffleItems() }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (isFinishing) {
            overridePendingTransition(R.anim.out_appear, R.anim.out_disappear)
        }
    }
}