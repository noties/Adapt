# Adapt

`Adapt` &mdash; `RecyclerView.Adapter`, only shorter. Same view in `RecyclerView` or any `ViewGroup`.
Define a modular item. Extremely easy to start with modular components.

## Install

[![adapt](https://img.shields.io/maven-central/v/io.noties/adapt.svg?label=adapt)](http://search.maven.org/#search|ga|1|g%3A%22io.noties%22%20AND%20a%3A%22adapt%22)

```gradle
implementation "io.noties:adapt:${adaptVersion}"
```

* id for item, hash with class or provide own way


## Pros
* Interchangeable items between RecyclerView and different ViewGroups (same item is used without modification)
* Ability to preview item in Layout Preview
* Modular design is enforced, creating re-usable view components

## Cons
* Targeted at relatively small lists (under 1000?)

## NO_ID
