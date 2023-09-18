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

package io.github.moulberry.moulconfig.gui.component;

import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.internal.TextRenderUtils;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.function.Supplier;

/**
 * A gui element which renders a string in a single line
 */
@AllArgsConstructor
public class TextComponent extends GuiComponent {
    final FontRenderer fontRenderer;
    final Supplier<String> string;
    final int width;
    final TextAlignment alignment;
    final boolean shadow;

    public TextComponent(String string, int width) {
        this(Minecraft.getMinecraft().fontRendererObj, () -> string, width, TextAlignment.LEFT, false);
    }
    public TextComponent(String string) {
        this(string, Minecraft.getMinecraft().fontRendererObj.getStringWidth(string));
    }

    @Override
    public int getWidth() {
        return width + 4;
    }

    @Override
    public int getHeight() {
        return fontRenderer.FONT_HEIGHT + 4;
    }

    @Override
    public void render(GuiImmediateContext context) {
        String text = string.get();
        int length = fontRenderer.getStringWidth(text);
        if (length > width) {
            TextRenderUtils.drawStringScaledMaxWidth(text, fontRenderer, 2, 2, shadow, width, -1);
        }
        switch (alignment) {
            case LEFT:
                fontRenderer.drawString(text, 2, 2, -1, shadow);
                break;
            case CENTER:
                fontRenderer.drawString(text, width / 2 - length / 2 + 2, 2, -1, shadow);
                break;
            case RIGHT:
                fontRenderer.drawString(text, width - length + 2, 2, -1, shadow);
                break;
        }
    }

    public enum TextAlignment {
        LEFT, CENTER, RIGHT;
    }
}
