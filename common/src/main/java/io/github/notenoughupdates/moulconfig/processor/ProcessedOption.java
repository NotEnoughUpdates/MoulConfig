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
import io.github.notenoughupdates.moulconfig.annotations.SearchTag;
import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor;
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorAccordion;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.observer.Property;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface ProcessedOption extends HasDebugLocation {
    SearchTag[] getSearchTags();

    int getAccordionId();

    GuiOptionEditor getEditor();

    ProcessedCategory getCategory();

    String getName();

    String getDescription();

    String getPath();

    Config getConfig();

    Object get();

    Type getType();

    boolean set(Object value);

    void explicitNotifyChange();

    interface HasField {
        Field getField();
    }

    default GetSetter<?> intoProperty() {
        return new GetSetter<Object>() {
            @Override
            public Object get() {
                return ProcessedOption.this.get();
            }

            @Override
            public void set(Object newValue) {
                ProcessedOption.this.set(newValue);
            }
        };
    }

}
