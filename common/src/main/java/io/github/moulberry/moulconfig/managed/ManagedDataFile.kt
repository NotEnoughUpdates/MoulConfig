package io.github.moulberry.moulconfig.managed

import java.io.File
import java.lang.management.ManagementFactory
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.function.Consumer

class ManagedDataFile<T> internal constructor(
    val file: File,
    val mapper: DataMapper<T>,
    private val loadFailed: Consumer<Exception>,
    private val saveFailed: Consumer<Exception>,
    private val beforeLoad: Runnable,
    private val afterLoad: Runnable,
    private val beforeSave: Runnable,
    private val afterSave: Runnable,
) {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun <T> create(
            file: File,
            mapper: DataMapper<T>,
            consumer: (ManagedDataFileBuilder<T>.() -> Unit) = {}
        ): ManagedDataFile<T> {
            return ManagedDataFileBuilder(file, mapper).apply(consumer).build()
        }
    }

    var instance: T = mapper.createDefault()

    init {
        file.parentFile.mkdirs()
        reloadFromFile()
    }

    fun reloadFromFile() {
        beforeLoad.run()
        try {
            instance = mapper.deserialize(file.readText())
        } catch (ex: Exception) {
            loadFailed.accept(ex)
        }
        afterLoad.run()
    }

    private fun createUniqueExtraFile(identifier: String, directory: File = file.parentFile): File {
        val jvmHash = ManagementFactory.getRuntimeMXBean().name.hashCode()
        val timestamp = System.currentTimeMillis().toString()
        directory.mkdirs()
        return directory.resolve("${file.nameWithoutExtension}-${jvmHash}-${timestamp}-$identifier.${file.extension}")
    }

    fun saveToFile() {
        beforeSave.run()
        val toSave = mapper.serialize(instance)
        val temporarySaveFile = createUniqueExtraFile("save")
        try {
            temporarySaveFile.writeText(toSave)
            mapper.deserialize(temporarySaveFile.readText())
            Files.move(
                temporarySaveFile.toPath(),
                file.toPath(),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
            )
        } catch (ex: Exception) {
            saveFailed.accept(ex)
        }
        afterSave.run()
    }
}