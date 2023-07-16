package io.noties.adapt.sample.explore

import android.content.Context
import android.graphics.Color
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.CircleShape
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.util.Gravity

object ExploreReadme {
  fun hey(context: Context) {
    ViewFactory.createView(context) {
// define a shape outside of DSL builder context
val circle = CircleShape {
  fill(Color.YELLOW)
  // also density independent pixels
  size(56, 56)
}

View()
  .background {

    // shape building DSL
    // creates a rectangle shape, filled with RED color
    Rectangle {
      fill(Color.RED)

      // add a shape explicitly
      add(circle.copy {
        gravity(Gravity.center.trailing)
      })

      add(circle.copy {
        gravity(Gravity.center.leading)
      })

      // `Circle` is a DSL function to create a shape and add it
      Circle {
        fill(Color.RED)
        gravity(Gravity.center)
        size(32, 32)
      }
    }
  }
    }
  }

}