attempt at creating proper abstraction for re-usable view components across different contexts

# Item
* ~~Override `equals` when in RecyclerView and diff is used to detect if contents are the same~~

* change `render` -> `bind`
* view group, items with the same id (although different objects) would throw index of bounds
  * should duplicates be detected and removed _automatically_?
  * can we have no-check for viewgroup? so, items are bound and that's it, one item one view, no id check
  * at least exception should be different (not index out of bounds)
* maybe make no holder thing, so every item is like inflated item, but there is a possibility to provide holder... why even?