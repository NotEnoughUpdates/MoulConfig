package io.github.moulberry.moulconfig.observer;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class PropertyTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (type.getRawType() != Property.class) return null;
        if (!(type.getType() instanceof ParameterizedType)) return null;

        Type innerType = ((ParameterizedType) type.getType()).getActualTypeArguments()[0];

        TypeAdapter<?> innerAdapter = gson.getAdapter(TypeToken.get(innerType));
        return (TypeAdapter<T>) new PropertyTypeAdapter<>(innerAdapter);
    }

    public static class PropertyTypeAdapter<T> extends TypeAdapter<Property<T>> {
        private final TypeAdapter<T> innerAdapter;

        public PropertyTypeAdapter(TypeAdapter<T> innerAdapter) {
            this.innerAdapter = innerAdapter;
        }

        @Override
        public void write(JsonWriter out, Property<T> value) throws IOException {
            innerAdapter.write(out, value.get());
        }

        @Override
        public Property<T> read(JsonReader in) throws IOException {
            return Property.of(innerAdapter.read(in));
        }
    }
}
