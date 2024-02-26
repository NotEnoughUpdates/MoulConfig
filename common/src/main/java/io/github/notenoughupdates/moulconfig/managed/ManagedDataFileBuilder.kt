package io.github.notenoughupdates.moulconfig.managed

import java.io.File
import java.util.function.BiConsumer
import java.util.function.Consumer

open class ManagedDataFileBuilder<T>(
    var file: File,
    val clazz: Class<T>
) {

    fun throwOnFailure() {
        loadFailed = BiConsumer { _, ex -> throw ex }
        saveFailed = BiConsumer { _, ex -> throw ex }
    }

    var mapper: DataMapper<T> = GsonMapper(clazz)

    @JvmOverloads
    fun jsonMapper(function: GsonMapper<T>.() -> Unit = {}) {
        mapper = GsonMapper(clazz).also(function)
    }

    open var loadFailed: BiConsumer<ManagedDataFile<T>, Exception> = BiConsumer { _, _ -> }
    open var saveFailed: BiConsumer<ManagedDataFile<T>, Exception> = BiConsumer { _, _ -> }
    open var beforeLoad: Consumer<ManagedDataFile<T>> = Consumer {}
    open var afterLoad: Consumer<ManagedDataFile<T>> = Consumer {}
    open var beforeSave: Consumer<ManagedDataFile<T>> = Consumer {}
    open var afterSave: Consumer<ManagedDataFile<T>> = Consumer {}
}
