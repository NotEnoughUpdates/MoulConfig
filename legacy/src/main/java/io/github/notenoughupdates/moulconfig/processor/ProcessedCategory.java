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

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessedCategory {
    public final String name;
    public final String desc;
    public final Field reflectField;
    public final List<ProcessedOption> options = new ArrayList<>();
    public final Map<Integer, ProcessedOption> accordionAnchors = new HashMap<>();

    public @Nullable String parent;

    public String getIdentifier() {
        return reflectField.toString();
    }

    public ProcessedCategory(Field field, String name, String desc) {
        this(field, name, desc, null);
    }

    public ProcessedCategory(Field field, String name, String desc, @Nullable String parent) {
        this.reflectField = field;
        this.name = name;
        this.parent = parent;
        this.desc = desc;
    }
}
