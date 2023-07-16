package io.noties.adapt.ui

import android.content.Context
import android.content.res.Resources
import android.view.View
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ViewElement_ExtensionsAccessibility_Test {

    @Test
    fun `accessibilityDescription - cs`() {
        val inputs = listOf(
            null,
            "Not null description"
        )
        for (input in inputs) {
            newElement()
                .accessibilityDescription(input)
                .renderView {
                    verify(this).contentDescription = eq(input)
                }
        }
    }

    @Test
    fun `accessibilityDescription - resId`() {
        val input = 99

        val text = "This is description"
        val resources = mock(Resources::class.java)
        `when`(resources.getString(eq(input))).thenReturn(text)

        newElement()
            .accessibilityDescription(input)
            .also {
                `when`(it.view.resources).thenReturn(resources)
            }
            .renderView {
                verify(this).contentDescription = eq(text)
            }
    }

    @Test
    fun accessibilityImportant() {
        val inputs = listOf(
            ImportantForAccessibility.no,
            ImportantForAccessibility.yes,
            ImportantForAccessibility.noHideDescendants,
            ImportantForAccessibility.auto,
            ImportantForAccessibility(-1233241)
        )
        for (input in inputs) {
            newElement()
                .accessibilityImportant(input)
                .renderView {
                    verify(this).importantForAccessibility = eq(input.value)
                }
        }
    }

    @Test
    fun `accessibilityImportant - boolean`() {
        val inputs = listOf(
            false to ImportantForAccessibility.no,
            true to ImportantForAccessibility.yes
        )
        for ((input, value) in inputs) {
            newElement()
                .accessibilityImportant(input)
                .renderView {
                    verify(this).importantForAccessibility = eq(value.value)
                }
        }
    }

    @Test
    fun `accessibilityLabelFor - id`() {
        val input = 9
        newElement()
            .accessibilityLabelFor(input)
            .renderView {
                verify(this).labelFor = eq(input)
            }
    }

    @Test
    fun `accessibilityLabelFor - element - initialized`() {
        // by default it is initialized with mock view
        val inputs = listOf(
            newElement().also { `when`(it.view.id).thenReturn(View.NO_ID) },
            newElement().also { `when`(it.view.id).thenReturn(652) }
        )

        for (input in inputs) {
            Assert.assertTrue(input.isInitialized)
            newElement()
                .accessibilityLabelFor { input }
                .renderView {
                    val id = input.view.id
                    if (id == View.NO_ID) {
                        // must ensure that labelFor would be called with different value
                        val captor = ArgumentCaptor.forClass(Int::class.java)
                        verify(input.view).id = captor.capture()
                        verify(this).labelFor = eq(captor.value)
                    } else {
                        verify(this).labelFor = eq(id)
                    }
                }
        }
    }

    @Test
    fun `accessibilityLabelFor - element - not initialized`() {
        // by default it is initialized with mock view
        val inputs = listOf(
            ViewElement<View, LayoutParams> { mock(View::class.java).also { it.id = View.NO_ID } },
            ViewElement<View, LayoutParams> { mock(View::class.java).also { it.id = 777 } },
        )

        for (input in inputs) {
            Assert.assertFalse(input.isInitialized)

            newElement()
                .accessibilityLabelFor { input }
                .renderView {
                    // at this point input is not initialized, view will register onView callback
                    Assert.assertEquals(1, input.viewBlocks.size)

                    input.init(mock(Context::class.java))

                    input.render()
                    // at this point input is initialized and view should receive proper callback
                    Assert.assertEquals(true, input.isInitialized)

                    val id = input.view.id
                    if (id == View.NO_ID) {
                        val captor = ArgumentCaptor.forClass(Int::class.java)
                        verify(input.view).id = captor.capture()
                        Assert.assertNotEquals(View.NO_ID, captor.value)
                        verify(this).labelFor = eq(captor.value)
                    } else {
                        verify(this).labelFor = eq(id)
                    }
                }
        }
    }

    @Test
    fun accessibilityLiveRegion() {
        val inputs = listOf(
            AccessibilityLiveRegion.none,
            AccessibilityLiveRegion.polite,
            AccessibilityLiveRegion.assertive,
            AccessibilityLiveRegion(-1231412)
        )
        for (input in inputs) {
            newElement()
                .accessibilityLiveRegion(input)
                .renderView {
                    verify(this).accessibilityLiveRegion = eq(input.value)
                }

        }
    }
}