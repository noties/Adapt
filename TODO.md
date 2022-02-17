- [ ] add default toString to item, simple class name, id + hash?
- [ ] selectable item background wrapper
- [ ] collection of wrappers:
    * background color
    * text color ? can we do anything like that
    * Provider? Like an environment property?
- [ ] ability to obtain adapt instance during `bind` method
    (so we can be rendering the same item in multiple adapts, expose during `bind`, holder?)
- [ ] `ItemLayout` bind method can be overridden by default (empty)
- [ ] dividers abstraction (utility to create those)
- [ ] unique ids based on source position (hard, throwing throwable for each one?)

* sticky - can we maybe add tracnslationY for item transition (so it is possible to have
  transparent ones and padding?)

* ItemContainer
* explicit tags, define colors for each

* Ability to obtain `Adapt` instance directly from an `Item`
  view tags can be used and then using some view to find parent with adapt associated, but:
  - `Item` can be bound in multiple `Adapt`s
  - in a `ListView` and `RecyclerView` view parent information can not 
    be present when _binding_ (pre attached binding) and won't be present until view goes through `onAttach`
* `Adapt` update observer