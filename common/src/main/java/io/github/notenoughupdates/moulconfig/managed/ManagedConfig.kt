package io.github.notenoughupdates.moulconfig.managed

import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor
import io.github.notenoughupdates.moulconfig.processor.BuiltinMoulConfigGuis
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption
import java.io.File
import java.util.function.BiFunction
import java.util.function.Consumer

class ManagedConfig<T : Config>(private val builder: ManagedConfigBuilder<T>) :
    ManagedDataFile<T>(builder.apply {
        afterLoad = Consumer<ManagedDataFile<T>> { (it as ManagedConfig<T>).rebuildConfigProcessor(builder) }
            .andThen(afterLoad)
    }) {

    companion object {
        @JvmStatic
        @JvmOverloads
        fun <T : Config> create(
            file: File,
            clazz: Class<T>,
            consumer: (ManagedConfigBuilder<T>.() -> Unit) = {}
        ): ManagedConfig<T> {
            return ManagedConfig(ManagedConfigBuilder(file, clazz).apply(consumer))
        }
    }
    // TODO: enforce the save callback, somehow

    lateinit var processor: MoulConfigProcessor<T>
        private set


    fun rebuildConfigProcessor() {
        rebuildConfigProcessor(builder)
    }

    private fun rebuildConfigProcessor(builder: ManagedConfigBuilder<T>) {
        processor = buildProcessor(builder)
    }

    /**
     * Helper function to introduce the A type parameter so that two objects can be cast to be that same A variable.
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun <A : Annotation> cast(
        processor: MoulConfigProcessor<T>,
        annotation: Class<A>,
        method: Any
    ) {
        @Suppress("UNCHECKED_CAST")
        processor.registerConfigEditor(
            annotation,
            method as BiFunction<ProcessedOption, A, GuiOptionEditor>
        )
    }

    private fun buildProcessor(builder: ManagedConfigBuilder<T>): MoulConfigProcessor<T> {
        val processor =
            MoulConfigProcessor(this.instance)
        if (builder.useDefaultProcessors) {
            BuiltinMoulConfigGuis.addProcessors(
                processor
            )
        }
        builder.customProcessors.forEach { (annotation, method) ->
            cast(processor, annotation, method)
        }
        val driver = ConfigProcessorDriver(processor)
        driver.checkExpose = builder.checkExpose
        driver.processConfig(this.instance)
        return processor
    }

    fun getEditor(): MoulConfigEditor<T> {
        return MoulConfigEditor(processor)
    }

    fun openConfigGui() {
        IMinecraft.instance.openWrappedScreen(getEditor())
    }
}

