package io.github.moulberry.moulconfig.managed

interface DataMapper<T> {
    fun serialize(value: T): String
    fun createDefault(): T
    fun deserialize(string: String): T
}
