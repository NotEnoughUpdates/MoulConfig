package io.github.moulberry.moulconfig.managed

import java.io.File
import java.lang.management.ManagementFactory
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.function.BiConsumer
import java.util.function.Consumer

open class ManagedDataFile<T> internal constructor(
    val file: File,
    val mapper: DataMapper<T>,
    private val loadFailed: BiConsumer<ManagedDataFile<T>, Exception>,
    private val saveFailed: BiConsumer<ManagedDataFile<T>, Exception>,
    private val beforeLoad: Consumer<ManagedDataFile<T>>,
    private val afterLoad: Consumer<ManagedDataFile<T>>,
    private val beforeSave: Consumer<ManagedDataFile<T>>,
    private val afterSave: Consumer<ManagedDataFile<T>>,
) {
    constructor(builder: ManagedDataFileBuilder<T>) : this(
        builder.file,
        builder.mapper,
        builder.loadFailed,
        builder.saveFailed,
        builder.beforeLoad,
        builder.afterLoad,
        builder.beforeSave,
        builder.afterSave
    )

    companion object {
        @JvmStatic
        @JvmOverloads
        fun <T> create(
            file: File,
            clazz: Class<T>,
            consumer: (ManagedDataFileBuilder<T>.() -> Unit) = {}
        ): ManagedDataFile<T> {
            return ManagedDataFile(ManagedDataFileBuilder(file, clazz).apply(consumer))
        }
    }

    var instance: T = mapper.createDefault()

    init {
        file.parentFile.mkdirs()
        reloadFromFile()
    }

    fun reloadFromFile() {
        beforeLoad.accept(this)
        try {
            if (file.exists()) {
                instance = mapper.deserialize(file.readText())
            } else {
                instance = mapper.createDefault()
            }
        } catch (ex: Exception) {
            loadFailed.accept(this, ex)
        }
        afterLoad.accept(this)
    }

    private fun createUniqueExtraFile(identifier: String, directory: File = file.parentFile): File {
        val jvmHash = ManagementFactory.getRuntimeMXBean().name.hashCode()
        val timestamp = System.currentTimeMillis().toString()
        directory.mkdirs()
        return directory.resolve("${file.nameWithoutExtension}-${jvmHash}-${timestamp}-$identifier.${file.extension}")
    }

    fun saveToFile() {
        beforeSave.accept(this)
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
            saveFailed.accept(this, ex)
        }
        afterSave.accept(this)
    }
}