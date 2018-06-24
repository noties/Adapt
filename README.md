# Adapt

`RecyclerView.Adapter` only shorter.

[![adapt](https://img.shields.io/maven-central/v/ru.noties/adapt.svg?label=adapt)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22adapt%22)

```gradle
implementation 'ru.noties:adapt:1.0.0'
```

---

Implementation of `RecyclerView.Adapter` that allows defining reusable item components.

```java
final Adapt<Item> adapt = Adapt.builder(Item.class)
        .include(SectionItem.class, new SectionView2())
        .include(ShapeItem.class, new ShapeView())
        .include(AppendItem.class, new AppendView(), new ViewProcessor<AppendItem>() {
            @Override
            public void process(@NonNull AppendItem item, @NonNull View view) {
                
            }
        })
        .build();
```

```java
final List<Item> items = obtainItems();
adapt.setItems(items);
```

```java
public class SectionView2 extends ItemView<SectionItem, DynamicHolder> {

    @NonNull
    @Override
    public DynamicHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new DynamicHolder(inflater.inflate(R.layout.view_section, parent, false));
    }

    @Override
    public void bindHolder(@NonNull DynamicHolder holder, @NonNull final SectionItem item) {
        holder
                .on(R.id.text, new DynamicHolder.Action<TextView>() {
                    @Override
                    public void apply(@NonNull TextView view) {
                        view.setText(item.name());
                    }
                })
                .on(R.id.text, new DynamicHolder.Action<View>() {
                    @Override
                    public void apply(@NonNull View view) {
                        // hey another one call chained
                    }
                });
    }
}
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