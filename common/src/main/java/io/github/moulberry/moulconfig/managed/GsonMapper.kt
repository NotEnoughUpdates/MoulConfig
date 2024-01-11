package io.github.moulberry.moulconfig.managed

import com.google.gson.GsonBuilder
import io.github.moulberry.moulconfig.observer.PropertyTypeAdapterFactory

class GsonMapper<T>(val clazz: Class<T>) : DataMapper<T> {
    val gsonBuilder = GsonBuilder()
        .registerTypeAdapterFactory(PropertyTypeAdapterFactory())
    private val gson by lazy { gsonBuilder.create() }
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
