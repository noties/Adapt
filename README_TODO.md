# Adapt and AdaptUI

## What it is?

AdaptUI came as a solution to reduce context switches between code and various XML files. Along with
the ability to sligthly tweak some XML layouts, drawables or styles without copying data just in
order to change an attribute or two.

With AdaptUI it is possible to create reusable, configurable and tweakable views and drawables
without leaving a single file in which they are defined. Combine that with the build-in layout
preview that comes with latest versions of Android Studio and you can have a truly single-file
components on Android. A single-file component reduces context switches, allows inspection and
modification of view building blocks meanwhile giving an immediate feedback with the help of the
preview. It keeps the loop running without the breaks.

Moreover, with AdaptUI does not make commitment.. It does not require a special compiler - all it is
using is Kotlin code. All it operates on - native Android views. It does provide conveniences on top
of native views, but does not restrict access. You still can access a view underneath, create own
elements, or extensions on elements.

AdaptUI has been inspired by Flutter, Combine and SwiftUI. Important difference from Flutter is the
direction is which elements are build - in Flutter you wrap target views with customization views,
like Padding, SizedBox, etc, so in the end initial view becomes wrapped under multiple layers of
customization - like a cabbage. It hurts readability and discoverability. Views also expect to
receive all its arguments in constructor, which leaves little Flutter is like a pockemon cabbage.
Target (conceptually important)
views are hidden by multiple pockemon layers with weird names and zero discoverability. In order to
find what it is inside (what view)
it holds you need to unwrap the layers, which make a lot of noise whilst you do that. Easier to copy
code, see how it behaves, just comment related code lines, no need to modify structure. The same is
re

Another things that {{positively}} distinguishes AdaptUI is AdaptUI does not come with any state
system, it can work with any.

easily discoverable customizations ease of creating a quick sketch with primary views, then
gradually updating them to match final design ability to comment parts of customization (flutter
complicated, XML does not allow comments for attributes)

## How?

```kotlin
// creates a Text `ViewElement` that wraps `TextView`
Text()
```

`Text()` is an extension function of a `ViewFactory`. Elements should be created
inside `ViewFactory` context, for example:

```kotlin
// TextView would be returned for the `ViewFactory.createView`
val textView = ViewFactory.createView(context) {
    Text()
}
```

AdaptUI provides these view elements out-of-box (adding a new one is a metter of creating a new
extension function):

* `Text()` -> `TextView`
* `HScroll()` -> `HorizontalScrollView`
* `HStack()` -> `LinearLayout` with `HORIZONTAL` orientation
* `Image()` -> `ImageView`
* `Pager()` -> `androidx.viewpager.ViewPager` (`androidx.viewpager` should be added to your
  dependencies list explicitly)
* `Progress()` -> `ProgressBar` with indeterminate progress
* `Spacer()` -> `View`, an element that can be used only as a child of `HStack`
  and `VStack` (`LinearLayout`), specifies `layout_weight`
* `TextInput()` -> `EditText`
* `View()` -> `View`
* `VScroll()` -> `ScrollView`
* `VStack()` -> `LinearLayout` with `VERTICAL` orientation
* `ZStack()` -> `FrameLayout`

Those are the building blocks defined directly in AdaptUI. But any android view or view-group can be
represented as one

```kotlin
VStack {
    Element(::CheckBox) // ViewElement<CheckBox, LinearLayout.LayoutParams>
    // the same as
    Element { context -> CheckBox(context) }
}
```

Each element also holds information about `LayoutParams`, so it is possible to configure them in a
type-safe manner

```kotlin
VStack {
    Text() // ViewElement<TextView, LinearLayout.LayoutParams>
        .layoutWeight(1F)
    Image() // ViewElement<ImageView, LinearLayout.LayoutParams>
        .layoutGravity(Gravity.trailing.end)
    // is available only for LinearLayout.LayoutParams
    Spacer()
}
```

AdaptUI follows a simple naming patterns for customization. For example, all layout customizations
that affect LayoutParams start with `layout*` -
`layoutWeigth`, `layoutGravity`, `layoutMargin` etc. Individual elements prefix customization
functions with its name. Those for example are some customizations available for the `Text` element:

* `text(CharSequence)`
* `textGravity(Gravity)`
* `textColor(Int)`
* `textHideIfEmpty()`
* and others

All of the customizations that affect dimensions
(width, height, padding, margin, etc) are already in DIP (density independent pixels), there is no
need to convert them explicitly to pixels

```kotlin
Text()
    .padding(16) // 16.dp
    .layoutMargin(vertical = 8) // 8.dp
    .layout(FILL, 128) // 128.dp
```

When building _static_ layouts all normal code flows are working as expected

```kotlin
VStack {

    if (someCondition) {
        Text("Some condition was met")
    } else {
        HStack {
            Image(error)
            Text("Condition was not met")
        }
    }

    // creates 10 Texts
    (0..9).forEach {
        Text("$it")
    }
}
```

Elements can still be referenced as regular objects

```kotlin
VStack {

    // it is referenced, but still added to the layout
    val image = Image() // ViewElement<ImageView, LinearLayout.LayoutParams>

    Text("Button-like")
        .padding(16)
        .background(Colors.accent)
        .onClick {
            // modify element and call render
            // NB! `render` is required when element is modified outside view-building phase
            image.render { background(Colors.primary) }

            // or access view directly
            // here we can access the view because it has been already initialized
            image.view.setBackgroundColor(Colors.primary)
        }
}
```



