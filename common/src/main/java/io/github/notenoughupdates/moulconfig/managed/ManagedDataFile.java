package io.github.notenoughupdates.moulconfig.managed;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ManagedDataFile<T> {
    private final File file;
    private final DataMapper<T> mapper;
    private final BiConsumer<ManagedDataFile<T>, Exception> loadFailed;
    private final BiConsumer<ManagedDataFile<T>, Exception> saveFailed;
    private final Consumer<ManagedDataFile<T>> beforeLoad;
    private final Consumer<ManagedDataFile<T>> afterLoad;
    private final Consumer<ManagedDataFile<T>> beforeSave;
    private final Consumer<ManagedDataFile<T>> afterSave;
    private T instance;

    ManagedDataFile(
        File file,
        DataMapper<T> mapper,
        BiConsumer<ManagedDataFile<T>, Exception> loadFailed,
        BiConsumer<ManagedDataFile<T>, Exception> saveFailed,
        Consumer<ManagedDataFile<T>> beforeLoad,
        Consumer<ManagedDataFile<T>> afterLoad,
        Consumer<ManagedDataFile<T>> beforeSave,
        Consumer<ManagedDataFile<T>> afterSave
    ) {
        this.file = file;
        this.mapper = mapper;
        this.loadFailed = loadFailed;
        this.saveFailed = saveFailed;
        this.beforeLoad = beforeLoad;
        this.afterLoad = afterLoad;
        this.beforeSave = beforeSave;
        this.afterSave = afterSave;
        this.instance = mapper.createDefault();
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        reloadFromFile();
    }

    public ManagedDataFile(ManagedDataFileBuilder<T> builder) {
        this(
            builder.getFile(),
            builder.getMapper(),
            builder.getLoadFailed(),
            builder.getSaveFailed(),
            builder.getBeforeLoad(),
            builder.getAfterLoad(),
            builder.getBeforeSave(),
            builder.getAfterSave()
        );
    }

    public File getFile() { return file; }
    public DataMapper<T> getMapper() { return mapper; }
    public T getInstance() { return instance; }
    public void setInstance(T instance) { this.instance = instance; }

    public void reloadFromFile() {
        beforeLoad.accept(this);
        try {
            if (file.exists()) {
                instance = mapper.deserialize(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
            } else {
                instance = mapper.createDefault();
            }
        } catch (Exception ex) {
            loadFailed.accept(this, ex);
        }
        injectIntoInstance();
        afterLoad.accept(this);
    }

    public void injectIntoInstance() {
    }

    private File createUniqueExtraFile(String identifier) {
        return createUniqueExtraFile(identifier, file.getParentFile());
    }

    private File createUniqueExtraFile(String identifier, File directory) {
        int jvmHash = ManagementFactory.getRuntimeMXBean().getName().hashCode();
        String timestamp = Long.toString(System.currentTimeMillis());
        if (directory != null) {
            directory.mkdirs();
        }
        String fileName = file.getName();
        int dot = fileName.lastIndexOf('.');
        String nameWithoutExtension = dot >= 0 ? fileName.substring(0, dot) : fileName;
        String extension = dot >= 0 ? fileName.substring(dot + 1) : "";
        return new File(directory, nameWithoutExtension + "-" + jvmHash + "-" + timestamp + "-" + identifier + "." + extension);
    }

    public void saveToFile() {
        beforeSave.accept(this);
        String toSave = mapper.serialize(instance);
        File temporarySaveFile = createUniqueExtraFile("save");
        try {
            Files.write(temporarySaveFile.toPath(), toSave.getBytes(StandardCharsets.UTF_8));
            mapper.deserialize(new String(Files.readAllBytes(temporarySaveFile.toPath()), StandardCharsets.UTF_8));
            Files.move(temporarySaveFile.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            saveFailed.accept(this, ex);
        }
        afterSave.accept(this);
    }
}
