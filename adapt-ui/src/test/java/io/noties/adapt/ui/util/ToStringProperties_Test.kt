package io.noties.adapt.ui.util

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ToStringProperties_Test {

    private class Ref {
        var text: String? = null
        var number: Float? = null
        var flag: Boolean? = null
        var color: Int? = null
    }

    @Test
    fun empty() {
        val ref = Ref()
        val inputs = listOf(
            toStringProperties { /* no calls here */ },
            toStringProperties { it() }, // empty vararg call
            toStringProperties {
                it(
                    ref::text,
                    ref::number,
                    ref::flag,
                    ref::color
                )
            } // all null properties values
        )
        for (input in inputs) {
            assertEquals("", input)
        }
    }

    @Test
    fun it() {
        val inputs = listOf(
            "text='1234'" to Ref().apply { text = "1234" },
            "number=1.0" to Ref().apply { number = 1F },
            "flag=true" to Ref().apply { flag = true },
            "color=0" to Ref().apply { color = 0 }
        )
        for ((text, ref) in inputs) {
            val result = toStringProperties {
                it(
                    ref::text,
                    ref::number,
                    ref::flag,
                    ref::color
                )
            }
            assertEquals(text, result)
        }
    }

    @Test
    fun `it - map`() {
        val text = "09876-hello"
        val color = 0xFF00ff00.toInt()

        val ref = Ref().apply {
            this.text = text
            this.color = color
        }
        val string = toStringProperties {
            it(ref::text)
            it(ref::color) {
                it?.toHexString()
            }
        }
        assertEquals("text='$text', color='#FF00FF00'", string)
    }

    @Test
    fun `default - name`() {
        val name = "RTYUWHnm1e21"
        val color = 981

        val ref = Ref().apply {
            this.color = color
        }
        val result = toStringPropertiesDefault(name) {
            it(ref::color)
            it(ref::text)
        }
        assertEquals("$name(color=$color)", result)
    }

    @Test
    fun `default - self`() {
        val ref = Ref().apply {
            flag = true
        }
        val result = toStringPropertiesDefault(ref) {
            it(ref::flag)
        }
        assertEquals("Ref(flag=true)", result)
    }
}