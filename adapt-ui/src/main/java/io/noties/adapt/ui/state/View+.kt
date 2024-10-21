package io.noties.adapt.ui.state

import android.view.View

val View.viewState: ViewState get() = ViewState(drawableState.toSet())
// setter does not seem to be very required right now (setting a view pressed or focused?)