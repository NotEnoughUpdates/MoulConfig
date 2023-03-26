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
import io.github.moulberry.moulconfig.Overlay;
import io.github.moulberry.moulconfig.annotations.ConfigOption;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.gui.editors.GuiOptionEditorAccordion;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;

public class MoulConfigProcessor<T extends Config> implements ConfigStructureReader {

    private final T configBaseObject;
    private final LinkedHashMap<String, ProcessedCategory> categories = new LinkedHashMap<>();
    private final List<Overlay> overlays = new ArrayList<>();
    private ProcessedCategory currentCategory;
    private Map<Overlay, List<ProcessedOption>> processedOverlays = new IdentityHashMap<>();
    private Stack<Integer> accordion = new Stack<>();
    private Map<Class<? extends Annotation>, BiFunction<ProcessedOption, Annotation, GuiOptionEditor>> editors = new HashMap<>();

    public MoulConfigProcessor(T configBaseObject) {
        this.configBaseObject = configBaseObject;
    }

    public <A extends Annotation> void registerConfigEditor(Class<A> annotation, BiFunction<ProcessedOption, ? extends A, GuiOptionEditor> editorGenerator) {
        editors.put(annotation, (BiFunction<ProcessedOption, Annotation, GuiOptionEditor>) editorGenerator);
    }

    public List<Overlay> getAllOverlays() {
        return overlays;
    }

    @Override
    public void beginCategory(Object baseObject, Field field, String name, String description) {
        currentCategory = new ProcessedCategory(field.getName(), name, description);
        categories.put(field.getName(), currentCategory);
    }

    public T getConfigObject() {
        return configBaseObject;
    }


    @Override
    public void endCategory() {
        accordion.clear();
    }

    @Override
    public void beginAccordion(Object baseObject, Field field, ConfigOption option, int id) {
        ProcessedOption processedOption = createProcessedOption(baseObject, field, option);
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
        ProcessedOption processedOption = createProcessedOption(baseObject, field, option);
        GuiOptionEditor optionGui = createOptionGui(processedOption, field, option);
        if (optionGui == null) {
            new Error("Warning: Could not create GUI Option Editor for " + field + " in " + baseObject.getClass()).printStackTrace();
            return;
        }
        processedOption.editor = optionGui;
        currentCategory.options.add(processedOption);
    }

    protected ProcessedOption createProcessedOption(Object baseObject, Field field, ConfigOption option) {
        ProcessedOption processedOption = new ProcessedOption(
            option.name(), option.desc(),
            option.subcategoryId(),
            field,
            currentCategory, baseObject,
            configBaseObject
        );
        if (!accordion.isEmpty()) {
            processedOption.accordionId = accordion.peek();
        }
        return processedOption;
    }

    @Override
    public void emitGuiOverlay(Object baseObject, Field field, ConfigOption option) {
        Overlay overlay;
        try {
            overlay = (Overlay) field.get(baseObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        overlays.add(overlay);
        if (processedOverlays.containsKey(overlay)) return;
        MoulConfigProcessor<T> subProcessor = new MoulConfigProcessor<>(configBaseObject);
        subProcessor.processedOverlays = processedOverlays;
        subProcessor.editors = editors;
        subProcessor.currentCategory = new ProcessedCategory(field.getName(), option.name(), option.desc());
        ConfigProcessorDriver.processCategory(overlay, field.getType(), subProcessor);
        processedOverlays.put(overlay, subProcessor.currentCategory.options);
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

    public LinkedHashMap<String, ProcessedCategory> getAllCategories() {
        return this.categories;
    }

    public Map<Overlay, List<ProcessedOption>> getOverlayOptions() {
        return processedOverlays;
    }
}
