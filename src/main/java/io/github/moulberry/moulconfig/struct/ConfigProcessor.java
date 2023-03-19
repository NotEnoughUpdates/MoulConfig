/*
 * Copyright (C) 2022 NotEnoughUpdates contributors
 *
 * This file is part of NotEnoughUpdates.
 *
 * NotEnoughUpdates is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * NotEnoughUpdates is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NotEnoughUpdates. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.moulberry.moulconfig.struct;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ConfigProcessor {
    private static final List<Class<? extends Annotation>> nonStoredConfigOptions = Arrays.asList(
        ConfigEditorAccordion.class, ConfigEditorInfoText.class,
        ConfigEditorButton.class
    );

    static int nextAnnotation = 0;

    private static void processCategory(Class<?> categoryClass, ConfigStructureReader reader) {
        Stack<Integer> accordionStack = new Stack<>();
        Set<Integer> usedAccordionIds = new HashSet<>();
        for (Field field : categoryClass.getDeclaredFields()) {
            ConfigOption optionAnnotation = field.getAnnotation(ConfigOption.class);
            if (optionAnnotation == null) continue;
            if (field.getAnnotation(Expose.class) == null
                && (field.getModifiers() & Modifier.TRANSIENT) == 0
                && nonStoredConfigOptions.stream().noneMatch(field::isAnnotationPresent)) {
                new Error("Warning: non transient @ConfigOption without @Expose in " + categoryClass + " on field " + field).printStackTrace();
            }

            Accordion accordionClassAnnotation = field.getAnnotation(Accordion.class);
            if (accordionClassAnnotation != null) {
                if (!usedAccordionIds.isEmpty()) {
                    new Error("Warning: Cannot mix @CnofigEditorAccordion and @ConfigAccordionId with @Accordion in class " + categoryClass).printStackTrace();
                }
                reader.beginAccordion(field, optionAnnotation, ++nextAnnotation);
                processCategory(field.getType(), reader);
                reader.endAccordion();
                continue;
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
            ConfigEditorAccordion selfAccordion = field.getAnnotation(ConfigEditorAccordion.class);
            if (selfAccordion != null) {
                if (usedAccordionIds.contains(selfAccordion.id())) {
                    new Error("Warning: reusing of config accordion id " + selfAccordion.id() + " in " + categoryClass + " on field " + field).printStackTrace();
                }
                usedAccordionIds.add(selfAccordion.id());
                accordionStack.push(selfAccordion.id());
                reader.beginAccordion(field, optionAnnotation, selfAccordion.id());
            } else {
                reader.emitOption(field, optionAnnotation);
            }
        }

    }

    public static void processConfig(Class<? extends Config> configClass, ConfigStructureReader reader) {
        reader.beginConfig(configClass);
        for (Field categoryField : configClass.getDeclaredFields()) {
            Category categoryAnnotation = categoryField.getAnnotation(Category.class);

            if (categoryAnnotation == null) continue;
            if (categoryField.getAnnotation(Expose.class) == null) {
                new Error("Warning: @Category without @Expose in " + configClass + " on field " + categoryField).printStackTrace();
            }
            if ((categoryField.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
                new Error("Warning: @Category on non public field " + categoryField + " in " + configClass).printStackTrace();
                continue;
            }
            reader.beginCategory(categoryField, categoryAnnotation);
            processCategory(categoryField.getType(), reader);
            reader.endCategory();
        }
        reader.endConfig();
    }

}
