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

package io.github.moulberry.moulconfig.processor;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ConfigProcessorDriver {
    private static final List<Class<? extends Annotation>> nonStoredConfigOptions = Arrays.asList(
        ConfigEditorAccordion.class, ConfigEditorInfoText.class,
        ConfigEditorButton.class
    );

    static int nextAnnotation = 1000000000;

    private static List<Field> getAllFields(Class<?> type) {
        if (type == null) return new ArrayList<>();
        List<Field> fields = getAllFields(type.getSuperclass());
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        return fields;
    }

    public static void processCategory(Object categoryObject, Class<?> categoryClass, ConfigStructureReader reader) {
        Stack<Integer> accordionStack = new Stack<>();
        Set<Integer> usedAccordionIds = new HashSet<>();
        for (Field field : getAllFields(categoryClass)) {
            ConfigOption optionAnnotation = field.getAnnotation(ConfigOption.class);
            if (optionAnnotation == null) continue;
            if (field.getAnnotation(Expose.class) == null
                && (field.getModifiers() & Modifier.TRANSIENT) == 0
                && nonStoredConfigOptions.stream().noneMatch(field::isAnnotationPresent)) {
                new Error("Warning: non transient @ConfigOption without @Expose in " + categoryClass + " on field " + field).printStackTrace();
            }

            ConfigOverlay annotation = field.getAnnotation(ConfigOverlay.class);
            if (annotation != null) {
                reader.emitGuiOverlay(categoryObject, field, optionAnnotation);
                if (!annotation.displayInline()) continue;
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
                    new Error("Warning: invalid @ConfigAccordionId in " + categoryClass + " on field " + field).printStackTrace();
                }
            }


            Accordion accordionClassAnnotation = field.getAnnotation(Accordion.class);
            if (accordionClassAnnotation != null) {
                if (!usedAccordionIds.isEmpty()) {
                    new Error("Warning: Cannot mix @ConfigEditorAccordion and @ConfigAccordionId with @Accordion in class " + categoryClass).printStackTrace();
                }
                reader.beginAccordion(categoryObject, field, optionAnnotation, ++nextAnnotation);
                try {
                    processCategory(field.get(categoryObject), field.getType(), reader);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                reader.endAccordion();
                continue;
            }

            ConfigEditorAccordion selfAccordion = field.getAnnotation(ConfigEditorAccordion.class);
            if (selfAccordion != null) {
                if (usedAccordionIds.contains(selfAccordion.id())) {
                    new Error("Warning: reusing of config accordion id " + selfAccordion.id() + " in " + categoryClass + " on field " + field).printStackTrace();
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

    public static void processConfig(Class<? extends Config> configClass, Config configObject, ConfigStructureReader reader) {
        reader.beginConfig(configClass, configObject);
        for (Field categoryField : getAllFields(configClass)) {
            Category categoryAnnotation = categoryField.getAnnotation(Category.class);

            if (categoryAnnotation == null) continue;
            if (categoryField.getAnnotation(Expose.class) == null) {
                new Error("Warning: @Category without @Expose in " + configClass + " on field " + categoryField).printStackTrace();
            }
            if ((categoryField.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
                new Error("Warning: @Category on non public field " + categoryField + " in " + configClass).printStackTrace();
                continue;
            }
            reader.beginCategory(configObject, categoryField, categoryAnnotation.name(), categoryAnnotation.desc());
            try {
                processCategory(categoryField.get(configObject), categoryField.getType(), reader);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            reader.endCategory();
        }
        reader.endConfig();
    }

}
