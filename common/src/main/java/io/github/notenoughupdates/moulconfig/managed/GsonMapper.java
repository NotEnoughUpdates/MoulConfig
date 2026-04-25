package io.github.notenoughupdates.moulconfig.managed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.notenoughupdates.moulconfig.ChromaColour;
import io.github.notenoughupdates.moulconfig.LegacyStringChromaColourTypeAdapter;
import io.github.notenoughupdates.moulconfig.observer.PropertyTypeAdapterFactory;

public class GsonMapper<T> implements DataMapper<T> {
    private final Class<T> clazz;
    private final GsonBuilder gsonBuilder = new GsonBuilder()
        .registerTypeAdapterFactory(new PropertyTypeAdapterFactory())
        .registerTypeAdapter(ChromaColour.class, new LegacyStringChromaColourTypeAdapter(true));
    private boolean doNotRequireExposed;
    private Gson gson;

    public GsonMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public GsonBuilder getGsonBuilder() {
        return gsonBuilder;
    }

    public boolean getDoNotRequireExposed() {
        return doNotRequireExposed;
    }

    public boolean isDoNotRequireExposed() {
        return doNotRequireExposed;
    }

    public void setDoNotRequireExposed(boolean doNotRequireExposed) {
        this.doNotRequireExposed = doNotRequireExposed;
    }

    private Gson getGson() {
        if (gson == null) {
            if (!doNotRequireExposed) {
                gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            }
            gson = gsonBuilder.create();
        }
        return gson;
    }

    @Override
    public String serialize(T value) {
        return getGson().toJson(value);
    }

    @Override
    public T createDefault() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T deserialize(String string) {
        return getGson().fromJson(string, clazz);
    }
}
