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

package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * A gui element which renders a string in a single line
 */
@RequiredArgsConstructor
public class TextComponent extends GuiComponent {
    final IFontRenderer fontRenderer;
    final Supplier<String> string;
    final int suggestedWidth;
    final TextAlignment alignment;
    final boolean shadow;
    final boolean split;
    private String lastString;
    private int lastWidth = -1;
    private List<String> lastSplit;
    private static final Pattern colorPattern = Pattern.compile("§[a-f0-9r]");


    public TextComponent(String string, int width) {
        this(IMinecraft.instance.getDefaultFontRenderer(), () -> string, width, TextAlignment.LEFT, false, false);
    }

    public TextComponent(String string) {
        this(string, IMinecraft.instance.getDefaultFontRenderer().getStringWidth(string));
    }

    @Override
    public int getWidth() {
        return suggestedWidth + 4;
    }

    @Override
    public int getHeight() {
        return 2 + (fontRenderer.getHeight() + 2) * split(string.get(), getWidth()).size();
    }

    public List<String> split(String text, int width) {
        if (!split) return Collections.singletonList(text);
        if (Objects.equals(text, lastString) && width == lastWidth)
            return lastSplit;
        lastString = text;
        lastWidth = width;
        lastSplit = fontRenderer.splitText(text, width);
        return lastSplit;
    }

    @Override
    public void render(GuiImmediateContext context) {
        context.getRenderContext().pushMatrix();
        List<String> lines = split(string.get(), context.getWidth());
        for (String line : lines) {
            int length = fontRenderer.getStringWidth(line);
            if (length > context.getWidth()) {
                context.getRenderContext().drawStringScaledMaxWidth(line, fontRenderer, 2, 2, shadow, context.getWidth(), -1);
            } else switch (alignment) {
                case LEFT:
                    context.getRenderContext().drawString(fontRenderer, line, 2, 2, -1, shadow);
                    break;
                case CENTER:
                    context.getRenderContext().drawString(fontRenderer, line, context.getWidth() / 2 - length / 2 + 2, 2, -1, shadow);
                    break;
                case RIGHT:
                    context.getRenderContext().drawString(fontRenderer, line, context.getWidth() - length + 2, 2, -1, shadow);
                    break;
            }
            context.getRenderContext().translate(0, fontRenderer.getHeight() + 2, 0);
        }
        context.getRenderContext().popMatrix();
    }

    public enum TextAlignment {
        LEFT, CENTER, RIGHT;
    }
}
