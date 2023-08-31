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

import net.minecraft.client.gui.DrawContext;

public abstract class GuiElement {

    public abstract void render(DrawContext context, int mouseX, int mouseY, float delta);

    public abstract boolean mouseClicked(double mouseX, double mouseY, int button);

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    public void mouseMoved(double mouseX, double mouseY) {}

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
}
