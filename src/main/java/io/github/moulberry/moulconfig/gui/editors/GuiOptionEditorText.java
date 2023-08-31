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

package io.github.moulberry.moulconfig.gui.editors;

import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.gui.elements.GuiElementTextField;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;

public class GuiOptionEditorText extends GuiOptionEditor {
    private final GuiElementTextField textField;

    public GuiOptionEditorText(ProcessedOption option) {
        super(option);

        textField = new GuiElementTextField((String) option.get(), 0);
    }

    @Override
    public void render(DrawContext context, int x, int y, int width) {
        super.render(context, x, y, width);
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int height = getHeight();

        int fullWidth = Math.min(width / 3 - 10, 80);

        int textFieldX = x + width / 6 - fullWidth / 2;
        if (textField.getFocus()) {
            fullWidth = Math.max(
                fullWidth,
                textRenderer.getWidth(textField.getText()) + 10
            );
        }

        textField.setSize(fullWidth, 16);
        textField.setText((String) option.get());

        textField.render(context, textFieldX, y + height - 7 - 14);
    }

    @Override
    public boolean mouseInput(int x, int y, int width, double mouseX, double mouseY, int button) {
        int height = getHeight();

        int fullWidth = Math.min(width / 3 - 10, 80);

        int textFieldX = x + width / 6 - fullWidth / 2;

        if (textField.getFocus()) {
            fullWidth = Math.max(
                fullWidth,
                MinecraftClient.getInstance().textRenderer.getWidth(textField.getText()) + 10
            );
        }

        int textFieldY = y + height - 7 - 14;
        textField.setSize(fullWidth, 16);

        if (button == 0 || button == 1) {
            if (mouseX > textFieldX && mouseX < textFieldX + fullWidth &&
                mouseY > textFieldY && mouseY < textFieldY + 16) {
                textField.mouseClicked(mouseX, mouseY, button);
                return true;
            }
            textField.unfocus();
        }

        return false;
    }

    @Override
    public boolean keyboardInput(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputUtil.GLFW_KEY_ENTER && textField.getFocus()) {
            textField.unfocus();
            return true;
        }
        if (textField.getFocus()) {
            textField.keyTyped(keyCode, scanCode, modifiers);

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
