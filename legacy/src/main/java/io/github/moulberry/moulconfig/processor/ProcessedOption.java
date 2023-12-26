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

/**/
package io.github.moulberry.moulconfig.processor;

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.observer.GetSetter;
import io.github.moulberry.moulconfig.observer.Property;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ProcessedOption {
    public final String name;
    public final String desc;
    public final Field field;
    public final String path;
    public final ProcessedCategory category;
    private final Object container;
    public GuiOptionEditor editor;
    public int accordionId = -1;
    public boolean isProperty;
    public Config config;

    public ProcessedOption(String name, String desc, String path, Field field, ProcessedCategory category, Object container, Config config) {
        this.name = name;
        this.path = path;
        this.desc = desc;
        this.category = category;
        this.config = config;
        this.field = field;
        this.container = container;
        this.isProperty = field.getType() == Property.class;
    }

    public Object get() {
        try {
            Object obj = field.get(container);
            if (isProperty) {
                return ((Property) obj).get();
            } else {
                return obj;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public GetSetter<?> intoProperty() {
        return new GetSetter<Object>() {
            @Override
            public Object get() {
                return ProcessedOption.this.get();
            }

            @Override
            public void set(Object newValue) {
                ProcessedOption.this.set(newValue);
            }
        };
    }

    public Type getType() {
        if (isProperty) {
            return ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        }
        return field.getGenericType();
    }

    public boolean set(Object value) {
        try {
            Object toSet;
            if (getType() == int.class && value instanceof Number) {
                toSet = ((Number) value).intValue();
            } else {
                toSet = value;
            }
            if (isProperty) {
                ((Property<Object>) field.get(container)).set(toSet);
            } else {
                field.set(container, toSet);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void explicitNotifyChange() {
        if (isProperty) {
            try {
                ((Property<?>) field.get(container)).notifyObservers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
