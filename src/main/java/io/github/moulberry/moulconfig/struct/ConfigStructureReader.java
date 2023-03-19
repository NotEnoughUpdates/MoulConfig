package io.github.moulberry.moulconfig.struct;

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.annotations.Category;
import io.github.moulberry.moulconfig.annotations.ConfigEditorAccordion;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

import java.lang.reflect.Field;

public interface ConfigStructureReader {

    default void beginConfig(Class<? extends Config> configClass) {
    }

    default void endConfig() {
    }

    void beginCategory(Field field, Category category);

    void endCategory();

    void beginAccordion(Field field, ConfigOption option, int id);

    void endAccordion();
    void emitOption(Field field, ConfigOption option);

}
