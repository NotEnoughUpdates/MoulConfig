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
import io.github.moulberry.moulconfig.common.IMinecraft;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.gui.elements.GuiElementColour;
import io.github.moulberry.moulconfig.internal.RenderUtils;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import lombok.val;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Type;

public class GuiOptionEditorColour extends GuiOptionEditor {
    private GuiElementColour colourElement = null;
    private final boolean usesString;

    public GuiOptionEditorColour(ProcessedOption option) {
        super(option);
        Type type = option.getType();
        if (type.equals(String.class)) {
            usesString = true;
        } else if (type.equals(ChromaColour.class)) {
            usesString = false;
        } else {
            throw new IllegalArgumentException("ConfigEditorColour may only be used on a String or ChromaColour field, but is used on " + option.field);
        }
    }

    ChromaColour get() {
        val value = option.get();
        if (usesString)
            //noinspection deprecation
            return ChromaColour.forLegacyString((String) value);
        return (ChromaColour) value;
    }

    void set(String newString) {
        if (usesString) {
            option.set(newString);
        } else {
            //noinspection deprecation
            option.set(ChromaColour.forLegacyString(newString));
        }
    }

    @Override
    public void render(int x, int y, int width) {
        super.render(x, y, width);
        int height = getHeight();

        int argb = get().getEffectiveColour().getRGB();
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        GlStateManager.color(r / 255f, g / 255f, b / 255f, 1);
        IMinecraft.instance.bindTexture(GuiTextures.BUTTON_WHITE);
        RenderUtils.drawTexturedRect(x + width / 6 - 24, y + height - 7 - 14, 48, 16);
    }

    @Override
    public void renderOverlay(int x, int y, int width) {
        if (colourElement != null) {
            colourElement.render();
        }
    }

    @Override
    public boolean mouseInputOverlay(int x, int y, int width, int mouseX, int mouseY) {
        return colourElement != null && colourElement.mouseInput(mouseX, mouseY);
    }

    @Override
    public boolean mouseInput(int x, int y, int width, int mouseX, int mouseY) {
        int height = getHeight();

        if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0 &&
            mouseX > x + width / 6 - 24 && mouseX < x + width / 6 + 24 &&
            mouseY > y + height - 7 - 14 && mouseY < y + height - 7 + 2) {
            colourElement = new GuiElementColour(mouseX, mouseY, get().toLegacyString(), this::set, () -> colourElement = null);
        }

        return false;
    }

    @Override
    public boolean keyboardInput() {
        return colourElement != null && colourElement.keyboardInput();
    }
}
