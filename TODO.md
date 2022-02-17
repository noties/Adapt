- [ ] ability to obtain adapt instance during `bind` method
    (so we can be rendering the same item in multiple adapts, expose during `bind`, holder?)
    (holder seems to be a good candiadate, as each adapt instance creates own holder,
        so we could set instance when new holder is created. What to do when item is no longer
        attached to an Adapt, can we detect this moment? An `Holder#isAttached` method?)
    this could be problematic, as for example RecyclerView calls bind before adding
        view to layout as a mean to pre-fill
- [ ] dividers abstraction (utility to create those)

* sticky - can we maybe add tracnslationY for item transition (so it is possible to have
  transparent ones and padding?)

- [ ] collection of wrappers:
    * background color
    * text color ? can we do anything like that
    * Provider? Like an environment property?
    * selectable item background wrapper
- [ ] unique ids based on source position (hard, throwing throwable for each one?)

* ItemContainer
* explicit tags, define colors for each

* Ability to obtain `Adapt` instance directly from an `Item`
  view tags can be used and then using some view to find parent with adapt associated, but:
  - `Item` can be bound in multiple `Adapt`s
  - in a `ListView` and `RecyclerView` view parent information can not 
    be present when _binding_ (pre attached binding) and won't be present until view goes through `onAttach`
* `Adapt` update observer