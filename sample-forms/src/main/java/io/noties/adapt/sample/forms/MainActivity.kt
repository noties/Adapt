package io.noties.adapt.sample.forms

import android.app.Activity
import android.os.Bundle

class MainActivity: Activity() {

    // each item must have an update handle (when it changes -> some callback is invoked)
    // each item must have a definition -> what it is, hints, placeholders, labels, etc (+id)
    // each item must have a validation path
    // we must somehow be able to construct all required data in a form of a data class (Form, maybe?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}