
# AdaptUI Flex element

__AdaptUI__ extensions that adds `Flex` element (based on [FlexboxLayout](https://github.com/google/flexbox-layout))

```kotlin
Flex {

    Text("1")
        .background(RectangleShape {
            fill(Color.RED)
        })
        .padding(16)

    Text("2")
        .background(RectangleShape {
            fill(Color.GREEN)
        })
        .padding(12)
        .layoutFlexGrow(1F)

    Text("3")
        .background(RectangleShape {
            fill(Color.YELLOW)
        })
        .layoutFlexWrapBefore(true)
        .layoutFlexGrow(1F)
        .textGravity(Gravity.center)

    Text("4")
        .layoutFlexWrapBefore(true)

}.flexDirection(FlexDirection.row)
    .flexJustifyContent(JustifyContent.center)
    .flexAlignItems(AlignItems.center)
    .flexAlignContent(AlignContent.center)
    .flexWrap(FlexWrap.wrap)
```

## Samples

[flex package](../sample/src/main/java/io/noties/adapt/sample/samples/flex)