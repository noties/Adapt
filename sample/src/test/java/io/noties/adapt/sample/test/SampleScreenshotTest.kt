package io.noties.adapt.sample.test

import android.app.Application
import android.content.Context
import android.view.View
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import io.noties.adapt.sample.Sample
import io.noties.adapt.sample.ui.color.background
import io.noties.adapt.sample.util.SampleUtil
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.App
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layoutFill
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class SampleScreenshotTest(private val sampleId: String) {

    companion object {
        // @BeforeClass is run AFTER parameters are obtained, thus there is init error
//        @BeforeClass
//        @JvmStatic
//        fun before() {
//            App.shared = object: Application() {
//                init {
//                    attachBaseContext(RenderAction.getCurrentContext())
//                }
//            }
//        }

        @JvmStatic
        @Suppress("unused")
        @get:Parameters(name = "{0}")
        val parameters: List<String>
            get() {
                // it is a little complicated obtain Context here to get samples
                //  from assets, thus samples.json was put in java.resources to be loaded when requested
                return SampleScreenshotTest::class.java.classLoader!!
                    .getResourceAsStream("samples.json")
                    .let {
                        SampleUtil.readSamples(it)
                    }
                    .map { it.id }
                    .let {
                        // paparazzi cannot render web-view
                        val skipped = listOf("20230716140149")
                        it
                            .filter { id -> !skipped.contains(id) }
                    }
            }
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.NEXUS_5,
        showSystemUi = false
    )

    @Test
    fun test() {
        App.shared = object : Application() {
            init {
                attachBaseContext(paparazzi.context)
            }
        }

        val sample = SampleUtil.samples.first { it.id == sampleId }

        paparazzi.snapshot(
            view = sample.createView(paparazzi.context),
        )
    }

    private fun Sample.createView(context: Context): View {
        val sample = this
        return ViewFactory.createView(context) {
            ZStack {
                Element {
                    SampleUtil.createSampleView(sample)
                        .createView(it)
                }
            }.indent()
                .layoutFill()
                .backgroundColor { background }
        }
    }
}