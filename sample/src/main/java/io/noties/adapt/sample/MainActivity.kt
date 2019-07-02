package io.noties.adapt.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.OnClickWrapper
import io.noties.adapt.sample.screen.flex.AdaptFlexActivity
import io.noties.adapt.sample.screen.grid.AdaptGridActivity
import io.noties.adapt.sample.screen.group.AdaptViewGroupActivity
import io.noties.adapt.sample.screen.linear.AdaptLinearRecyclerActivity
import io.noties.adapt.sample.screen.view.AdaptViewActivity
import io.noties.debug.AndroidLogDebugOutput
import io.noties.debug.Debug

enum class Screen(val activity: Class<out Activity>, val title: String, val description: String? = null) {
    LINEAR(AdaptLinearRecyclerActivity::class.java, "Recycler", "Usage in RecyclerView with LinearLayoutManager"),
    GRID(AdaptGridActivity::class.java, "Recycler Grid", "Usage in RecyclerView with GridLayoutManager"),
    VIEW_GROUP(AdaptViewGroupActivity::class.java, "ViewGroup", "Usage in a ViewGroup"),
    FLEX_VIEW_GROUP(AdaptFlexActivity::class.java, "FlexLayout", "Usage with a FlexLayout in a ViewGroup context"),
    VIEW(AdaptViewActivity::class.java, "View", "Usage with a regular View (bind Item)")
}

class MainActivity : Activity() {

    companion object {
        init {
            Debug.init(AndroidLogDebugOutput(true))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recycler_view)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)

        val adapt = Adapt.create()
        recycler.adapter = adapt
        adapt.setItems(items())
    }

    private fun items(): List<Item<*>> {

        val randomizer = SampleRandomizer()

        return Screen.values()
                .map {
                    OnClickWrapper(SampleItem(sample(randomizer, it))) { _, _ ->
                        display(it.activity)
                    }
                }
    }

    private fun sample(randomizer: SampleRandomizer, screen: Screen) = Sample(
            randomizer.nextColor(),
            randomizer.nextShape(),
            screen.title,
            screen.description)

    private fun display(activity: Class<out Activity>) {
        startActivity(Intent(this, activity))
        overridePendingTransition(R.anim.in_appear, R.anim.in_disappear)
    }
}