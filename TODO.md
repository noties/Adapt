* ItemContainer
* Logo for README
* SearchBar
* Padding to app icon (add icon to main app-bar)
* README, wrapper data
* explicit tags, define colors for each

* Ability to obtain `Adapt` instance directly from an `Item`
  view tags can be used and then using some view to find parent with adapt associated, but:
  - `Item` can be bound in multiple `Adapt`s
  - in a `ListView` and `RecyclerView` view parent information can not 
    be present when _binding_ (pre attached binding) and won't be present until view goes through `onAttach`
* `Adapt` update observer