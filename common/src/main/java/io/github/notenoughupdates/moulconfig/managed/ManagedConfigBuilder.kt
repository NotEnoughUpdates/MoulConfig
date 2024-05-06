package io.github.notenoughupdates.moulconfig.managed

import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption
import java.io.File
import java.util.function.BiFunction

class ManagedConfigBuilder<T>(file: File, clazz: Class<T>) : ManagedDataFileBuilder<T>(file, clazz) {
    var useDefaultProcessors = true
    var checkExpose = true
    internal val customProcessors =
        mutableListOf<Pair<Class<out Annotation>, BiFunction<ProcessedOption, Annotation, GuiOptionEditor>>>()

    fun clearCustomProcessors() {
        customProcessors.clear()
    }

    inline fun <reified A : Annotation> customProcessor(noinline editorGenerator: (ProcessedOption, A) -> GuiOptionEditor) {
        customProcessor(A::class.java, editorGenerator)
    }

    fun <A : Annotation> customProcessor(
        annotation: Class<A>,
        editorGenerator: BiFunction<ProcessedOption, in A, GuiOptionEditor>
    ) {
        @Suppress("UNCHECKED_CAST")
        customProcessors.add(
            Pair(
                annotation,
                editorGenerator as BiFunction<ProcessedOption, Annotation, GuiOptionEditor>
            )
        )
    }

}
