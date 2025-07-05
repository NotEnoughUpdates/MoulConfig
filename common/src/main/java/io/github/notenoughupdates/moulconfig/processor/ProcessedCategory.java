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

import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor;
import io.github.notenoughupdates.moulconfig.internal.Warnings;
import lombok.var;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ProcessedCategory extends HasDebugLocation {
    String getDisplayName();

    String getDescription();

    String getIdentifier();

    @Nullable String getParentCategoryId();

    @Unmodifiable
    List<? extends ProcessedOption> getOptions();

    @Unmodifiable
    Map<? extends Integer, ? extends ProcessedOption> getAccordionAnchors();

    /**
     * Collect a list of categories into a map that can be used by {@link MoulConfigEditor#MoulConfigEditor(LinkedHashMap, Config)}.
     * Also checks that all ids are unique and all categories with a parent are ordered correctly
     */
    static <T extends ProcessedCategory> @Unmodifiable LinkedHashMap<String, T> collect(Iterable<? extends T> categories) {
        var map = new LinkedHashMap<String, T>();
        String lastParentId = null;
        for (T category : categories) {
            if (map.containsKey(category.getIdentifier())) {
                Warnings.warn("Category list contains multiple categories with identifier " + category.getIdentifier());
            }
            if (category.getParentCategoryId() == null) {
                lastParentId = category.getParentCategoryId();
            } else {
              if (!category.getParentCategoryId().equals(lastParentId)) {
                  Warnings.warn("Out of order child category " + category + " has parent with id " + category.getParentCategoryId() + " but the last parent was " + lastParentId);
              }
            }
            map.put(category.getIdentifier(), category);
        }
        return map;
    }

}
