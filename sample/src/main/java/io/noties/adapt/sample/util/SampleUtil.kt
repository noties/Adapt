package io.noties.adapt.sample.util

import android.content.Context
import io.noties.adapt.sample.BuildConfig
import io.noties.adapt.sample.Sample
import io.noties.adapt.sample.SampleView
import io.noties.debug.Debug
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader

object SampleUtil {
    fun readSamples(context: Context): List<Sample> {
        return context.assets.open("samples.json")
            .reader()
            .use(InputStreamReader::readText)
            .let(::JSONArray)
            .let {
                val list = mutableListOf<JSONObject>()
                for (i in 0 until it.length()) {
                    list.add(it.getJSONObject(i))
                }
                list
            }
            .map(::init)
    }

    fun createView(sample: Sample): SampleView {
        val type = Class.forName(sample.javaClassName)
        return type.newInstance() as SampleView
    }

    fun sourceCodeUrl(sample: Sample): String {
        val branch = BuildConfig.GIT_BUILD_BRANCH

        val extension = try {
            val type = Class.forName(sample.javaClassName)
            // kotlin adds a special @Metadata annotation, let's check if type has it
            if (type.isAnnotationPresent(Metadata::class.java)) {
                "kt"
            } else {
                "java"
            }
        } catch (t: Throwable) {
            Debug.e(t)
            "kt"
        }

        val path = sample.javaClassName
            .replace('.', '/')

        return "https://github.com/noties/Adapt/blob/$branch/sample/src/main/java/$path.$extension"
    }

    private fun init(json: JSONObject): Sample = Sample(
        json.getString("id"),
        json.getString("title"),
        json.optString("description").let(HtmlUtil::fromHtml),
        json.optJSONArray("tags")?.let {
            val list = mutableListOf<String>()
            for (i in 0 until it.length()) {
                val tag = it.optString(i)
                if (!tag.isNullOrBlank()) {
                    list.add(tag)
                }
            }
            list
        } ?: emptyList(),
        json.getString("javaClassName")
    )
}