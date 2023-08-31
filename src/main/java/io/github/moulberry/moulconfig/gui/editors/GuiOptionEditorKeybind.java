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

import io.github.moulberry.moulconfig.GuiTextures;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.internal.TextRenderUtils;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class GuiOptionEditorKeybind extends GuiOptionEditor {
    private final int defaultKeyCode;
    private boolean editingKeycode;

    public GuiOptionEditorKeybind(ProcessedOption option, int defaultKeyCode) {
        super(option);
        this.defaultKeyCode = defaultKeyCode;
    }

    @Override
    public void render(DrawContext context, int x, int y, int width) {
        super.render(context, x, y, width);

        int height = getHeight();

        context.drawTexture(GuiTextures.BUTTON, x + width / 6 - 24, y + height - 7 - 14, 0, 0, 48, 16, 48, 16);

        Text keyName = InputUtil.fromKeyCode((int) option.get(), 0).getLocalizedText();
        Text text = editingKeycode ? Text.literal("> ").append(keyName).append(Text.literal(" <")) : keyName;
        TextRenderUtils.drawStringCenteredScaledMaxWidth(text,
                context,
                x + (float) width / 6, y + height - 7 - 6,
                false, 40, 0xFF303030
        );

        context.drawTexture(GuiTextures.RESET, x + width / 6 - 24 + 48 + 3, y + height - 7 - 14 + 3, 0, 0, 10, 11, 10, 11);
    }

    @Override
    public boolean mouseInput(int x, int y, int width, double mouseX, double mouseY, int button) {
        if (button != -1 && editingKeycode) {
            editingKeycode = false;
            option.set(button - 100);
            return true;
        }

        if (button == 0) {
            int height = getHeight();
            if (mouseX > x + (double) width / 6 - 24 && mouseX < x + (double) width / 6 + 24 &&
                    mouseY > y + height - 7 - 14 && mouseY < y + height - 7 + 2) {
                editingKeycode = true;
                return true;
            }
            if (mouseX > x + (double) width / 6 - 24 + 48 + 3 && mouseX < x + (double) width / 6 - 24 + 48 + 13 &&
                    mouseY > y + height - 7 - 14 + 3 && mouseY < y + height - 7 - 14 + 3 + 11) {
                option.set(defaultKeyCode);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyboardInput(int keyCode, int scanCode, int modifiers) {
        if (editingKeycode) {
            editingKeycode = false;
            if (keyCode > 256) keyCode = 0;
            option.set(keyCode);
            return true;
        }
        return false;
    }
}
