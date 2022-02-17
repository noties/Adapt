# Changelog


# Unreleased
### Added
* `Item.Wrapper` and `Item#wrap`
* Item default `toString` implementation

### Changed
* `ItemView#bind` is overridden by default (no op)
* `AdaptViewGroup#findViewFor` uses `Item#equals` instead of `==` operator
* `AdaptView` uses `Item#equals` instead of `==` operator

### Removed
* `ItemWrapper.Provider` and dedicated constructor `ItemWrapper#init(Provider)` are removed


# 2.3.0-SNAPSHOT
* Add `ItemViewGroup` for a group of items with the usage of `AdaptViewGroup`
* Add `ViewState` utility to save/restore view state (deprecated `NestedRecyclerState`)
* Add `@CheckResult` for `Holder.requireView` methods
* Add `AdaptViewGroup.ChangeHandler` with `ChangeHandlerDef` and `TransitionChangeHandler` implementations
* Add `AdaptViewGroup#findViewFor` and `AdaptViewGroup#findItemFor` method
* `AdaptViewGroup` checks if Item returns a View already attached to a parent
* `StickyItemDecoration`: use view-type from supplied item (for wrapped items), allow exact size of 
header (instead of assuming that height is `wrap_content`)


# 2.2.0
* create `ItemGroup` for easier nested RecyclerView support
* create `ItemLayoutWrapper` for easier wrapping of an `Item` inside a different layout
* add `HasWrappedItem` interface (2 implementations - `ItemWrapper` and `ItemLayoutWrapper`)
* utility to automatically process nested RecyclerView state - `NestedRecyclerState`
* add `Adapt#getItem(position)` method
* add `AdaptView#view()` method
* add `StickyItemDecoration` for sticky headers/sections