- [ ] processor to generate strings, colors, dimensions
- [ ] dialog (with window-insets handling)
- [ ] update logo, make gradient and new asset
- [ ] `AdaptUIScrollIfScrollsSample` transition for parent and layout fill (maybe missing), review all samples 
   that could be affected by new view layout (so maybe they need to be set to match-parent)
- [ ] unify preview class names, `Preview_Sample`, `PreviewSample`, `Preview__Sample`
- [ ] verify all tags are specified in `Tags` object
- [ ] move `PreviewUtils` (adapt + adapt-ui) to `preview` package instead of `util`
- [ ] add consumer proguard to remove preview layouts (ui and regular)
- [ ].
  > Adapt README Item is a chunk of view logic (along with actual view attached) that could be
  >  rendered and passed around.
- [ ].
  > Resource generator for colors, strings, drawables? (drawables? like icons)
- [ ].
  > Image loader (`AsyncImage`)
- [ ] Shape.shadow Text/Label.textShadow ColorBuilder

- [ ] VScroll and HScroll are actually expose ViewFactory<FrameLayout.LayoutParams>, so
  nested children might be able to add certain FrameLayout elements or layout customization,
  meanwhile they are in different context.

- [ ] `Text` autosize must be applied when text changes (maxLines?)
- [ ] SHOW, a layout with rounded background, icon and text => just a text with padding and shape
  plus, clickable, foreground, cliptooutline
- [ ] shape, padding, for ex top, can result in rect.top be greater than bottom (we do not touch
  bottom)...
  NOPE, convert to dp

- [ ] stateful-shape:
  ```
  // TODO: maybe make more fluent, like
  //  focused = shape ??
  ```
- [ ] text, text res so locale is automatically taken from context

- [ ] review wrappers in adapt (padding and edges)
- [ ] adapt element, makre onAdapt accept it instead o this
- [ ] maybe make viewElement open? but what would we achieve? most extensions use `ViewElement`,
  so type information would not be preserved

- [ ] review all property references that we have and reduce the amount? generates additional code
- [ ] investigate the size... inline onView? and most of the extsniosn?
- [ ] window insets
- [ ] view, additional gestures

---------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------
---- DONE - DONE - DONE - DONE - DONE - DONE - DONE - DONE - DONE - DONE - DONE - DONE - DONE -----
---------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------

- [x] run all tests (sample.verifyPaDe) - added `test.sh`
- [X] StatfulShape.create instead of `drawable`
- [-] maybe LP typealias? does ot solve anything, as we still need to specify generic variant, which
  would cause name collision
  ==: yes, `LP` does not bring any benefits only trouble with generics
- [X] shape-drawable stateful handling
  ==: In the end changed implementation to `ViewState`, which is a little better abstraction
  `DrawableState` and all its usages are deprecated
- [X]
  > refactor DrawableState to be fluent, right now a little confusing, let it be:
  `pressed.enabled` => DrawableState(attrs: Array<@Attr Int>)
  DrawableStateSet.pressed (to check if contains should be renamed, like isPressed or hasPressed?)
  == in the end done differently
- [X]] on view pred draw should have `once` as it delivers callback only once
- [X] element+extensions, accessibility properties
- [-] common interface for shape and stateful-shape
  // done by different state builder
- [X] explicit tags in sample (enum?), define colors for each
- [-] view group, diff, obtain same type and bind if id is different
  this would complicate current simple (adn transition-ready) diff, as we would need to lookup
  if item is present in the list further, so we can safely reuse it
- [x] factory, lock after used
- [?] Int.unused in shape builder functions (where applicable)
- [x] `onView`... cannot specify the `this`, thus can be inconvenient with nesting
- [x] element to include item directly in layout (wraps adaptview)
- [x] view-stub like element
- [?] shape, stroke width, stroke gap and stroke dash to be relative?
  UPD, seems to have very little value
- [x] createView, move to view factory, cannot import?
- [x] text hint color
- [x] a project without view pager would fail, as viewelementfactory would not be able to resolve it
  seems to be working now, as view-pager is not importaed, but referenced by full-name
- [X] layout width|height when used with layoutWeight - anyway it is a proper way to define such a
  view it is better to make weight explicit in `layout` function call for LinearLayout
- [X] shape rotation
- [?] asset with stroke.. as there are a lot of drawables.. the task bec
- [x] view, additional lifecycle callbacks, like onAttach, onDetach, onViewPreDraw, etc
- [-] addTo in ViewElement, tests and validate all properly initialy it, referencing factory can be
  confusing, moreover it can lead to unexpected results
- [x] clipToOutline to allow clipping view by using the shape
- [x] castLayout when inside an adapt item is not working, as by default just viewgroup params are
  set because by default element-item is using default parameters provided by view-factory

Size of adapt-ui release binary

- `421KB` with toString and properties
- `366KB` with static toString in shape and gradient
