package io.github.moulberry.moulconfig.processor;

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

import java.lang.reflect.Field;

public interface ConfigStructureReader {

    default void beginConfig(Class<? extends Config> configClass, Config configObject) {
    }

    default void endConfig() {
    }

    void beginCategory(Object baseObject, Field field, String name, String description);

    void endCategory();

    void beginAccordion(Object baseObject, Field field, ConfigOption option, int id);

    void endAccordion();
    void emitOption(Object baseObject, Field field, ConfigOption option);

    void emitGuiOverlay(Object baseObject, Field field, ConfigOption option);

}
