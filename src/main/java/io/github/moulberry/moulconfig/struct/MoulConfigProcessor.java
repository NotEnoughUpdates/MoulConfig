package io.github.moulberry.moulconfig.struct;

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.annotations.Category;
import io.github.moulberry.moulconfig.annotations.ConfigEditorAccordion;
import io.github.moulberry.moulconfig.annotations.ConfigOption;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;

public class MoulConfigProcessor<T extends Config> implements ConfigStructureReader {

    private final T configBaseObject;
    private final LinkedHashMap<String, ProcessedCategory> categories = new LinkedHashMap<>();

    private ProcessedCategory currentCategory;
    private Object currentCategoryObject;
    private Stack<Integer> accordion = new Stack<>();
    private Map<Class<? extends Annotation>, BiFunction<ProcessedOption, Annotation, GuiOptionEditor>> editors = new HashMap<>();

    public MoulConfigProcessor(T configBaseObject) {
        this.configBaseObject = configBaseObject;
    }

    public <A extends Annotation> void registerConfigEditor(Class<A> annotation, BiFunction<ProcessedOption, ? extends A, GuiOptionEditor> editorGenerator) {
        editors.put(annotation, (BiFunction<ProcessedOption, Annotation, GuiOptionEditor>) editorGenerator);
    }

    @Override
    public void beginCategory(Field field, Category category) {
        currentCategory = new ProcessedCategory(field.getName(), category.name(), category.desc());
        categories.put(field.getName(), currentCategory);
        try {
            currentCategoryObject = field.get(configBaseObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public T getConfigObject() {
        return configBaseObject;
    }


    @Override
    public void endCategory() {
    }

    @Override
    public void beginAccordion(Field field, ConfigOption option, ConfigEditorAccordion accordion) {
        emitOption(field, option);
        this.accordion.push(accordion.id());
    }

    @Override
    public void endAccordion() {
        accordion.pop();
    }

    @Override
    public void emitOption(Field field, ConfigOption option) {
        ProcessedOption processedOption = createProcessedOption(field, option);
        GuiOptionEditor optionGui = createOptionGui(processedOption, field, option);
        if (optionGui == null) {
            new Error("Warning: Could not create GUI Option Editor for " + field + " in " + currentCategoryObject.getClass()).printStackTrace();
            return;
        }
        processedOption.editor = optionGui;
        if (!accordion.isEmpty()) {
            processedOption.accordionId = accordion.peek();
        }
        currentCategory.options.put(field.getName(), processedOption);
    }

    protected ProcessedOption createProcessedOption(Field field, ConfigOption option) {
        return new ProcessedOption(
            option.name(), option.desc(),
            option.subcategoryId(),
            field,
            currentCategoryObject,
            configBaseObject
        );
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
}
