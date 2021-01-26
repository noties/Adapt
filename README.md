# Adapt

`Adapt` &mdash; `RecyclerView.Adapter` only shorter, `ListView.Adapter` and  only shorter 
Same view in `RecyclerView`, `ListView` or any other `ViewGroup`.
Define a modular item. Extremely easy to start with modular components.

Reusable view components between `RecyclerView`(+`ViewPager2`<sup>*</sup>), `android.widget.AdapterView<?>`(`ListView`, 
`GridView`, `StackView`, `Spinner`, `AdapterViewFlipper`, +`AlertDialog`), 
and regular `ViewGroup` (`LinearLayout` inside a `ScrollView`) without
modification and out-of-box.

## Install

[![adapt](https://img.shields.io/maven-central/v/io.noties/adapt.svg?label=adapt)](http://search.maven.org/#search|ga|1|g%3A%22io.noties%22%20AND%20a%3A%22adapt%22)

```gradle
implementation "io.noties:adapt:${adaptVersion}"
```

* id for item, hash with class or provide own way


## Pros
* Interchangeable items between RecyclerView, ListView and different ViewGroups (same item is used without modification)
* Render individual item as a regular Android widget view
* Modular design is enforced, creating re-usable view components
* Ability to preview item in Layout Preview

## Cons
* Targeted at relatively small lists (under 1000?)

## NO_ID
