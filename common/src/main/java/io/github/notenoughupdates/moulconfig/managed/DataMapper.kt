package io.github.notenoughupdates.moulconfig.managed

interface DataMapper<T> {
    fun serialize(value: T): String
    fun createDefault(): T
    fun deserialize(string: String): T
}
