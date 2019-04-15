package io.noties.adapt.sample.screen.group

import android.os.Bundle
import android.transition.TransitionManager
import io.noties.adapt.AdaptViewGroup
import io.noties.adapt.OnClickWrapper
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.screen.BaseSampleActivity
import ru.noties.debug.Debug

class AdaptViewGroupActivity : BaseSampleActivity() {

    override fun layoutResId() = R.layout.activity_view_group

    override fun title() = "ViewGroup\n+Transitions"

    override fun addMoreItems() {

        val items = group.currentItems.toMutableList().apply {
            // wrappers can wrap other wrappers
            val items = generator.generate(1)
                    .map { OnClickWrapper(it) { item, _ -> Debug.i("click wrapper, item: %s", item) } }
                    .map { GroupWrapper(it) }
                    .toList()
            addAll(items)
        }

        // trigger auto transition
        TransitionManager.beginDelayedTransition(group.viewGroup())

        group.setItems(items)
    }

    override fun shuffleItems() {

        // trigger auto transition
        TransitionManager.beginDelayedTransition(group.viewGroup())

        group.setItems(generator.shuffle(group.currentItems))
    }

    private lateinit var group: AdaptViewGroup

    private val generator = ItemGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        group = AdaptViewGroup.create(findViewById(R.id.group))

        addMoreItems()
    }
}