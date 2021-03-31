package io.noties.adapt.sample.processor

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.noties.adapt.sample.annotation.AdaptSample
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@Suppress("MemberVisibilityCanBePrivate")
class AdaptSampleProcessor : AbstractProcessor() {

    companion object {
        const val KEY_SAMPLES_FILE = "io.noties.adapt.samples_file"
    }

    lateinit var messager: Messager
    lateinit var samplesFilePath: String
    lateinit var samples: List<AdaptSampleHolder>

    override fun init(environment: ProcessingEnvironment) {
        super.init(environment)

        messager = environment.messager
        samplesFilePath = environment.options[KEY_SAMPLES_FILE]
            ?: error("Missing required `${KEY_SAMPLES_FILE}` option")

        samples = readSamples(samplesFilePath)
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf(KEY_SAMPLES_FILE)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(AdaptSample::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment): Boolean {
        if (!p1.processingOver()) {

            val samples = annotatedSampleTypes(p1)
                .map(::createSample)
                .sortedByDescending { it.id }

            samples.forEach {
                messager.printMessage(Diagnostic.Kind.NOTE, "$it\n.\n")
            }

            if (this.samples != samples) {
                writeSamples(samples)
            }
        }
        return false
    }

    fun readSamples(path: String): List<AdaptSampleHolder> {
        val file = File(path)
        if (!file.exists()) return emptyList()
        val json = file.readText()
        return Gson().fromJson(json, object : TypeToken<List<AdaptSampleHolder>>() {}.type)
    }

    fun annotatedSampleTypes(environment: RoundEnvironment): Set<TypeElement> {
        val type = AdaptSample::class.java
        val elements: Set<Element> = environment.getElementsAnnotatedWith(type)
        return elements
            .filterIsInstance<TypeElement>()
            .toSet()
    }

    fun createSample(element: TypeElement): AdaptSampleHolder {
        val annotation = element.getAnnotation(AdaptSample::class.java)
            ?: error("AdaptSample annotation is missing")
        val type = element.qualifiedName.toString()
        return AdaptSampleHolder(
            type,
            annotation.id,
            annotation.title,
            annotation.description,
            annotation.tags.toSet().sorted()
        )
    }

    fun writeSamples(samples: List<AdaptSampleHolder>) {
        val file = File(samplesFilePath)
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw IllegalStateException("Cannot create file at path: `$samplesFilePath`")
            }
        }

        val json = GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(samples)

        file.writeText(json)
    }
}