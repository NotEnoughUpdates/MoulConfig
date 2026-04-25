package io.github.notenoughupdates.moulconfig.managed;

import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ManagedConfigBuilder<T> extends ManagedDataFileBuilder<T> {
    static final class CustomProcessor {
        final Class<? extends Annotation> annotation;
        final BiFunction<ProcessedOption, Annotation, GuiOptionEditor> method;

        CustomProcessor(Class<? extends Annotation> annotation, BiFunction<ProcessedOption, Annotation, GuiOptionEditor> method) {
            this.annotation = annotation;
            this.method = method;
        }
    }

    private boolean useDefaultProcessors = true;
    private boolean checkExpose = true;
    private final List<CustomProcessor> customProcessors = new ArrayList<>();

    public ManagedConfigBuilder(File file, Class<T> clazz) {
        super(file, clazz);
    }

    public boolean getUseDefaultProcessors() { return useDefaultProcessors; }
    public boolean isUseDefaultProcessors() { return useDefaultProcessors; }
    public void setUseDefaultProcessors(boolean useDefaultProcessors) { this.useDefaultProcessors = useDefaultProcessors; }
    public boolean getCheckExpose() { return checkExpose; }
    public boolean isCheckExpose() { return checkExpose; }
    public void setCheckExpose(boolean checkExpose) { this.checkExpose = checkExpose; }
    List<CustomProcessor> getCustomProcessors() { return customProcessors; }

    public void clearCustomProcessors() {
        customProcessors.clear();
    }

    @SuppressWarnings("unchecked")
    public <A extends Annotation> void customProcessor(
        Class<A> annotation,
        BiFunction<ProcessedOption, ? super A, GuiOptionEditor> editorGenerator
    ) {
        customProcessors.add(new CustomProcessor(
            annotation,
            (BiFunction<ProcessedOption, Annotation, GuiOptionEditor>) editorGenerator
        ));
    }
}
