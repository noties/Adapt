```
// can return nothing, but then it would require to be used as the last statement
//  as no further configuration would be possible
private fun AnyViewElement.myCustomStyle() = this
    .scrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY)
    .overScrollMode(View.OVER_SCROLL_NEVER)
    .padding(101)
```

// NB! new elements func builder can be added to a specific layout params, like `Spacer`

# TODO

* Allow overriding element function... for example to use different TextView when `Text` is called
  (or maybe disallow importing certain functions... that require explicit permission?)
  It is better to keep current semantics, but allow using different imports... it is not that safe
  can we ensure that certain elements are not imported?
* shape, if paint is supplied color with alpha, it would be considered solid (when elevating)
  and draw only part of the shadow...
* input element
* onPreDraw callback