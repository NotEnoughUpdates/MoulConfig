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

package io.github.notenoughupdates.moulconfig.processor;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.Accordion;
import io.github.notenoughupdates.moulconfig.annotations.Category;
import io.github.notenoughupdates.moulconfig.annotations.ConfigAccordionId;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorAccordion;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorInfoText;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import io.github.notenoughupdates.moulconfig.internal.BoundField;
import io.github.notenoughupdates.moulconfig.internal.Warnings;
import lombok.var;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class ConfigProcessorDriver {
    private final List<Class<? extends Annotation>> nonStoredConfigOptions = Arrays.asList(
        ConfigEditorAccordion.class, ConfigEditorInfoText.class,
        ConfigEditorButton.class
    );

    public final ConfigStructureReader reader;

    public boolean warnForPrivateFields = true;

    public int nextAnnotation = 1000000000;

    public boolean checkExpose = true;

    public ConfigProcessorDriver(ConfigStructureReader reader) {
        this.reader = reader;
    }

    private static List<Field> getAllFields(Class<?> type) {
        if (type == null) return new ArrayList<>();
        List<Field> fields = getAllFields(type.getSuperclass());
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        return fields;
    }

    public void processCategory(Object categoryObject,
                                List<BoundField> deferredSubCategories) {
        Class<?> categoryClass = categoryObject.getClass();
        Stack<Integer> accordionStack = new Stack<>();
        Set<Integer> usedAccordionIds = new HashSet<>();
        for (Field field : getAllFields(categoryClass)) {
            if (field.getAnnotation(Category.class) != null) {
                deferredSubCategories.add(new BoundField(field, categoryObject));
            }
            ConfigOption optionAnnotation = field.getAnnotation(ConfigOption.class);
            if (optionAnnotation == null) continue;
            if (checkExpose && field.getAnnotation(Expose.class) == null
                    && (field.getModifiers() & Modifier.TRANSIENT) == 0
                    && nonStoredConfigOptions.stream().noneMatch(field::isAnnotationPresent)) {
                Warnings.warn("Non transient @ConfigOption without @Expose in " + categoryClass + " on field " + field);
            }

            if ((field.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
                field.setAccessible(true);
                if (warnForPrivateFields)
                    Warnings.warn("@ConfigOption on non public field " + field + " in " + categoryClass);
            }

            ConfigAccordionId parentAccordion = field.getAnnotation(ConfigAccordionId.class);
            if (parentAccordion == null) {
                while (!accordionStack.isEmpty()) {
                    reader.endAccordion();
                    accordionStack.pop();
                }
            } else {
                while (!accordionStack.isEmpty() && accordionStack.peek() != parentAccordion.id()) {
                    accordionStack.pop();
                    reader.endAccordion();
                }
                if (accordionStack.isEmpty()) {
                    Warnings.warn("Invalid @ConfigAccordionId in " + categoryClass + " on field " + field);
                }
            }


            Accordion accordionClassAnnotation = field.getAnnotation(Accordion.class);
            if (accordionClassAnnotation != null) {
                if (!usedAccordionIds.isEmpty()) {
                    Warnings.warn("Cannot mix @ConfigEditorAccordion and @ConfigAccordionId with @Accordion in class " + categoryClass);
                }
                reader.beginAccordion(categoryObject, field, optionAnnotation, ++nextAnnotation);
                try {
                    reader.pushPath(field.getName());
                    var subCategory = new ArrayList<BoundField>();
                    processCategory(field.get(categoryObject), subCategory);
                    if (!subCategory.isEmpty()) {
                        Warnings.warn("Cannot define sub categories inside of an accordion: " + subCategory.get(0));
                    }
                    reader.popPath();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                reader.endAccordion();
                continue;
            }

            ConfigEditorAccordion selfAccordion = field.getAnnotation(ConfigEditorAccordion.class);
            if (selfAccordion != null) {
                if (usedAccordionIds.contains(selfAccordion.id())) {
                    Warnings.warn("Reusing of config accordion id " + selfAccordion.id() + " in " + categoryClass + " on field " + field);
                }
                usedAccordionIds.add(selfAccordion.id());
                accordionStack.push(selfAccordion.id());
                reader.beginAccordion(categoryObject, field, optionAnnotation, selfAccordion.id());
            } else {
                reader.emitOption(categoryObject, field, optionAnnotation);
            }
        }
        while (!accordionStack.isEmpty()) {
            reader.endAccordion();
            accordionStack.pop();
        }
    }

    private void processCategoryMeta(
        Object parent,
        Field categoryField,
        Field parentField
    ) {
        Category categoryAnnotation = categoryField.getAnnotation(Category.class);

        if (categoryAnnotation == null) return;
        if (checkExpose && categoryField.getAnnotation(Expose.class) == null) {
            Warnings.warn("@Category without @Expose in " + parent.getClass() + " on field " + categoryField);
        }
        if ((categoryField.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
            categoryField.setAccessible(true);
            if (warnForPrivateFields)
                Warnings.warn("@Category on non public field " + categoryField + " in " + parent.getClass());
        }
        var deferredSubCategories = new ArrayList<BoundField>();
        reader.beginCategory(parent, categoryField, categoryAnnotation.name(), categoryAnnotation.desc());
        if (parentField != null) {
            reader.setCategoryParent(parentField);
        }
        try {
            reader.pushPath(categoryField.getName());
            processCategory(categoryField.get(parent), deferredSubCategories);
            reader.popPath();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        reader.endCategory();
        for (var subCategory : deferredSubCategories) {
            if (parentField == null) {
                processCategoryMeta(subCategory.getBoundTo(), subCategory.getField(), categoryField);
            } else {
                Warnings.warn("Found double recursive sub category at " + subCategory);
            }
        }
    }

    public void processConfig(Config configObject) {
        reader.beginConfig(configObject.getClass(), this, configObject);
        for (Field categoryField : getAllFields(configObject.getClass())) {
            processCategoryMeta(configObject, categoryField, null);
        }
        reader.endConfig();
    }

}
