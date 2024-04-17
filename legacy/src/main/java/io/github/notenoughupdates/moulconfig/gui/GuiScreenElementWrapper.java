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

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiScreenElementWrapper extends GuiScreen {
    public final GuiElement element;

    public GuiScreenElementWrapper(GuiElement element) {
        this.element = element;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        element.render();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        if (Mouse.getEventButton() != -1) {
            element.mouseInput(i, j, new MouseEvent.Click(Mouse.getEventButton(), Mouse.getEventButtonState()));
        }
        if (Mouse.getEventDWheel() != 0) {
            element.mouseInput(i, j, new MouseEvent.Scroll((float) Mouse.getEventDWheel()));
        }
        if (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0) {
            element.mouseInput(i, j, new MouseEvent.Move((float) Mouse.getEventDX(), (float) Mouse.getEventDY()));
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        if (element.keyboardInput(new KeyboardEvent.KeyPressed(Keyboard.getEventKey(), Keyboard.getEventKeyState())))
            return;
        if (Keyboard.getEventKeyState() && !Character.isISOControl(Keyboard.getEventCharacter()))
            if (element.keyboardInput(new KeyboardEvent.CharTyped(Keyboard.getEventCharacter()))) return;
        super.handleKeyboardInput();
    }
}
