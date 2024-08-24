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

package io.github.notenoughupdates.moulconfig.gui;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * A GuiContext manages focus and global state of a collection of {@link GuiComponent gui elements}.
 */
@Getter
@ToString
@Setter
public class GuiContext {
    /**
     * The root element of this GuiContext
     */
    public final GuiComponent root;
    public GuiComponent focusedElement;
    public List<FloatingGuiElement> floatingWindows = new ArrayList<>();
    public Runnable closeRequestHandler;

    public void setFocusedElement(GuiComponent focusedElement) {
        if (this.focusedElement == focusedElement) return;
        GuiComponent oldElement = this.focusedElement;
        if (oldElement != null)
            oldElement.onLostFocus();
        if (focusedElement != null)
            focusedElement.onGainedFocus();
        this.focusedElement = focusedElement;
    }

    public GuiContext(GuiComponent root) {
        this.root = root;
        root.foldRecursive((Void) null, (guiElementNew, _void) -> {
            guiElementNew.setContext(this);
            return _void;
        });
    }

    public void onAfterClose() {
        root.foldRecursive((Void) null, (component, _void) -> {
            if (component instanceof CloseEventListener) {
                ((CloseEventListener) component).onAfterClose();
            }
            return _void;
        });
    }

    public CloseEventListener.CloseAction onBeforeClose() {
        return root.foldRecursive(CloseEventListener.CloseAction.NO_OBJECTIONS_TO_CLOSE, (component, action) -> {
            if (component instanceof CloseEventListener) {
                return ((CloseEventListener) component).onBeforeClose().or(action);
            }
            return action;
        });
    }

    public void requestClose() {
        if (closeRequestHandler != null) {
            closeRequestHandler.run();
        }
    }
}
