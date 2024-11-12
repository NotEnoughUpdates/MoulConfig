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
package io.github.notenoughupdates.moulconfig.processor;

import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor;
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorAccordion;
import io.github.notenoughupdates.moulconfig.internal.Warnings;
import lombok.Getter;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;

public class MoulConfigProcessor<T extends Config> implements ConfigStructureReader {

    private final T configBaseObject;
    private final LinkedHashMap<String, ProcessedCategoryImpl> categories = new LinkedHashMap<>();
    private ProcessedCategoryImpl currentCategory;
    private Stack<Integer> accordion = new Stack<>();
    private Stack<String> categoryPath = new Stack<>();
    @Getter
    private boolean isFinalized;
    private Map<Field, ProcessedOption> optionLookup = new HashMap<>();
    private Map<Class<? extends Annotation>, BiFunction<ProcessedOption, Annotation, GuiOptionEditor>> editors = new HashMap<>();
    private ConfigProcessorDriver driver;

    public MoulConfigProcessor(T configBaseObject) {
        this.configBaseObject = configBaseObject;
    }

    public static <T extends Config> MoulConfigProcessor<T> withDefaults(T configBaseObject) {
        MoulConfigProcessor<T> moulConfigProcessor = new MoulConfigProcessor<>(configBaseObject);
        BuiltinMoulConfigGuis.addProcessors(moulConfigProcessor);
        return moulConfigProcessor;
    }

    public <A extends Annotation> void registerConfigEditor(Class<A> annotation, BiFunction<ProcessedOption, ? extends A, GuiOptionEditor> editorGenerator) {
        editors.put(annotation, (BiFunction<ProcessedOption, Annotation, GuiOptionEditor>) editorGenerator);
    }

    public void requireFinalized() {
        if (!isFinalized) {
            Warnings.warn("Finalization requirement on MoulConfigProcessor broken. Please first process a config", 4);
        }
    }

    @Override
    public void beginCategory(Object baseObject, Field field, String name, String description) {
        currentCategory = new ProcessedCategoryImpl(field, name, description);
        categories.put(currentCategory.getIdentifier(), currentCategory);
    }

    public T getConfigObject() {
        return configBaseObject;
    }

    @Override
    public void pushPath(String fieldPath) {
        categoryPath.push(fieldPath);
    }

    @Override
    public void beginConfig(Class<? extends Config> configClass, ConfigProcessorDriver driver, Config configObject) {
        this.driver = driver;
        if (editors.isEmpty()) {
            Warnings.warn("Calling a config processor with no editors registered. Consider registering editors using BuiltinMoulConfigGuis.addProcessors or MoulConfigProcessor.withDefaults");
        }
        if (isFinalized) {
            Warnings.warn("Processing a finalized config");
        }
    }

    @Override
    public void endConfig() {
        isFinalized = true;
    }

    @Override
    public void popPath() {
        categoryPath.pop();
    }

    @Override
    public void endCategory() {
        for (ProcessedOption option : currentCategory.options) {
            val editor = option.getEditor();
            if (editor instanceof GuiOptionEditorAccordion) {
                currentCategory.accordionAnchors.put(((GuiOptionEditorAccordion) editor).getAccordionId(), option);
            }
        }
        accordion.clear();
    }

    @Override
    public void beginAccordion(Object baseObject, Field field, ConfigOption option, int id) {
        var processedOption = createProcessedOption(baseObject, field, option);
        // TODO: expose setter in subclass
        processedOption.editor = new GuiOptionEditorAccordion(processedOption, id);
        currentCategory.options.add(processedOption);
        this.accordion.push(id);
    }

    @Override
    public void endAccordion() {
        accordion.pop();
    }

    @Override
    public void emitOption(Object baseObject, Field field, ConfigOption option) {
        var processedOption = createProcessedOption(baseObject, field, option);
        GuiOptionEditor optionGui = createOptionGui(processedOption, field, option);
        if (optionGui == null) {
            Warnings.warn("Could not create GUI Option Editor for " + field + " in " + baseObject.getClass());
            return;
        }
        processedOption.editor = optionGui;
        currentCategory.options.add(processedOption);
    }

    protected ProcessedOptionImpl createProcessedOption(Object baseObject, Field field, ConfigOption option) {
        ProcessedOptionImpl processedOption = new ProcessedOptionImpl(
            option.name(), option.desc(), String.join(".", categoryPath) + "." + field.getName(),
            field,
            currentCategory, baseObject,
            configBaseObject
        );
        optionLookup.put(field, processedOption);
        if (!accordion.isEmpty()) {
            processedOption.accordionId = accordion.peek();
        }
        return processedOption;
    }

    @Override
    public void setCategoryParent(Field field) {
        currentCategory.parent = field.toString();
    }

    protected GuiOptionEditor createOptionGui(ProcessedOption processedOption, Field field, ConfigOption option) {
        for (Map.Entry<Class<? extends Annotation>, BiFunction<ProcessedOption, Annotation, GuiOptionEditor>> entry :
            editors.entrySet()) {
            Annotation annotation = field.getAnnotation(entry.getKey());
            if (annotation == null) continue;
            GuiOptionEditor editor = entry.getValue().apply(processedOption, annotation);
            if (editor == null) {
                continue;
            }
            return editor;
        }
        return null;
    }

    public LinkedHashMap<String, ? extends ProcessedCategory> getAllCategories() {
        requireFinalized();
        return this.categories;
    }

    public @Nullable ProcessedOption getOptionFromField(@NotNull Field field) {
        requireFinalized();
        return optionLookup.get(field);
    }
}
