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

package io.github.notenoughupdates.moulconfig.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Used for draggable lists. The field associated with this option may only be of type {@link List List<T>}.
 * That {@code T} may only be either an {@code int}, or an enum. If an {@code int} is used, {@link #exampleText()}
 * needs to be provided, otherwise it needs to be kept empty.
 * <p>
 * This option allows the user to add options out of the list of enums or example texts, reorder them and delete elements.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEditorDraggableList {
    /**
     * Deprecated feature. Use an enum type instead.
     */
    @Deprecated
    String[] exampleText() default {};

    /**
     * @return set to true to disable deleting items when the list only has one element left. irrelevant if you {@link #allowDeleting()} is set.
     */
    boolean requireNonEmpty() default false;

    /**
     * If true, never cache the exampleText map and
     * always call element.toString() at render‐time.
     */
    boolean dynamicToString() default false;

    /**
     * @return set to false to disable deleting items from the list
     */
    boolean allowDeleting() default true;
}
