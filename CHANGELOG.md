# Changelog

# 5.0.0
### Changed
* `Text`: `.textFont` => `.textTypeface`

#### Shape
`Shape` has changed API. Right now all shapes are named following basic pattern - `RectangleShape`,
  `OvalShape`, etc. And previous `Rectangle`, `Oval` are used in factory building.

Before:
```kotlin
View()
  .layoutFill()
  .background(Rectangle {
      fill(Color.BLACK)
  })
  .background(ShapeDrawable {
      Rectangle {
          add(Oval {
              fill(Color.RED)
          })
      }
  })
```

After:
```kotlin
View()
  .layoutFill()
  // now `background` and `foreground` expose function to build Shape with ShapeFactory
  .background { /*ShapeFactory.() -> Shape*/
    // function defined in `ShapeFactory`
    Rectangle()
  }
  // new factory method in Shape
  .background(Shape.builder {
    Rectangle {
      // no need to call `add`, `Oval` automatically adds OvalShape
      Oval()
      
      // still shape is returned and can be referenced
      // NB! this `CircleShape` has been added to the parent `RectangleShape`
      val circle = Circle()
      
      // If you need to create a shape without adding it - use proper class name:
      val notAddedCircle = CircleShape()
      
      // add is still present, shapes can be added manually
      add(notAddedCircle.copy())
    }
  })
  .background(ShapeDrawable {
    // NB! this `ShapeDrawable` function signature is still the same, so most usages
    //  should be fine. You might want to remove additional `add` calls as shapes,
    //  referenced via `ShapeFactory` functions are added automatically
    Rectangle {
      // it is valid, shape won't be added twice..
      add(Oval())
      // ..but better to remove `add` all-together
      Oval()
    }
  })
```

The easiest path to migrate to new `Shape` is to explicitly specify proper name
* `Rectangle {}` -&gt; `RectangleShape {}`
* `Oval {}` -&gt; `OvalShape {}`
* etc, for all the shapes

Then, new factory functions can be used, depending on the use-case and should be reviewed manually.
For example, in case of `background` for a `ViewElement`

```diff
 View()
   .layoutFill()
-  .background(Rectangle {
-      // shape definition
-   })
+  .background {
+    Rectangle {
+      // shape definition
+    }
+  }
```

*Please carefully review* your shape definitions, as most of things won't require a special attention,
but in some cases it might lead to different results, for example:

```kotlin
// before:
val shape = Rectangle {
  val base = Oval().fill(Color.RED)
  add(base.copy {
      fill(Color.YELLOW)
  })
  add(base.copy {
      stroke(Color.GREEN)
  })
}

// after `Rectangle` was changed to `RectangleShape`:
val shape = RectangleShape {
  // NB! this line, Oval is automatically added to the parent shape
  //   which in this case, most likely, is not what is needed, change to `OvalShape` instead
  val wrongBase = Oval().fill(Color.RED)
  val validBase = OvalShape().fill(Color.RED)
  add(base.copy {
    fill(Color.YELLOW)
  })
  add(base.copy {
    stroke(Color.GREEN)
  })
}

// NB! as a rule, if result is stored, most likely, full shape name must be used:
val shape = Oval() // it is valid `Oval` returns shape, but it is already added
val shape = OvalShape() // proper usage
```

# 4.0.0

### Added
* `Item.Wrapper` and `Item#wrap`
* `ItemWrapper#findWrapped` method to obtain wrapper of specific type
* Item default `toString` implementation
* `AdaptViewGroup#init` convenience method that takes `ChangeHandler` directly
* `AdaptView#init` convenience method that takes `Item<?>` directly
* `LineNumberId` utility to get current line number in source code
* `AdaptDivider` utility to _divide_ supplied list (convenience to add dividers/separators)
* `Edges` utility class to encapsulate padding/margin
* `Decorator` in `StickyItemDecoration` to additionally process sticky item view
* A collection of wrappers:
  + `BackgroundWrapper`
  + `FrameWrapper`
  + `IdWrapper`
  + `MarginWrapper`
  + `OnBindWrapper`
  + `OnClickWrapper`
  + `PaddingWrapper`
  + `EnabledWrapper`

### Changed
* `Item.Key#builder` now requires root item, `Item.Key.Builder#build` does not require argument;
  this is done due to possible confusion of positions of items (so, each call wrap previous)
* `ItemView#bind` is overridden by default (no op)
* `AdaptViewGroup#findViewFor` uses `Item#equals` instead of `==` operator
* `AdaptView` uses `Item#equals` instead of `==` operator
* `ItemWrapper` moved to package `io.noties.adapt.wrapper`

### Deprecated
* `Item.Key.single` in favor of `Item.Key.just`

### Removed
* `ItemWrapper.Provider` and dedicated constructor `ItemWrapper#init(Provider)` are removed


# 2.3.0-SNAPSHOT
* Add `ItemViewGroup` for a group of items with the usage of `AdaptViewGroup`
* Add `ViewState` utility to save/restore view state (deprecated `NestedRecyclerState`)
* Add `@CheckResult` for `Holder.requireView` methods
* Add `AdaptViewGroup.ChangeHandler` with `ChangeHandlerDef` and `TransitionChangeHandler` implementations
* Add `AdaptViewGroup#findViewFor` and `AdaptViewGroup#findItemFor` method
* `AdaptViewGroup` checks if Item returns a View already attached to a parent
* `StickyItemDecoration`: use view-type from supplied item (for wrapped items), allow exact size of 
header (instead of assuming that height is `wrap_content`)


# 2.2.0
* create `ItemGroup` for easier nested RecyclerView support
* create `ItemLayoutWrapper` for easier wrapping of an `Item` inside a different layout
* add `HasWrappedItem` interface (2 implementations - `ItemWrapper` and `ItemLayoutWrapper`)
* utility to automatically process nested RecyclerView state - `NestedRecyclerState`
* add `Adapt#getItem(position)` method
* add `AdaptView#view()` method
* add `StickyItemDecoration` for sticky headers/sections