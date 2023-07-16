
# Generate a new AdaptUISampleView with preview
Create a new Kotlin file with name for the sample

```
@io.noties.adapt.sample.annotation.AdaptSample(
    id = $ID$,
    title = "$TITLE$",
    description = "$DESCRIPTION$",
    tags = ["adapt-ui", $TAGS$]
)
class $SAMPLE_NAME$: io.noties.adapt.sample.samples.adaptui.AdaptUISampleView() {
    override fun io.noties.adapt.ui.ViewFactory<io.noties.adapt.ui.LayoutParams>.body() {
        $END$
    }
}

@io.noties.adapt.sample.util.Preview
@Suppress("ClassName", "unused")
private class Preview__$SAMPLE_NAME$(
    context: android.content.Context, 
    attrs: android.util.AttributeSet?
) : io.noties.adapt.sample.util.PreviewSampleView(context, attrs) {
    override val sampleView: io.noties.adapt.sample.SampleView
        get() = $SAMPLE_NAME$()
}
```