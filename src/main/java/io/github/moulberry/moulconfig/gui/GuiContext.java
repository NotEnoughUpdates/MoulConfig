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

package io.github.moulberry.moulconfig.gui;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * A GuiContext manages focus and global state of a collection of {@link GuiElementNew gui elements}.
 */
@Getter
@ToString
@Setter
public class GuiContext {
    /**
     * The root element of this GuiContext
     */
    public final GuiElementNew root;
    public GuiElementNew focusedElement;
    public List<FloatingGuiElement> floatingWindows = new ArrayList<>();

    public GuiContext(GuiElementNew root) {
        this.root = root;
        root.foldRecursive((Void) null, (guiElementNew, _void) -> {
            guiElementNew.setContext(this);
            return _void;
        });
    }
}