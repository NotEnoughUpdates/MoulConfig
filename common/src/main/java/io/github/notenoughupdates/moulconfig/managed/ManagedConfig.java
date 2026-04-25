package io.github.notenoughupdates.moulconfig.managed;

import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor;
import io.github.notenoughupdates.moulconfig.processor.BuiltinMoulConfigGuis;
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;

import java.io.File;
import java.util.function.Consumer;

public class ManagedConfig<T extends Config> extends ManagedDataFile<T> {
    private final ManagedConfigBuilder<T> builder;
    private MoulConfigProcessor<T> processor;

    @SuppressWarnings("unchecked")
    public ManagedConfig(ManagedConfigBuilder<T> builder) {
        super(prepare(builder));
        this.builder = builder;
    }

    private static <T extends Config> ManagedConfigBuilder<T> prepare(ManagedConfigBuilder<T> builder) {
        builder.setAfterLoad(((Consumer<ManagedDataFile<T>>) file -> ((ManagedConfig<T>) file).rebuildConfigProcessor(builder)).andThen(builder.getAfterLoad()));
        return builder;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ManagedConfig create(File file, Class clazz) {
        ManagedConfigBuilder builder = new ManagedConfigBuilder(file, clazz);
        return new ManagedConfig(builder);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ManagedConfig create(File file, Class clazz, Consumer consumer) {
        ManagedConfigBuilder builder = new ManagedConfigBuilder(file, clazz);
        consumer.accept(builder);
        return new ManagedConfig(builder);
    }

    public MoulConfigProcessor<T> getProcessor() {
        return processor;
    }

    @Override
    public void injectIntoInstance() {
        if (getInstance().saveRunnables != null) {
            getInstance().saveRunnables.add(this::saveToFile);
        }
    }

    public void rebuildConfigProcessor() {
        rebuildConfigProcessor(builder);
    }

    private void rebuildConfigProcessor(ManagedConfigBuilder<T> builder) {
        processor = buildProcessor(builder);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private MoulConfigProcessor<T> buildProcessor(ManagedConfigBuilder<T> builder) {
        MoulConfigProcessor<T> processor = new MoulConfigProcessor<>(getInstance());
        if (builder.getUseDefaultProcessors()) {
            BuiltinMoulConfigGuis.addProcessors(processor);
        }
        for (ManagedConfigBuilder.CustomProcessor customProcessor : builder.getCustomProcessors()) {
            processor.registerConfigEditor((Class) customProcessor.annotation, customProcessor.method);
        }
        ConfigProcessorDriver driver = new ConfigProcessorDriver(processor);
        driver.checkExpose = builder.getCheckExpose();
        driver.processConfig(getInstance());
        return processor;
    }

    public MoulConfigEditor<T> getEditor() {
        return new MoulConfigEditor<>(processor);
    }

    public void openConfigGui() {
        IMinecraft.INSTANCE.openWrappedScreen(getEditor());
    }
}
