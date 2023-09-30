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

import io.github.moulberry.moulconfig.internal.ForgeRenderContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

@RequiredArgsConstructor
@ToString
public class GuiScreenElementWrapperNew extends GuiScreen {
    @NonNull
    public GuiContext context;

    GuiImmediateContext createContext() {
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        return new GuiImmediateContext(
            new ForgeRenderContext(), 0, 0,
            width, height,
            x, y, x, y
        );
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();

        context.getRoot().keyboardEvent(new KeyboardEvent.KeyPressed(Keyboard.getEventKey(), Keyboard.getEventKeyState()), createContext());
        context.getRoot().keyboardEvent(new KeyboardEvent.CharTyped(Keyboard.getEventCharacter()), createContext());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ForgeRenderContext frc = new ForgeRenderContext();
        context.getRoot().render(new GuiImmediateContext(
            frc,
            0, 0, width, height, mouseX, mouseY, mouseX, mouseY
        ));
        frc.doDrawTooltip();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (Mouse.getEventButton() != -1) {
            context.getRoot().mouseEvent(new MouseEvent.Click(Mouse.getEventButton(), Mouse.getEventButtonState()), createContext());
        }
        if (Mouse.getEventDWheel() != 0) {
            context.getRoot().mouseEvent(new MouseEvent.Scroll((float) Mouse.getEventDWheel()), createContext());
        }
        if (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0) {
            context.getRoot().mouseEvent(new MouseEvent.Move((float) Mouse.getEventDX(), (float) Mouse.getEventDY()), createContext());
        }
    }
}
