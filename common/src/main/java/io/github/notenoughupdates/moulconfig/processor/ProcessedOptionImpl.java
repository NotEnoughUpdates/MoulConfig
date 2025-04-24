package io.github.notenoughupdates.moulconfig.processor;

import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.SearchTag;
import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor;
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorAccordion;
import io.github.notenoughupdates.moulconfig.observer.Property;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ProcessedOptionImpl implements ProcessedOption, ProcessedOption.HasField {
    final String name;
    final String desc;
    final Field field;
    final String path;
    final ProcessedCategory category;
    final Object container;
    GuiOptionEditor editor;
    int accordionId = -1;
    boolean isProperty;
    Config config;

    public ProcessedOptionImpl(String name, String desc, String path, Field field, ProcessedCategory category, Object container, Config config) {
        this.name = name;
        this.path = path;
        this.desc = desc;
        this.category = category;
        this.config = config;
        this.field = field;
        this.container = container;
        this.isProperty = field.getType() == Property.class;
    }

    @Override
    public SearchTag[] getSearchTags() {
        return field.getAnnotationsByType(SearchTag.class);
    }

    private GuiOptionEditorAccordion owningAccordion;

    @Override
    public String getDebugDeclarationLocation() {
        return field.toString();
    }

    public GuiOptionEditorAccordion getOwningAccordion() {
        if (owningAccordion == null && getAccordionId() >= 0) {
            owningAccordion = getCategory()
                .getOptions()
                .stream()
                .map(ProcessedOption::getEditor)
                .filter(it -> it instanceof GuiOptionEditorAccordion)
                .map(it -> (GuiOptionEditorAccordion) it)
                .filter(it -> it.getAccordionId() == getAccordionId())
                .findAny()
                .orElse(null);
        }
        return owningAccordion;
    }

    @Override
    public int getAccordionId() {
        return accordionId;
    }

    @Override
    public GuiOptionEditor getEditor() {
        return editor;
    }

    @Override
    public ProcessedCategory getCategory() {
        return category;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override

    public String getDescription() {
        return desc;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
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

    @Override
    public Type getType() {
        if (isProperty) {
            return ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        }
        return field.getGenericType();
    }

    @Override
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

    @Override
    public void explicitNotifyChange() {
        if (isProperty) {
            try {
                ((Property<?>) field.get(container)).notifyObservers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Field getField() {
        return field;
    }
}
