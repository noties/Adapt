<img src="./art/logo_social.png" width="90%" alt="logo" />

# Adapt &amp; AdaptUI 

__Adapt__ is a UI library to create decoupled widget components. They can be
  used in a `RecyclerView`, `ListView`, inside a `LinearLayout` or used directly as a `View` interchangeably, 
  no code involved. One `Item` to rule them all. Layout preview enabled.

__AdaptUI__ is en enhanced Android view DSL builder that brings together dynamism 
  and flexibility of __Adapt__ to native Android views. It aims to provide convenience 
  and peace of mind for developers, meanwhile fixing pain points 
  of Android XML - missing composability, reuse and customization. They are all included out of box. 
  It is a _disappearing_
  view and layout builder that gives total control of created views, without imposing any
  limitations or forcing the use of certain tools or compilers. A view is a view. As it should have been.

## Install

[![adapt](https://img.shields.io/maven-central/v/io.noties/adapt.svg?label=adapt)](http://search.maven.org/#search|ga|1|g%3A%22io.noties%22%20AND%20a%3A%22adapt%22)

```gradle
implementation "io.noties:adapt:${adaptVersion}"
implementation "io.noties:adapt-ui:${adaptVersion}"
implementation "io.noties:adapt-ui-flex:${adaptVersion}"
```

## [AdaptUI](./adapt-ui/README.md)
<img src="./art/ui_showcase_text2.jpg" height="480px">

🚧 All showcase previews can be accessed via [dedicate page](./PREVIEW_SHOWCASE.md)

🚧 Documentation might still be a bit lacking, but most of the features in `adapt-ui` module
  come with a [dedicated sample](./sample/src/main/java/io/noties/adapt/sample/samples) class file. 
  What better can explain the functionality than the code, right? ;)


## [Adapt](./adapt/README.md)

![gif](./art/preview.gif)
![XML layout-preview](./art/layout_preview.png)

🚧 Documentation might still be a bit lacking, but most of the features in `adapt` module
  come with a [dedicated sample](./sample/src/main/java/io/noties/adapt/sample/samples) class file.
  What better can explain the functionality than the code, right? ;)

## License

```
  Copyright 2021 Dimitry Ivanov (legal@noties.io)

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