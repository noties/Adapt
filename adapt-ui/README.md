
## AdaptUI

<img src="../art/ui_showcase_text2.jpg" height="480px">

🚧 All showcase previews can be accessed via [dedicate page](../PREVIEW_SHOWCASE.md)

🚧 Documentation might still be a bit lacking, but most of the features in `adapt-ui` module
  come with a [dedicated sample](../sample/src/main/java/io/noties/adapt/sample/samples) class file.
  What better can explain the functionality than the code, right? ;)


## Create a view

To create a `View` with __AdaptUI__ one should use `ViewFactory`

```kotlin
val view: View = ViewFactory.createView(context) {
  Text("Hello from AdaptUI")
}
// view would be `TextView`
```

To hold a reference to a `View` nested in layout, a `reference` can be used
```kotlin
class Ref {
  // will hold a reference to created TextView
  lateinit var textView: TextView
}

val ref = Ref()

val view = ViewFactory.createView(context) {
  VStack {
    Text("My Text")
    Text("Another text")
      // pass mutable property to the `reference` extension
      .reference(ref::textView)
      // which can be also done with `onView` extension
      .onView { ref.textView = it }
  }
}

// view is LinearLayout with VERTICAL orientation

// will update referenced TextView
ref.textView.text = "Hello another text"
```

To have full control of how `ViewFactory` creates a `View`, `newView` factory builder method
can be used:
```kotlin
val view = ViewFactory.newView(viewGroup)
  .layoutParams(LayoutParams(12, 66))
  // by default ViewFactory immediately renders view,
  //  but it is possible to postpone rendering until view is attached to window
  .renderOnAttach()
  .create {
    // root view will receive passed LayoutParams(12, 66)    
    HStack {
      // just a generic View
      View()
        .background(Color.GREEN)
    }
  }
```

## ViewElement

#### Available basic elements:
* `HScroll` -&gt; `android.widget.HorizontalScrollView`
* `HStack` -&gt; `android.widget.LinearLayout` with `HORIZONTAL` orientation
* `Image` -&gt; `android.widget.ImageView`
* `Pager` -&gt; `androidx.viewpager.widget.ViewPager`
* `Progress` -&gt; `android.widget.ProgressBar`
* `Recycler` -&gt; `androidx.recyclerview.widget.RecyclerView`
* `Text` -&gt; `android.widget.TextView`
* `TextInput` -&gt; `android.widget.EditText`
* `View` -&gt; `android.view.View`
* `VScroll` -&gt; `android.widget.ScrollView`
* `VStack` -&gt; `android.widget.LinearLayout` with `VERTICAL` orientation
* `ZStack` -&gt; `android.widget.FrameLayout`

#### Composite elements:
* `HScrollStack` -&gt; `HScroll { HStack { /*children*/ } }` 
* `VScrollStack` -&gt; `VScroll { VStack { /*children*/ } }`

#### Special elements:
* `Inflated` - element that takes XML layout and adds it to layout
* `Item` - element that takes __Adapt__ `Item` (and siblings) and adds it directly to layout
* `Lazy` - similar to `ViewStub` element that adds children views to layout only when explicitly requested
* `Spacer` -&gt; `android.view.View` with `layout_weight=1` (available only inside `VStack` and `HStack`)

#### Builder elements
To facilitate creation of new elements there are 2 builtin helpers:
* `Element` -&gt to create a single `View`
* `ElementGroup` -&gt; to create a `ViewGroup`


ViewElement has 2 type arguments: `View` and `LayoutParams` (inherits from ViewGroup that created this element)
```kotlin
class ViewElement<V: android.view.View, LP: android.widget.ViewGroup.LayoutParams>()

// When building LayoutParams are inherited from created ViewGroup
//  which opens additional type-safe abilities to customize view
ViewFactory.createView(context) {
  VStack {
    // ViewElement<TextView, LinearLayout.LayoutParams>
    Text("My Text")

    ZStack {
      // ViewElement<ImageView, FrameLayout.LayoutParams>
      Image()
    }
  }
}
```


### Customization

All of `ViewElement` customizations are implemented as extension functions. If an extension
is dedicated to a specific view, it is prefixed with element's name. For example, `Text`
extensions are prefixed with `text*`, `Image` - with `image*`:
```kotlin
Text("My text")
  .textSize(16)
  .textColor(Color.RED)
  .textSingleLine()
  .textHideIfEmpty()

Image()
  .imageScaleType(ImageView.ScaleType.CENTER_CROP)
  .imageTint(Color.BLACK)
```

All extensions specify required receiver types, so it is not possible to call `textSize` on `Image` element:
```kotlin
// does not compile
Image()
  .textSize(12)
  ^^^^^^^^^^^^^
```

Extensions dedicated to `LayoutParams` are prefixed with `layout*`:
```kotlin
VStack {
  Text()
    // FILL and WRAP are special constants available in ViewFactory
    // layout(width, height) is available to all elements
    .layout(FILL, WRAP)
    // only available to element with LinearLayout.LayoutParams
    .layoutWeight(1F)
    // only available for element with MarginLayoutParams (LinearLayout, FrameLayout)
    .layoutMargin(16)
    // only available for element with MarginLayoutParams (LinearLayout, FrameLayout)
    .layoutGravity(Gravity.center.horizontal)
}
```

