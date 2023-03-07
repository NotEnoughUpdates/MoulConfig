package io.github.moulberry.moulconfig.struct;

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;

import java.lang.reflect.Field;
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
    public Config config;

    public ProcessedOption(String name, String desc, int subcategoryId, Field field, ProcessedCategory category, Object container, Config config) {
        this.name = name;
        this.desc = desc;
        this.subcategoryId = subcategoryId;
        this.category = category;
        this.config = config;
        this.field = field;
        this.container = container;
    }

    public Object get() {
        try {
            return field.get(container);
        } catch (Exception e) {
            return null;
        }
    }

    public Type getType() {
        return field.getGenericType();
    }

    public boolean set(Object value) {
        try {
            if (field.getType() == int.class && value instanceof Number) {
                field.set(container, ((Number) value).intValue());
            } else {
                field.set(container, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
