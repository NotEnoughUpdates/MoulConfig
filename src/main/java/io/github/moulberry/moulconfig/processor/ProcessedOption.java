package io.github.moulberry.moulconfig.processor;

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.observer.Property;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ProcessedOption {
    public final String name;
    public final String desc;
    public final int subcategoryId;
    public final Field field;
    public final ProcessedCategory category;
    private final Object container;
    public GuiOptionEditor editor;
    public int accordionId = -1;
    public boolean isProperty;
    public Config config;

    public ProcessedOption(String name, String desc, int subcategoryId, Field field, ProcessedCategory category, Object container, Config config) {
        this.name = name;
        this.desc = desc;
        this.subcategoryId = subcategoryId;
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
