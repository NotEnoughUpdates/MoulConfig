package io.github.notenoughupdates.moulconfig.managed

import com.google.gson.GsonBuilder
import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.LegacyStringChromaColourTypeAdapter
import io.github.notenoughupdates.moulconfig.observer.PropertyTypeAdapterFactory

class GsonMapper<T>(val clazz: Class<T>) : DataMapper<T> {
    val gsonBuilder = GsonBuilder()
        .registerTypeAdapterFactory(PropertyTypeAdapterFactory())
        .registerTypeAdapter(ChromaColour::class.java, LegacyStringChromaColourTypeAdapter(true))
    var doNotRequireExposed = false

    private val gson by lazy {
        if (!doNotRequireExposed) {
            gsonBuilder.excludeFieldsWithoutExposeAnnotation()
        }
        gsonBuilder.create()
    }
    override fun serialize(value: T): String {
        return gson.toJson(value)
    }

    override fun createDefault(): T {
        return clazz.newInstance()
    }

    override fun deserialize(string: String): T {
        return gson.fromJson(string, clazz)
    }
}
