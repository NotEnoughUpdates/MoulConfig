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

import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor;
import io.github.notenoughupdates.moulconfig.internal.KeybindHelper;
import io.github.notenoughupdates.moulconfig.internal.RenderUtils;
import io.github.notenoughupdates.moulconfig.internal.TextRenderUtils;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Collections;

public class GuiOptionEditorKeybindL extends GuiOptionEditor {
    private final int defaultKeyCode;
    private boolean editingKeycode;

    private Pair<Integer, Integer> lastMousePosition = null;

    public GuiOptionEditorKeybindL(ProcessedOption option, int defaultKeyCode) {
        super(option);
        this.defaultKeyCode = defaultKeyCode;
    }

    @Override
    public void render(RenderContext renderContext, int x, int y, int width) {
        super.render(renderContext, x, y, width);

        int height = getHeight();

        GlStateManager.color(1, 1, 1, 1);
        IMinecraft.instance.bindTexture(GuiTextures.BUTTON);
        RenderUtils.drawTexturedRect(x + width / 6 - 24, y + height - 7 - 14, 48, 16);

        String keyName = KeybindHelper.getKeyName((int) option.get());
        String text = editingKeycode ? "> " + keyName + " <" : keyName;
        TextRenderUtils.drawStringCenteredScaledMaxWidth(text,
            Minecraft.getMinecraft().fontRendererObj,
            x + width / 6, y + height - 7 - 6,
            false, 38, 0xFF303030
        );

        int resetX = x + width / 6 - 24 + 48 + 3;
        int resetY = y + height - 7 - 14 + 3;

        IMinecraft.instance.bindTexture(GuiTextures.RESET);
        GlStateManager.color(1, 1, 1, 1);
        RenderUtils.drawTexturedRect(resetX, resetY, 10, 11, GL11.GL_NEAREST);
        // TODO: make use of the mouseX and mouseY from the context when switching this to a proper multi-version component
        if (lastMousePosition != null &&
            lastMousePosition.getFirst() >= resetX && lastMousePosition.getFirst() < resetX + 10 &&
            lastMousePosition.getSecond() >= resetY && lastMousePosition.getSecond() < resetY + 11) {
            renderContext.scheduleDrawTooltip(Collections.singletonList(
                "§cReset to Default"
            ));
        }
    }

    @Override
    public boolean mouseInput(int x, int y, int width, int mouseX, int mouseY) {
        lastMousePosition = new Pair<>(mouseX, mouseY);
        if (Mouse.getEventButtonState() && Mouse.getEventButton() != -1 && editingKeycode) {
            editingKeycode = false;
            option.set(Mouse.getEventButton() - 100);
            return true;
        }

        if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0) {
            int height = getHeight();
            if (mouseX > x + width / 6 - 24 && mouseX < x + width / 6 + 24 &&
                mouseY > y + height - 7 - 14 && mouseY < y + height - 7 + 2) {
                editingKeycode = true;
                return true;
            }
            if (mouseX > x + width / 6 - 24 + 48 + 3 && mouseX < x + width / 6 - 24 + 48 + 13 &&
                mouseY > y + height - 7 - 14 + 3 && mouseY < y + height - 7 - 14 + 3 + 11) {
                option.set(defaultKeyCode);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyboardInput() {
        if (editingKeycode) {
            editingKeycode = false;
            int keyCode = 0;
            if (Keyboard.getEventKey() != Keyboard.KEY_ESCAPE && Keyboard.getEventKey() != 0) {
                keyCode = Keyboard.getEventKey();
            }
            if (keyCode > 256) keyCode = 0;
            option.set(keyCode);
            return true;
        }
        return false;
    }
}
