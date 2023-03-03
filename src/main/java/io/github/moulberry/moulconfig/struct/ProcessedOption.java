package io.github.moulberry.moulconfig.struct;

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;

import java.lang.reflect.Field;

public class ProcessedOption {
    public final String name;
    public final String desc;
    public final int subcategoryId;
    public final Field field;
    private final Object container;
    public GuiOptionEditor editor;
    public int accordionId = -1;
    public Config config;

    public ProcessedOption(String name, String desc, int subcategoryId, Field field, Object container, Config config) {
        this.name = name;
        this.desc = desc;
        this.subcategoryId = subcategoryId;
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
