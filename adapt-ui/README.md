
* how to extend (which types to use, downsides, for example, direct `ViewGroup.Layout` would match
  only that but not decendants)
  
```
// can return nothing, but then it would require to be used as the last statement
//  as no further configuration would be possible
private fun AnyViewElement.myCustomStyle() = this
    .scrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY)
    .overScrollMode(View.OVER_SCROLL_NEVER)
    .padding(101)
```

// NB! layout definitions cannot be extended (onLayout, etc to the resque)
// NB! new elements func builder can be added to a specific layout params, like `Spacer`