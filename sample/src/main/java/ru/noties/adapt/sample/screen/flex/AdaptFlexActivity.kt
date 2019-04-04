package ru.noties.adapt.sample.screen.flex

import android.os.Bundle
import android.transition.TransitionManager
import ru.noties.adapt.AdaptViewGroup
import ru.noties.adapt.sample.ItemGenerator
import ru.noties.adapt.sample.R
import ru.noties.adapt.sample.screen.BaseSampleActivity

class AdaptFlexActivity : BaseSampleActivity() {

    override fun layoutResId() = R.layout.activity_flex

    override fun title() = "FlexLayout"

    override fun addMoreItems() {

        val items = group.currentItems.toMutableList().apply {
            val list = List(3) {
                FlexItemWrapper(it + 1, generator.generate(1)[0])
            }
            addAll(list)
        }

        TransitionManager.beginDelayedTransition(group.viewGroup())
        group.setItems(items)
    }

    override fun shuffleItems() {
        TransitionManager.beginDelayedTransition(group.viewGroup())
        group.setItems(generator.shuffle(group.currentItems))
    }

    private lateinit var group: AdaptViewGroup

    private val generator = ItemGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        group = AdaptViewGroup.create(findViewById(R.id.flex))

        addMoreItems()
    }
}