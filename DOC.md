attempt at creating proper abstraction for re-usable view components across different contexts

# Item
* ~~Override `equals` when in RecyclerView and diff is used to detect if contents are the same~~

* view group, items with the same id (although different objects) would throw index of bounds
  * should duplicates be detected and removed _automatically_?
  * can we have no-check for viewgroup? so, items are bound and that's it, one item one view, no id check
  
* adaptView implement Adapt

* `Adapt.notifyItemChanged` can also return a boolean indicating if adapt contains such an item