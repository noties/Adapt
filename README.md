# Adapt

`RecyclerView.Adapter` only shorter. Implementation of `RecyclerView.Adapter` that allows defining reusable item components.

## Install

[![adapt](https://img.shields.io/maven-central/v/ru.noties/adapt.svg?label=adapt)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22adapt%22)

```gradle
implementation "ru.noties:adapt:${adaptVersion}"
```

## Usage

To obtain an instance of `Adapt` one must use `#builder` factory method:

```java
Adapt.builder(Item.class)
```

`Item.class` is a base type of all _items_ that `Adapt` instance will hold and render. It can be an **interface** or **concrete implementation**. All _items_ that are going to be added must either _implement_ or _extend_ base type.

```java
final Adapt<Item> adapt = Adapt.builder(Item.class)
        .include(SectionItem.class, new SectionView())
        .build();
```

Where `SectionItem` is:
```java
public class SectionItem extends Item {

    private final String name;

    public SectionItem(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String name() {
        return name;
    }
}
```

`SectionItem` is merely a data-holder (POJO) that represents certain data portion that needs to be rendered on screen.

### ItemView

In order to _bind_ this data to a visual representation `ItemView` class is used.

```java
public class SectionView extends ItemView<SectionItem, SectionView.SectionHolder> {
    
    @NonNull
    @Override
    public SectionHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new SectionHolder(inflater.inflate(R.layout.view_section, parent, false));
    }

    @Override
    public void bindHolder(@NonNull SectionHolder holder, @NonNull SectionItem item) {
        holder.text.setText(item.name());
    }

    static class SectionHolder extends Holder {

        final TextView text = requireView(R.id.text);

        SectionHolder(@NonNull View view) {
            super(view);
        }
    }
}
```

`ItemView` has some additional methods that one can override:
* `void bindHolder(@NonNull H holder, @NonNull T item, @NonNull List<Object> payloads)`
* `long itemId(@NonNull T item)`
* `void onViewRecycled(@NonNull H holder)`
* `Context context(@NonNull Holder holder)`

By default `itemId` returns hashcode of supplied item. Make sure that you either override this method in your `ItemView` or implement `hashcode()` in your item class (do not forget about `equals()` too)

> **Please note** that one does not work directly `itemViewType`. `Adapt` library does this automatically by using supplied item class `hashcode()` value as the key.

### Holder

`Holder` is a subclass of `RecyclerView.ViewHolder`. It has some additional methods:
* `findView(@IdRes int)`
* `requireView(@IdRes int)`

#### findView

Returns `@Nullable` View, so if a View with supplied id is not found in layout, `null` will be returned

#### requireView
Will never return `null`. Instead an `AdaptError` will be thrown if a view with specified id is not found in layout. Always prefer using this method if you _expect_ a view to be present.

### DynamicHolder

`Adapt` comes with a `Holder` implementation that does not need _subclassing_

```java
public class SectionView2 extends ItemView<SectionItem, DynamicHolder> {

    @NonNull
    @Override
    public DynamicHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new DynamicHolder(inflater.inflate(R.layout.view_section, parent, false));
    }

    @Override
    public void bindHolder(@NonNull DynamicHolder holder, @NonNull final SectionItem item) {
        final TextView textView = holder.requireView(R.id.text);
        textView.setText(item.name());
    }
}
```

Internally it caches requested views so it's performance is on par with a `Holder` subclass.

Additionally there is convenience method to process views:
```java
@Override
public void bindHolder(@NonNull DynamicHolder holder, @NonNull final SectionItem item) {
    holder.
            on(R.id.text, new DynamicHolder.Action<TextView>() {
                @Override
                public void apply(@NonNull TextView textView) {
                    textView.setText(item.name());
                }
            });
}
```

`#on` method returns `DynamicHolder` instance, so it's possible to chain calls:

```java
@Override
public void bindHolder(@NonNull DynamicHolder holder, @NonNull final SectionItem item) {
    holder.
            on(R.id.text, new DynamicHolder.Action<TextView>() {
                @Override
                public void apply(@NonNull TextView textView) {
                    textView.setText(item.name());
                }
            })
            .on(R.id.recycler_view, new DynamicHolder.Action<RecyclerView>() {
                @Override
                public void apply(@NonNull RecyclerView view) {

                }
            });
}
```

### ViewProcessor

It's a pretty common task to apply `OnClickListener` or `OnLongClickListener` to resulting View that is being displayed in a `RecyclerView`. `Adapt` comes with a simple abstraction that will help achieve that. One can specify `ViewProcessor` when building `Adapt` instance:

```java
final Adapt<Item> adapt = Adapt.builder(Item.class)
        .include(SectionItem.class, new SectionView(), new ViewProcessor<SectionItem>() {
            @Override
            public void process(@NonNull SectionItem item, @NonNull View view) {
                
            }
        })
        .build();
```

For convenience there are:
* `OnClickViewProcessor`
* `OnLongClickViewProcessor`

#### OnClickViewProcessor
Can be created directly:
```java
new OnClickViewProcessor<SectionItem>() {
    @Override
    public void onClick(@NonNull SectionItem item, @NonNull View view) {

    }
})
```

or via `#create` factory method:

```java
OnClickViewProcessor.create(new OnClickViewProcessor.OnClick<SectionItem>() {
    @Override
    public void onClick(@NonNull SectionItem item, @NonNull View view) {

    }
})
```

#### OnLongClickViewProcessor
Can be created directly:
```java
new OnLongClickViewProcessor<SectionItem>() {
    @Override
    public boolean onLongClick(@NonNull SectionItem item, @NonNull View view) {
        return false;
    }
}
```

Or via `#create` factory method:
```java
OnLongClickViewProcessor.create(new OnLongClickViewProcessor.OnLongClick<SectionItem>() {
    @Override
    public boolean onLongClick(@NonNull SectionItem item, @NonNull View view) {
        return false;
    }
})
```

### AdaptUpdate

In order to _dispatch_ updates to a `RecyclerView` `AdaptUpdate` is used. Library provides 2 implementations:
* `DiffUtilUpdate`
* `NotifyDataSetChangedUpdate`

#### DiffUtilUpdate

There are 2 factory methods to obtain an instance of `DiffUtilUpdate`:
* `#create()`
* `#create(Callback)`

`#create()` factory method creates a `DiffUtilUpdate` with default implementation of the `Callback`:
* `detectMoves=true`
* `areItemsTheSame` checks `oldItem.equals(newItem)`
* `areContentsTheSame` returns `true`
* `getChangePayload` returns `null`

If this doesn't answer your needs you can simply override specified methods to provide functionality that suits you most.

#### NotifyDataSetChangedUpdate

Is a simple implementation that calls `adapter.notifyDataSetChanged()`. It can be obtained via `#create` factory method:

```java
NotifyDataSetChangedUpdate.create();
```

---

```java
final Adapt<Item> adapt = Adapt.builder(Item.class)
        .include(SectionItem.class, new SectionView())
        .adaptUpdate(DiffUtilUpdate.<Item>create())
        .build();
```

Note that if `AdaptUpdate` is not provided explicitly `NotifyDataSetChangedUpdate` would be used.

### Stable Ids

```java
final Adapt<Item> adapt = Adapt.builder(Item.class)
        .include(SectionItem.class, new SectionView())
        .hasStableIds(true)
        .build();
```

By default this value is `true`.


### Layout Inflater

```java
final Adapt<Item> adapt = Adapt.builder(Item.class)
        .include(SectionItem.class, new SectionView())
        .layoutInflater(LayoutInflater.from(context))
        .build();
```

Specify `LayoutInflater` to be used when inflating `ItemView`s. If not specified `LayoutInflater` will be automatically obtained.

---

```java
final Adapt<Item> adapt = Adapt.builder(Item.class)
        .include(SectionItem.class, new SectionView())
        .adaptUpdate(NotifyDataSetChangedUpdate.<Item>create())
        .layoutInflater(LayoutInflater.from(context))
        .hasStableIds(true)
        .build();

final RecyclerView recyclerView = findViewById(R.id.recycler_view);
recyclerView.setAdapter(adapt.recyclerViewAdapter());

adapt.setItems(obtainItems());
```

## License

```
  Copyright 2018 Dimitry Ivanov (mail@dimitryivanov.ru)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```