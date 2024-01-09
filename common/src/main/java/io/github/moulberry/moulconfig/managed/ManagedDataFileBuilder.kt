package io.github.moulberry.moulconfig.managed

import java.io.File
import java.util.function.Consumer

class ManagedDataFileBuilder<T> internal constructor(
    var file: File,
    var mapper: DataMapper<T>,
) {

    fun throwOnFailure() {
        loadFailed = Consumer { throw it }
        saveFailed = Consumer { throw it }
    }

    var loadFailed: Consumer<Exception> = Consumer {}
    var saveFailed: Consumer<Exception> = Consumer {}
    var beforeLoad: Runnable = Runnable {}
    var afterLoad: Runnable = Runnable {}
    var beforeSave: Runnable = Runnable {}
    var afterSave: Runnable = Runnable {}

    fun build(): ManagedDataFile<T> {
        return ManagedDataFile(
            file,
            mapper,
            loadFailed, saveFailed, beforeLoad, afterLoad, beforeSave, afterSave
        )
    }
}
