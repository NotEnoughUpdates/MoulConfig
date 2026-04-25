package io.github.notenoughupdates.moulconfig.managed;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ManagedDataFileBuilder<T> {
    private File file;
    private final Class<T> clazz;
    private DataMapper<T> mapper;
    private BiConsumer<ManagedDataFile<T>, Exception> loadFailed = (file, ex) -> {};
    private BiConsumer<ManagedDataFile<T>, Exception> saveFailed = (file, ex) -> {};
    private Consumer<ManagedDataFile<T>> beforeLoad = file -> {};
    private Consumer<ManagedDataFile<T>> afterLoad = file -> {};
    private Consumer<ManagedDataFile<T>> beforeSave = file -> {};
    private Consumer<ManagedDataFile<T>> afterSave = file -> {};

    public ManagedDataFileBuilder(File file, Class<T> clazz) {
        this.file = file;
        this.clazz = clazz;
        this.mapper = new GsonMapper<>(clazz);
    }

    public void throwOnFailure() {
        loadFailed = (file, ex) -> {
            throw new RuntimeException(ex);
        };
        saveFailed = (file, ex) -> {
            throw new RuntimeException(ex);
        };
    }

    public void jsonMapper() {
        jsonMapper(mapper -> {});
    }

    public void jsonMapper(Consumer<GsonMapper<T>> function) {
        GsonMapper<T> gsonMapper = new GsonMapper<>(clazz);
        function.accept(gsonMapper);
        mapper = gsonMapper;
    }

    public File getFile() { return file; }
    public void setFile(File file) { this.file = file; }
    public Class<T> getClazz() { return clazz; }
    public DataMapper<T> getMapper() { return mapper; }
    public void setMapper(DataMapper<T> mapper) { this.mapper = mapper; }
    public BiConsumer<ManagedDataFile<T>, Exception> getLoadFailed() { return loadFailed; }
    public void setLoadFailed(BiConsumer<ManagedDataFile<T>, Exception> loadFailed) { this.loadFailed = loadFailed; }
    public BiConsumer<ManagedDataFile<T>, Exception> getSaveFailed() { return saveFailed; }
    public void setSaveFailed(BiConsumer<ManagedDataFile<T>, Exception> saveFailed) { this.saveFailed = saveFailed; }
    public Consumer<ManagedDataFile<T>> getBeforeLoad() { return beforeLoad; }
    public void setBeforeLoad(Consumer<ManagedDataFile<T>> beforeLoad) { this.beforeLoad = beforeLoad; }
    public Consumer<ManagedDataFile<T>> getAfterLoad() { return afterLoad; }
    public void setAfterLoad(Consumer<ManagedDataFile<T>> afterLoad) { this.afterLoad = afterLoad; }
    public Consumer<ManagedDataFile<T>> getBeforeSave() { return beforeSave; }
    public void setBeforeSave(Consumer<ManagedDataFile<T>> beforeSave) { this.beforeSave = beforeSave; }
    public Consumer<ManagedDataFile<T>> getAfterSave() { return afterSave; }
    public void setAfterSave(Consumer<ManagedDataFile<T>> afterSave) { this.afterSave = afterSave; }
}
