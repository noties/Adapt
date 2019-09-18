# Changelog

## 2.3.0-SNAPSHOT
* Add `ItemViewGroup` for a group of items with the usage of `AdaptViewGroup`
* Add `ViewState` utility to save/restore view state (deprecated `NestedRecyclerState`)
* Add `@CheckResult` for `Holder.requireView` methods
* Add `HasChildrenItems` common interface for a group item (`ItemGroup` and `ItemViewGroup`)
* Add `Adapt#onSaveInstanceState` and `Adapt#onRestoreInstanceState` for easier state saving/restoration
* Add `AdaptViewGroup.ChangeHandler` with `ChangeHandlerDef` and `TransitionChangeHandler` implementations
* Add `AdaptViewGroup#findItemForView` method
* `AdaptViewGroup` checks if Item returns a View already attached to a parent

## 2.2.0
* create `ItemGroup` for easier nested RecyclerView support
* create `ItemLayoutWrapper` for easier wrapping of an `Item` inside a different layout
* add `HasWrappedItem` interface (2 implementations - `ItemWrapper` and `ItemLayoutWrapper`)
* utility to automatically process nested RecyclerView state - `NestedRecyclerState`
* add `Adapt#getItem(position)` method
* add `AdaptView#view()` method
* add `StickyItemDecoration` for sticky headers/sections