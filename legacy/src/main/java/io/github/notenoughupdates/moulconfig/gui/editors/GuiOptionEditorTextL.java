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

package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.gui.elements.GuiElementTextField;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiOptionEditorTextL extends GuiOptionEditor {
    private final GuiElementTextField textField;

    public GuiOptionEditorTextL(ProcessedOption option) {
        super(option);

        textField = new GuiElementTextField((String) option.get(), 0);
    }

    @Override
    public void render(RenderContext renderContext, int x, int y, int width) {
        super.render(renderContext, x, y, width);
        int height = getHeight();

        int fullWidth = Math.min(width / 3 - 10, 80);

        int textFieldX = x + width / 6 - fullWidth / 2;
        if (textField.getFocus()) {
            fullWidth = Math.max(
                fullWidth,
                Minecraft.getMinecraft().fontRendererObj.getStringWidth(textField.getText()) + 10
            );
        }

        textField.setSize(fullWidth, 16);
        textField.setText((String) option.get());

        textField.render(textFieldX, y + height - 7 - 14);
    }

    @Override
    public boolean mouseInput(int x, int y, int width, int mouseX, int mouseY, MouseEvent mouseEvent) {
        if (mouseEvent instanceof MouseEvent.Move) {
            textField.mouseMoved(mouseX, mouseY);
            return false;
        }
        return super.mouseInput(x, y, width, mouseX, mouseY, mouseEvent);
    }

    @Override
    public boolean mouseInput(int x, int y, int width, int mouseX, int mouseY) {
        int height = getHeight();

        int fullWidth = Math.min(width / 3 - 10, 80);

        int textFieldX = x + width / 6 - fullWidth / 2;

        if (textField.getFocus()) {
            fullWidth = Math.max(
                fullWidth,
                Minecraft.getMinecraft().fontRendererObj.getStringWidth(textField.getText()) + 10
            );
        }

        int textFieldY = y + height - 7 - 14;
        textField.setSize(fullWidth, 16);

        if ((Mouse.getEventButton() == 0 || Mouse.getEventButton() == 1)) {
            if (mouseX > textFieldX && mouseX < textFieldX + fullWidth &&
                mouseY > textFieldY && mouseY < textFieldY + 16) {
                if (Mouse.getEventButtonState()) {
                    textField.mouseClicked(mouseX, mouseY, Mouse.getEventButton());
                } else {
                    textField.mouseUnclicked(mouseX, mouseY, Mouse.getEventButton());
                }

                return true;
            }
            textField.unfocus();
        }

        return false;
    }

    @Override
    public boolean keyboardInput() {
        if (!Keyboard.getEventKeyState()) return false;
        if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE && textField.getFocus()) {
            textField.unfocus();
            return true;
        }
        if (textField.getFocus()) {
            textField.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());

            try {
                textField.setCustomBorderColour(0xffffffff);
                option.set(textField.getText());
            } catch (Exception e) {
                textField.setCustomBorderColour(0xffff0000);
            }

            return true;
        }
        return false;
    }
}
