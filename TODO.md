* explicit tags in sample (enum?), define colors for each

- [ ] `onView`... cannot specify the `this`, thus can be inconvenient with nesting
- [ ] a project without view pager would fail, as viewelementfactory would not be able to resolve it
- [ ] review all property references that we have and reduce the amount? generates additional code
- [ ] investigate the size... inline onView? and most of the extsniosn?
- [ ] createView, move to view factory, cannot import?
- [ ] text hint color
- [ ] window insets
- [ ] Int.unused in shape builder functions (where applicable)
- [ ] shape, stroke width, stroke gap and stroke dash to be relative?
- [ ] element+extensions, accessibility properties
- [ ] view, additional gestures

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