If extension is shared between __V__ertical and __H__orizontal variants it is prefixed with
element name but without `V` or `H`:
```kotlin
VScroll { }
  .scrollFillViewPort()
HScroll { }
  .scrollFillViewPort()
```

Extensions that provide callback-like behavior are prefixed with `on*`:
```kotlin
Text()
  .onClick { /* delivered when view is clicked */ }
  .onLongClick { /* delivered when view is long-clicked */ }
  .onDrawableStateChange { textView: TextView, stateSet -> }
  .onViewAttachedOnce { /* delivered when view becomes attached to window */ }
  .onView { textView: TextView -> /* delivered when view is available */ }
```

Extensions that are applicable to all elements unconditionally do not have any specific prefixes:
```kotlin
Text()
  .padding(16)
  .background(Color.BLACK)
  .elevation(4)

Image()
  .padding(16)
  .background(Color.BLACK)
  .elevation(4)
```

If some functionality is not covered by provided extensions `onView` allows getting
access to created view directly:

```kotlin
Text()
  // `it: TextView`
  .onView { it.isActivated = true }
```

### Create element extension

```kotlin
// extension for generic ViewElement (would be available to all elements)
//  generic type arguments are required in order to allow further
//  customizations without loosing original types
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.activated(
  activated: Boolean = true
) = onView {
  it.isActivated = activated
}

// looses original types, BEWARE - limits fluent usage
fun ViewElement<out View, *>.activated2(
  activated: Boolean = true
) = onView { 
  it.isActivated = activated 
}

ViewFactory.createView(context) {
  Text()
    .activated()
    // original type is preserved, can still customize TextView
    .textSize(16)
    .activated2()
    // no longer possible to customize TextView
    .textSize(16)
    ^^^^^^^^^^^^^
}
```

To customize an element inside a specific `ViewGroup` its `LayoutParams` can be specified
as upper bound:

```kotlin
fun <V : View, LP : LinearLayout.LayoutParams> ViewElement<V, LP>.layoutWeightAndHeight(
  height: Int
) = onLayoutParams {
  it.weight = 1F
  it.height = height.dip
}

ViewFactory.createView(context) {
  VStack {
    Text()
      .layoutWeightAndHeight(42)
  }
}
```


### Dimensions

As you might have noticed all dimensions in __AdaptUI__ are specified as literal integers.
This is so because using density-independent units is what a developer wants in most of the cases.
So, for example `Image().layout(56, 56)` would assign to created `ImageView` 56 __in density independent units__,
which for `xxhdpi` would be 168 pixels (`56 * 3F`).

```kotlin
ZStack {
  // all Density-Independent pixels
  Image()
    .layout(56, 56)
    // all normal integer operation are available, it is just an Integer
    .padding(12 + 12)
    .layoutMargin(16)
    .translation(x = 8, y = -4)
    .elevation(12)
    .minimumSize(56)
    .pivot(24, 24)

  // textSize is using Scaled Density Independent pixels
  Text()
    .textSize(16)
}
```

### Encapsulate styling

Application specific styling can be encapsulated with an extension:

```kotlin
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textPrimary() = this
  .textSize(17)
  .textTypeface(Typeface.MONOSPACE)
  .textLetterSpacing(0.1F)
  .textColor(Color.BLACK)

Text()
  .textPrimary()
  // still possible to customize it further
  // overrides value from style extension 
  .textSize(21)
```

Or with a dedicated `style` property:

```kotlin
val primary = ElementStyle.view<TextView> {
  it.textSize(17)
    .textColor(Color.BLACK)
    .textTypeface(Typeface.MONOSPACE)
}

Text()
  .style(primary)
  // also possible to customize it further
  .textSize(21)
```


## Shape

One of the cool things is that __AdaptUI__ comes with a powerful graphics addon - `Shape`. In the end
it is still a `Drawable`. But without Drawable's drawbacks:
* customization (try adding padding to an XML drawable, just to be used in one place)
* composability (try composing multiple XML drawables)

```kotlin
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

      // add a child shape explicitly
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
```

### Available Shapes
First comes DSL name, second real class name
* `Arc`: `ArcShape` - draws an arc
* `Asset`: `AssetShape` - adds an existing drawable
* `Capsule`: `CapsuleShape` - capsule shape
* `Circle`: `CircleShape` - circle shape
* `Corners`: `CornersShape` - a shape with irregular radius corners (top-left different than bottom-right)
* `Label`: `LabelShape` - single line text shape
* `Oval`: `OvalShape` - oval shape
* `Rectangle`: `RectangleShape` - rectangle shape
* `RoundedRectangle`: `RoundedRectangleShape` - rounded rectangle shape (rounded corners)
* `Text`: `TextShape` - multiline text shape based on `StaticLayout`


