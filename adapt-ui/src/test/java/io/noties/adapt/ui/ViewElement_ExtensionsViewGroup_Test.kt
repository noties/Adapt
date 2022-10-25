package io.noties.adapt.ui

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ViewElement_ExtensionsViewGroup_Test {

    @Test
    fun enabled() {
        val inputs = listOf(true, false)
        for (input in inputs) {
            val children = listOf(
                mockt<View>(),
                mockt(),
            )
            val group = mockt<ViewGroup> {
                on { this.childCount } doReturn children.size
                children.withIndex()
                    .forEach {
                        on { getChildAt(it.index) } doReturn it.value
                    }
            }
            val element = ViewElement<ViewGroup, LayoutParams> { group }.also {
                it.init(mockt())
            }
            Assert.assertEquals(group, element.view)

            // renderView first renders and then triggers callback
            element.enabled(input).renderView {
                verify(this).isEnabled = eq(input)
                children.forEach { verifyNoInteractions(it) }
            }
        }
    }

    @Test
    fun `enabled - applyToChildren`() {
        // applyToChildren would recursively apply the same value

        val inputs = listOf(true, false)
        for (input in inputs) {
            val children = listOf(
                mockt<View>(),
                mockt(),
            )
            val group = mockt<ViewGroup> {
                on { this.childCount } doReturn children.size
                children.withIndex()
                    .forEach {
                        on { getChildAt(it.index) } doReturn it.value
                    }
            }
            val element = ViewElement<ViewGroup, LayoutParams> { group }.also {
                it.init(mockt())
            }
            element.enabled(input, true).renderView {
                verify(this).isEnabled = eq(input)
                children.forEach { child ->
                    verify(child).isEnabled = eq(input)
                }
            }
        }
    }

    @Test
    fun activated() {
        val inputs = listOf(true, false)
        for (input in inputs) {
            val children = listOf(
                mockt<View>(),
                mockt(),
            )
            val group = mockt<ViewGroup> {
                on { this.childCount } doReturn children.size
                children.withIndex()
                    .forEach {
                        on { getChildAt(it.index) } doReturn it.value
                    }
            }
            val element = ViewElement<ViewGroup, LayoutParams> { group }.also {
                it.init(mockt())
            }
            element.activated(input).renderView {
                verify(this).isActivated = eq(input)
                children.forEach { verifyNoInteractions(it) }
            }
        }
    }

    @Test
    fun `activated - applyToChildren`() {
        // applyToChildren would recursively apply the same value

        val inputs = listOf(true, false)
        for (input in inputs) {
            val children = listOf(
                mockt<View>(),
                mockt(),
            )
            val group = mockt<ViewGroup> {
                on { this.childCount } doReturn children.size
                children.withIndex()
                    .forEach {
                        on { getChildAt(it.index) } doReturn it.value
                    }
            }
            val element = ViewElement<ViewGroup, LayoutParams> { group }.also {
                it.init(mockt())
            }
            element.activated(input, true).renderView {
                verify(this).isActivated = eq(input)
                children.forEach { child ->
                    verify(child).isActivated = eq(input)
                }
            }
        }
    }

    @Test
    fun clipToPadding() {
        listOf(true, false)
            .forEach {
                newElementOfType<ViewGroup>()
                    .clipToPadding(it)
                    .renderView {
                        verify(this).clipToPadding = eq(it)
                    }
            }
    }

    @Test
    fun clipChildren() {
        listOf(true, false)
            .forEach {
                newElementOfType<ViewGroup>()
                    .clipChildren(it)
                    .renderView {
                        verify(this).clipChildren = eq(it)
                    }
            }
    }

    // a combination, creates 2 view blocks
    @Test
    fun noClip() {
        newElementOfType<ViewGroup>()
            .noClip()
            .renderView {
                verify(this).clipChildren = eq(false)
                verify(this).clipToPadding = eq(false)
            }
    }
}