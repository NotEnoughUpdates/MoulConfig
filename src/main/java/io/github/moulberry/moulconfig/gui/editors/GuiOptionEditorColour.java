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

import io.github.moulberry.moulconfig.ChromaColour;
import io.github.moulberry.moulconfig.GuiTextures;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.gui.elements.GuiElementColour;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.opengl.GL11;

public class GuiOptionEditorColour extends GuiOptionEditor {
    private GuiElementColour colourElement = null;

    public GuiOptionEditorColour(ProcessedOption option) {
        super(option);

    }

    @Override
    public void render(DrawContext context, int x, int y, int width) {
        super.render(context, x, y, width);
        int height = getHeight();

        int argb = ChromaColour.specialToChromaRGB((String) option.get());
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        context.setShaderColor(r / 255f, g / 255f, b / 255f, 1);
        context.drawTexture(GuiTextures.BUTTON_WHITE, x + width / 6 - 24, y + height - 7 - 14, 0, 0, 48, 16, 48, 16);
        context.setShaderColor(1, 1, 1, 1);
    }

    @Override
    public void renderOverlay(DrawContext context, int x, int y, int width) {

        if (colourElement != null) {
            colourElement.render(context, (int) MinecraftClient.getInstance().mouse.getX(), (int) MinecraftClient.getInstance().mouse.getY(), 0);
        }
    }

    @Override
    public boolean mouseDragged(int finalX, int finalY, int finalWidth, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return colourElement != null && colourElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseInputOverlay(int x, int y, int width, double mouseX, double mouseY, int button) {
        return colourElement != null && colourElement.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseInput(int x, int y, int width, double mouseX, double mouseY, int button) {
        int height = getHeight();

        if (button == 0 &&
                mouseX > x + width / 6 - 24 && mouseX < x + width / 6 + 24 &&
                mouseY > y + height - 7 - 14 && mouseY < y + height - 7 + 2) {
            colourElement = new GuiElementColour((int) mouseX, (int) mouseY, (String) option.get(), option::set, () -> colourElement = null);
        }

        return false;
    }

    @Override
    public boolean keyboardInput(int keyCode, int scanCode, int modifiers) {
        return colourElement != null && colourElement.keyPressed(keyCode, scanCode, modifiers);
    }
}
