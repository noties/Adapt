<img src="./assets/adapt-social.jpg" width="90%" alt="logo" />

# Adapt &amp; AdaptUI

## Install

[![adapt](https://img.shields.io/maven-central/v/io.noties.adapt/adapt.svg?label=adapt)](http://search.maven.org/#search|ga|1|g%3A%22io.noties.adapt%22%20AND%20a%3A%22adapt%22)

```gradle
implementation platform("io.noties.adapt:bom:$adaptVersion")

implementation 'io.noties.adapt:adapt'
implementation 'io.noties.adapt:adapt-kt'
implementation 'io.noties.adapt:adapt-ui'
implementation 'io.noties.adapt:adapt-ui-flex'
```

## [AdaptUI](./adapt-ui/README.md)

> Fluent (no-xml) Android-View DSL in Kotlin.

With flexibility in mind and total control over the process.
Can be used as an enhancement over existing native Android widgets and layouts.
Creates advanced Android views and layouts in openly-explorable and readable way.
Influenced by SwiftUI. Adapted to Kotlin.<!-- Android Compose? -->

<img src="./assets/showcase/ui_showcase_text2.jpg" height="480px">
<a href="./PREVIEW_SHOWCASE.md">🖼️ More previews like this</a><br /><br />
<a href="./adapt-ui/README.md">➡️ Continue reading</a>

---

🚧 \[Documentation is under construction]. Meanwhile, most of the features
  come with a [dedicated sample or samples](./sample/src/main/java/io/noties/adapt/sample/samples). They include
  always relevant code that could be also previewed in the [installed sample application](./releases/latest).
  Along with Layout Preview in Android Studio to play-around.

---

## [Adapt](./adapt/README.md)

Android **true** adapter. ViewGroup _agnostique_ adapter that interchangeably
renders items across `RecyclerView`, `ListView`, `ViewPager`, `LinearLayout`, `FlexboxLayout`, or any other `ViewGroup`.

![gif](./assets/preview.gif)
![XML layout-preview](./assets/layout_preview.png)

[➡️ Continue reading](./adapt/README.md)

## License

```
  Copyright 2026 Dimitry Ivanov (hey@noties.io)

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
