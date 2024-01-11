package io.github.moulberry.moulconfig.managed

import io.github.moulberry.moulconfig.Config
import io.github.moulberry.moulconfig.gui.GuiOptionEditor
import io.github.moulberry.moulconfig.gui.GuiScreenElementWrapper
import io.github.moulberry.moulconfig.gui.MoulConfigEditor
import io.github.moulberry.moulconfig.processor.BuiltinMoulConfigGuis
import io.github.moulberry.moulconfig.processor.ConfigProcessorDriver
import io.github.moulberry.moulconfig.processor.MoulConfigProcessor
import io.github.moulberry.moulconfig.processor.ProcessedOption
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import java.io.File
import java.util.function.BiFunction
import java.util.function.Consumer

class ManagedConfig<T : Config>(private val builder: ManagedConfigBuilder<T>) : ManagedDataFile<T>(builder.apply {
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
    private inline fun <A : Annotation> cast(processor: MoulConfigProcessor<T>, annotation: Class<A>, method: Any) {
        @Suppress("UNCHECKED_CAST")
        processor.registerConfigEditor(annotation, method as BiFunction<ProcessedOption, A, GuiOptionEditor>)
    }

    private fun buildProcessor(builder: ManagedConfigBuilder<T>): MoulConfigProcessor<T> {
        val processor = MoulConfigProcessor(this.instance)
        if (builder.useDefaultProcessors) {
            BuiltinMoulConfigGuis.addProcessors(processor)
        }
        builder.customProcessors.forEach { (annotation, method) ->
            cast(processor, annotation, method)
        }
        ConfigProcessorDriver.processConfig(this.instance.javaClass, this.instance, processor)
        return processor
    }

    fun getEditor(): MoulConfigEditor<T> {
        return MoulConfigEditor(processor)
    }

    fun getGui(): GuiScreen {
        return GuiScreenElementWrapper(getEditor())
    }

    fun openConfigGui() {
        Minecraft.getMinecraft().displayGuiScreen(getGui())
    }
}

