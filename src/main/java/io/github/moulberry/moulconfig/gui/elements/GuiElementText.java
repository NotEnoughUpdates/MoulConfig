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

package io.github.moulberry.moulconfig.gui.elements;

import io.github.moulberry.moulconfig.gui.GuiElementNew;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.internal.TextRenderUtils;
import lombok.AllArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Supplier;

/**
 * A gui element which renders a string in a single line
 */
@AllArgsConstructor
public class GuiElementText extends GuiElementNew {
    final TextRenderer textRenderer;
    final Supplier<String> string;
    final int width;
    final TextAlignment alignment;
    final boolean shadow;

    public GuiElementText(String string, int width) {
        this(MinecraftClient.getInstance().textRenderer, () -> string, width, TextAlignment.LEFT, false);
    }
    public GuiElementText(String string) {
        this(string, MinecraftClient.getInstance().textRenderer.getWidth(string));
    }

    @Override
    public int getWidth() {
        return width + 4;
    }

    @Override
    public int getHeight() {
        return textRenderer.fontHeight + 4;
    }

    @Override
    public void render(DrawContext drawContext, GuiImmediateContext context) {
        String text = string.get();
        int length = textRenderer.getWidth(text);
        if (length > width) {
            TextRenderUtils.drawStringScaledMaxWidth(text, drawContext, 2, 2, shadow, width, -1);
        }
        switch (alignment) {
            case LEFT -> drawContext.drawText(textRenderer, text, 2, 2, -1, shadow);
            case CENTER -> drawContext.drawText(textRenderer, text, width / 2 - length / 2 + 2, 2, -1, shadow);
            case RIGHT -> drawContext.drawText(textRenderer, text, width - length + 2, 2, -1, shadow);
        }
    }

    public enum TextAlignment {
        LEFT, CENTER, RIGHT;
    }
}
