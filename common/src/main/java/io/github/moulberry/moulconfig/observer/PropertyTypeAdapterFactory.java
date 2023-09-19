/*
 * Copyright (C) 2023 NotEnoughUpdates contributors
 *
 * This file is part of MoulConfig.
 *
 * MoulConfig is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * MoulConfig is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MoulConfig. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
