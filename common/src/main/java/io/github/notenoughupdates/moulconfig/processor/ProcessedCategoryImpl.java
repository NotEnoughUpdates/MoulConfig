package io.github.notenoughupdates.moulconfig.processor;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessedCategoryImpl implements ProcessedCategory {
    public final String name;
    public final String desc;
    public final Field reflectField;
    public final List<ProcessedOption> options = new ArrayList<>();
    public final Map<Integer, ProcessedOption> accordionAnchors = new HashMap<>();

    public @Nullable String parent;

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public String getDebugDeclarationLocation() {
        return reflectField.toString();
    }

    @Override
    public String getIdentifier() {
        return reflectField.toString();
    }

    @Override
    public @Nullable String getParentCategoryId() {
        return parent;
    }

    @Override
    public @Unmodifiable List<ProcessedOption> getOptions() {
        return options;
    }

    @Override
    public @Unmodifiable Map<Integer, ProcessedOption> getAccordionAnchors() {
        return accordionAnchors;
    }

    public ProcessedCategoryImpl(Field field, String name, String desc) {
        this(field, name, desc, null);
    }

    public ProcessedCategoryImpl(Field field, String name, String desc, @Nullable String parent) {
        this.reflectField = field;
        this.name = name;
        this.parent = parent;
        this.desc = desc;
    }
}
