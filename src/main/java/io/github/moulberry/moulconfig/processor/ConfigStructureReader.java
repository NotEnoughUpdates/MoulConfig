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

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

import java.lang.reflect.Field;

public interface ConfigStructureReader {

    default void pushPath(String fieldPath) {
    }

    default void popPath() {
    }

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
