package io.noties.adapt.sample.screen.group

import android.os.Bundle
import io.noties.adapt.AdaptViewGroup
import io.noties.adapt.OnClickWrapper
import io.noties.adapt.TransitionChangeHandler
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.screen.BaseSampleActivity
import io.noties.debug.Debug

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

//        // trigger auto transition
        // @since 2.3.0 there is the TransitionChangeHandler utility class,
        // but it's still possible to just use this:
//        TransitionManager.beginDelayedTransition(group.viewGroup())

        group.setItems(items)
    }

    override fun shuffleItems() {

//        // trigger auto transition
        // @since 2.3.0 there is the TransitionChangeHandler utility class,
        // but it's still possible to just use this:
//        TransitionManager.beginDelayedTransition(group.viewGroup())

        group.setItems(generator.shuffle(group.currentItems))
    }

    private lateinit var group: AdaptViewGroup

    private val generator = ItemGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        group = AdaptViewGroup.builder(findViewById(R.id.group))
                .changeHandler(TransitionChangeHandler.create())
                .build()

        addMoreItems()
    }
}