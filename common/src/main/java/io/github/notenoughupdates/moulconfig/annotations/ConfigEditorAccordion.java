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

/**
 * Legacy annotation for creating {@link Accordion accordions}.
 * To use annotate a field in your category with this annotation. The content and type of the field do not matter,
 * then place all the fields that should be within this collapsible accordion directly after that field and annotate
 * the child fields with {@link ConfigAccordionId}.
 * The label for the accordion are taken from a {@link ConfigOption} annotation on that same stub field.
 *
 * <pre>
 *  {@code
 *  \@ConfigEditorAccordion(id = 10)
 *  \@ConfigOption(name = "Accordion name", desc = "")
 *  public boolean accordionField;
 *
 *  \@ConfigAccordionId(id = 10)
 *  \@ConfigOption(name = "Child Option Name", desc = "Child Option Description")
 *  \@ConfigEditorBoolean
 *  public boolean childField = true;
 *  }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Deprecated
public @interface ConfigEditorAccordion {
    int id();
}
