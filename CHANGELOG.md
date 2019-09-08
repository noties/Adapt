# Changelog

## 2.2.0
* create `ItemGroup` for easier nested RecyclerView support
* create `ItemLayoutWrapper` for easier wrapping of an `Item` inside a different layout
* add `HasWrappedItem` interface (2 implementations - `ItemWrapper` and `ItemLayoutWrapper`)
* utility to automatically process nested RecyclerView state - `NestedRecyclerState`
* add `Adapt#getItem(position)` method
* add `AdaptView#view()` method
* add `StickyItemDecoration` for sticky headers/sections