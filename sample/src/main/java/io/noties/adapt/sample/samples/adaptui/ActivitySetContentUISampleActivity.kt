package io.noties.adapt.sample.samples.adaptui

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.util.setContentUI

class ActivitySetContentUISampleActivity : Activity() {

    lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentUI {
            // by default will FILL parent
            ZStack {
                Text("Hello here!")
                    .layoutWrap()
                    .reference(::text)
                    .textColor { white }
                    .textSize { 24 }
                    .layoutGravity { center }
            }.backgroundColor { black }
        }
    }
}